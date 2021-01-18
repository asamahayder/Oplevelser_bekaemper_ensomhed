package group24.oplevelserbekaemperensomhed.logic.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import group24.oplevelserbekaemperensomhed.data.DateDTO
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.view.search.ActivitySearch
import java.util.*
import kotlin.collections.ArrayList

class FirebaseDAO{

    private var uploadImageCounter = 0
    private var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = firebaseStorage.reference

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Fetches all events from the database and returns them as a list of DBEvents
    private fun getAllEventsFromDB(callBack: MyCallBack) {
        val dbEvents = ArrayList<DBEvent>()
        db.collection("events")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!){
                        val dbEvent = document.toObject(DBEvent::class.java)
                        dbEvents.add(dbEvent)
                    }
                    callBack.onCallBack(dbEvents)
                }
            }
    }

    //Gets a single user from database based on a user id
    fun getUser(id: String, callBack: MyCallBack) {
        db.collection("users").document(id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dbUser = task.result!!.toObject(DBUser::class.java)
                    if (dbUser != null) {
                        val user = UserDTO(dbUser.name,dbUser.age,dbUser.address,dbUser.occupation,dbUser.education,dbUser.about,dbUser.gender,ArrayList(),dbUser.profilePictures.toCollection(ArrayList<String>()))
                        callBack.onCallBack(user)
                    } else {
                        callBack.onCallBack("failure")
                    }
                } else {
                    callBack.onCallBack("failure")
            }
            }
    }


    //Fetches all current promotional banners from database
    fun getAllBanners(callBack: MyCallBack) {
        val dbBanners = ArrayList<DBBanner>()
        db.collection("banners")
            .get()
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val dbBanner = document.toObject(DBBanner::class.java)
                        dbBanners.add(dbBanner)
                    }
                    callBack.onCallBack(dbBanners)
                }
            }
    }


    //Fetches all events from database and returns them as a list of EventDTO
    fun getEvents(callBack: MyCallBack){
        val list : ArrayList<EventDTO> = ArrayList()
        getAllEventsFromDB(object : MyCallBack {
            override fun onCallBack(`object`: Any) {
                val eventDataList = `object` as ArrayList<DBEvent>
                for (dbEvent in eventDataList) {
                    getUser(dbEvent.eventCreator, object : MyCallBack {
                        override fun onCallBack(`object`: Any) {
                            try {
                                val user = `object` as UserDTO
                                val event = EventDTO(user,null,dbEvent.eventDescription,dbEvent.eventTitle,
                                    DateDTO(dbEvent.eventDate[0],dbEvent.eventDate[1],dbEvent.eventDate[2]),dbEvent.eventLikes,dbEvent.category,dbEvent.address,dbEvent.price,dbEvent.pictures.toCollection(ArrayList()))
                                list.add(event)
                                callBack.onCallBack(list)
                            }catch (e: ClassCastException){
                                return
                            }
                        }
                    })
                }
            }
        })
    }


    //Creates an event inside the database
    fun createEvent(event: EventDTO, callBack: MyCallBack){
        val date: List<String> = listOf(event.eventDate.date, event.eventDate.startTime, event.eventDate.endTime)
        val pictures: List<String> = event.pictures

        val participants: List<String> = listOf(event.participants?.get(0)?.name.toString())

        val localData = LocalData
        val userID = localData.id

        val dbEvent = DBEvent(address = event.address, category = event.category, eventCreator = userID, eventDate = date, eventDescription = event.eventDescription, eventLikes = event.eventLikes, eventTitle = event.eventTitle, price = event.price, pictures = pictures, participants = participants)
        db.collection("events").add(dbEvent).addOnSuccessListener {
            createParticipantPair(dbEvent.eventTitle, callBack) //we add the owner as a participant to his own event
        }.addOnFailureListener{
            callBack.onCallBack("Failure")
        }
    }


    //Create a user in the database
    fun createUser(dbUser: DBUser, uid: String, callBack: MyCallBack) {
        db.collection("users").document(uid).set(dbUser)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    callBack.onCallBack("success")
                }
            }
            .addOnFailureListener {

            }
    }


    //Upload images to the database
    fun uploadImages(imageList: ArrayList<Uri>, myListener: MyUploadPicturesListener) {
        val pictureDownloadLinks: ArrayList<String> = ArrayList()
        //Inspired by code from following site: https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/

        uploadImageCounter = imageList.size //The counter is used to check if all threads are finished.
        for (imageUri in imageList) {
            val ref: StorageReference = storageReference.child("images/" + UUID.randomUUID().toString())
            val uploadTask = ref.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                // Continue with the task to get the download URL
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    pictureDownloadLinks.add(downloadUri.toString())
                    //Every thread decrements counter and checks if 0. This is to ensure that only the last thread that finishes continues.
                    //This is a mechanism that we have implemented to sync threads.
                    uploadImageCounter--
                    if (uploadImageCounter == 0) {
                        //The database creates a download url for each picture after it is uploaded. We save this in the event so we can download later.
                        myListener.onSuccess(pictureDownloadLinks)
                    } else {
                        myListener.onProgress(uploadImageCounter)
                    }
                } else {
                    myListener.onFailure("Update failed")
                }
            }
        }
    }


    //Deletes user from database
    fun deleteUser(id: String,callBack: MyCallBack) {
        deleteParticipantsByUser(object: MyCallBack{ //First we delete all the user's participations
            override fun onCallBack(`object`: Any) {
                val message = `object` as String
                if (message.equals("success")){
                    deleteAllCreatedEvents(object: MyCallBack{ //Then we delete all the user's created events
                        override fun onCallBack(`object`: Any) {
                            val message2 = `object` as String
                            if (message2.equals("success")){
                                //Then we delete the user
                                db.collection("users").document(id).delete().addOnCompleteListener {task ->
                                    if (task.isSuccessful) {
                                        callBack.onCallBack(true)
                                    }
                                    else {
                                        callBack.onCallBack(false)
                                    }
                                }
                            }else{
                                callBack.onCallBack(false)
                            }
                        }
                    })
                }else{
                    callBack.onCallBack(false)
                }
            }
        })

    }

    //This handles when a user joins an event. A Participation pair is a data object consisting of 2 fields: eventName and userID.
    //Everytime someone joins an event, a pair is created.
    //By fetching pairs, we can easily determine an event's participants
    fun createParticipantPair(eventTitle: String, callBack: MyCallBack){
        val localData = LocalData
        val userID = localData.id
        val pair = DBEventParticipantPair(eventTitle, userID)
        db.collection("eventParticipants").add(pair).addOnCompleteListener{
            task ->
            if (task.isSuccessful){
                callBack.onCallBack("success")
            }else{
                callBack.onCallBack("Fail")
            }
        }
    }

    //Handles leaving an event.
    fun deleteParticipantPair(eventTitle: String, callBack: MyCallBack){
        val localData = LocalData
        val userID = localData.id
        db.collection("eventParticipants").whereEqualTo("eventName", eventTitle).whereEqualTo("participant", userID).get().addOnCompleteListener{
            task ->
            if (task.isSuccessful){
                val batch = db.batch()
                for(document in task.result!!){
                    batch.delete(document.reference)
                }
                batch.commit().addOnCompleteListener{
                    task ->
                    if (task.isSuccessful){
                        callBack.onCallBack("success")
                    }else{
                        callBack.onCallBack("fail")
                    }
                }
            }else{
                callBack.onCallBack("fail")
            }
        }
    }


    //Deletes all created events
    fun deleteAllCreatedEvents(callBack: MyCallBack){
        val localData = LocalData
        val batch = db.batch()
        db.collection("events").whereEqualTo("eventCreator", localData.id).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                if (task.result.documents.isNotEmpty()){
                    for (document in task.result){
                        batch.delete(document.reference)
                    }
                    batch.commit().addOnCompleteListener {
                            task ->
                        if (task.isSuccessful){
                            callBack.onCallBack("success")
                        }else{
                            callBack.onCallBack("fail")
                        }
                    }
                }else{
                    callBack.onCallBack("success")
                }
            }else{
                callBack.onCallBack("fail")
            }
        }
    }


    //Deletes all participantion pairs for current user. Used to delete a user completely from database
    fun deleteParticipantsByUser(callBack: MyCallBack){
        val localData = LocalData

        //The batch lets us perform multiple operations simultaneously.
        val batch = db.batch()
        db.collection("eventParticipants").whereEqualTo("participant", localData.id).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                if (!task.result.documents.isEmpty()){
                    for (document in task.result){
                        batch.delete(document.reference)
                    }

                    batch.commit().addOnCompleteListener {
                            task ->
                        if (task.isSuccessful){
                            callBack.onCallBack("success")
                        }else{
                            callBack.onCallBack("fail")
                        }
                    }
                }else{
                    callBack.onCallBack("success")
                }

            }else{
                callBack.onCallBack("fail")
            }
        }
    }


    //Fetches all participants for a given event
    fun getParticipants(event: EventDTO, callBack: MyCallBack){
        val pairs: ArrayList<DBEventParticipantPair> = ArrayList()
        val users: ArrayList<UserDTO> = ArrayList()

        //The counter mechanism is explained in the method: uploadImages
        var counter = 0
        db.collection("eventParticipants").whereEqualTo("eventName", event.eventTitle).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                counter = task.result.size()
                for (document in task.result!!){
                    val dbPair = document.toObject(DBEventParticipantPair::class.java)
                    pairs.add(dbPair)
                }

                if (pairs.isNotEmpty()){
                    for (pair in pairs){
                        getUser(pair.participant, object : MyCallBack{
                            override fun onCallBack(`object`: Any) {
                                val user = `object` as UserDTO
                                users.add(user)
                                counter--
                                if (counter == 0){
                                    callBack.onCallBack(users)
                                }
                            }
                        })
                    }
                }else{
                    callBack.onCallBack(users)
                }

            }
        }
    }


    //Fetches all the events that the current user has created
    fun getCreatedEvents(callBack: MyCallBack){
        val localData = LocalData
        val events: ArrayList<EventDTO> = ArrayList()
        val dbEvents: ArrayList<DBEvent> = ArrayList()
        db.collection("events").whereEqualTo("eventCreator", localData.id).get().addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                for (document in task.result!!){
                    val dbEvent = document.toObject(DBEvent::class.java)
                    dbEvents.add(dbEvent)
                }

                for (dbEvent in dbEvents){
                    val event = EventDTO(localData.userData,null,dbEvent.eventDescription,dbEvent.eventTitle,
                        DateDTO(dbEvent.eventDate[0],dbEvent.eventDate[1],dbEvent.eventDate[2]),dbEvent.eventLikes,dbEvent.category,dbEvent.address,dbEvent.price,dbEvent.pictures.toCollection(ArrayList()))
                    events.add(event)
                }

                callBack.onCallBack(events)

            }else{
                callBack.onCallBack(events)
            }
        }
    }


    //Fetching all the events the current user has joined
    fun getJoinedEvents(callBack: MyCallBack){
        val localData = LocalData
        val pairs: ArrayList<DBEventParticipantPair> = ArrayList()
        val events: ArrayList<EventDTO> = ArrayList()
        var counter = 0
        db.collection("eventParticipants").whereEqualTo("participant", localData.id).get().addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                counter = task.result.size()
                for (document in task.result!!){
                    val dbPair = document.toObject(DBEventParticipantPair::class.java)
                    pairs.add(dbPair)
                }

                if (pairs.isNotEmpty()){
                    for (pair in pairs){
                        getEventByEventName(pair.eventName, object: MyCallBack{
                            override fun onCallBack(`object`: Any) {
                                val event = `object` as EventDTO

                                //filtering out a users own events
                                if (!event.eventCreator!!.name.equals(localData.userData.name)){
                                    events.add(event)
                                }
                                counter--
                                if (counter == 0){
                                    callBack.onCallBack(events)
                                }
                            }
                        })
                    }
                }else{
                    callBack.onCallBack(events)
                }

            }else{
                callBack.onCallBack(events)
            }
        }
    }


    //Gets a single event by searching for it using the eventName parameter
    fun getEventByEventName(eventName: String, callBack: MyCallBack){
        db.collection("events").whereEqualTo("eventTitle", eventName).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                if (!task.result!!.isEmpty){
                    val dbEvent = task.result.documents[0].toObject(DBEvent::class.java) //If there are multiple events with the same name, we choose the first one. This is not ideal, but we didn't have time to fix.
                    getUser(dbEvent!!.eventCreator, object : MyCallBack {
                        override fun onCallBack(`object`: Any) {
                            val user = `object` as UserDTO
                            val event = EventDTO(user, null, dbEvent.eventDescription, dbEvent.eventTitle,
                                DateDTO(dbEvent.eventDate[0], dbEvent.eventDate[1], dbEvent.eventDate[2]),
                                dbEvent.eventLikes, dbEvent.category, dbEvent.address, dbEvent.price, dbEvent.pictures.toCollection(ArrayList()))

                            callBack.onCallBack(event)
                        }
                    })
                }else{
                    callBack.onCallBack("fail")
                }
            }
        }
    }


    //Deletes an event by getting it using it's name
    fun deleteEventByEventTitle(eventTitle: String, callBack: MyCallBack){
        var counter = 0
        db.collection("events").whereEqualTo("eventTitle", eventTitle).get().addOnCompleteListener{
            task ->
            if (task.isSuccessful){
                counter = task.result.size()
                val batch = db.batch()
                for (document in task.result!!){
                    deleteAllParticipants(eventTitle, object: MyCallBack{ //Deletes all the participation pairs for the event
                        override fun onCallBack(`object`: Any) {
                            val message = `object` as String
                            if (message.equals("success")){
                                counter--
                                batch.delete(document.reference)
                                if (counter == 0){
                                    batch.commit().addOnCompleteListener{
                                            task ->
                                        if (task.isSuccessful){
                                            callBack.onCallBack("success")
                                        }else{
                                            callBack.onCallBack("fail")
                                        }
                                    }
                                }
                            }else{
                                callBack.onCallBack("fail")
                            }
                        }
                    })
                }
            }else{
                callBack.onCallBack("fail")
            }
        }

    }


    //Deleting all participants for a given eventTitle
    fun deleteAllParticipants(eventTitle: String, callBack: MyCallBack){
        db.collection("eventParticipants").whereEqualTo("eventName", eventTitle).get().addOnCompleteListener{
                task ->
            if (task.isSuccessful){
                if (!task.result.documents.isEmpty()){
                    val batch = db.batch()
                    for(document in task.result!!){
                        batch.delete(document.reference)
                    }
                    batch.commit().addOnCompleteListener{
                            task ->
                        if (task.isSuccessful){
                            callBack.onCallBack("success")
                        }else{
                            callBack.onCallBack("fail")
                        }
                    }
                }else{
                    callBack.onCallBack("success")
                }
            }else{
                callBack.onCallBack("fail")
            }
        }
    }

}