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

    private var uploadImageCounter = 0;

    private var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = firebaseStorage.reference

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

    fun getAllUsers(callBack: MyCallBack) {
        val dbUsers = ArrayList<DBUser>()
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!){
                        val dbUser = document.toObject(DBUser::class.java)
                        dbUsers.add(dbUser)
                    }
                    callBack.onCallBack(dbUsers)
                }
            }
    }

    fun getUser(id: String, callBack: MyCallBack) {
        db.collection("users").document(id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dbUser = task.result!!.toObject(DBUser::class.java)
                    Log.d("TESSSST", "id = $id user = $dbUser")
                    if (dbUser != null) {
                        val user: UserDTO = UserDTO(dbUser.name,dbUser.age,dbUser.address,dbUser.occupation,dbUser.education,dbUser.about,dbUser.gender,ArrayList(),dbUser.profilePictures.toCollection(ArrayList<String>()))
                        callBack.onCallBack(user)
                    } else {
                        callBack.onCallBack("failure")
                    }
                } else {
                    callBack.onCallBack("failure")
            }
            }
    }

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

    fun getEvents(callBack: MyCallBack){
        val list : ArrayList<EventDTO> = ArrayList()
        getAllEventsFromDB(object : MyCallBack {
            override fun onCallBack(`object`: Any) {
                val eventDataList = `object` as ArrayList<DBEvent>
                Log.d(ActivitySearch.TAG, "Getting all eventCreators")
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

    fun createEvent(event: EventDTO, callBack: MyCallBack){
        val date: List<String> = listOf(event.eventDate.date, event.eventDate.startTime, event.eventDate.endTime)
        val pictures: List<String> = event.pictures

        //TODO: we do not use this parameter, so remove it
        val participants: List<String> = listOf(event.participants?.get(0)?.name.toString())

        val localData = LocalData
        val userID = localData.id

        val dbEvent = DBEvent(address = event.address, category = event.category, eventCreator = userID, eventDate = date, eventDescription = event.eventDescription, eventLikes = event.eventLikes, eventTitle = event.eventTitle, price = event.price, pictures = pictures, participants = participants)
        db.collection("events").add(dbEvent).addOnSuccessListener {
            createParticipantPair(dbEvent.eventTitle, callBack)
        }.addOnFailureListener{
            println("*******************************************************************************************************************")
            Log.e("gg", it.stackTrace.toString())
            callBack.onCallBack("Failure")
        }
    }


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

    fun uploadImages(imageList: ArrayList<Uri>, myListener: MyUploadPicturesListener) {
        val pictureDownloadLinks: ArrayList<String> = ArrayList()
        //Inspired by code from following site: https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/

        uploadImageCounter = imageList.size
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

                    //handle progress dialog
                    uploadImageCounter--
                    if (uploadImageCounter == 0) {
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

    fun deleteUser(id: String,callBack: MyCallBack) {
        db.collection("users").document(id).delete().addOnCompleteListener {task ->
            if (task.isSuccessful) {
                callBack.onCallBack(true)
            }
            else {
                callBack.onCallBack(false)
            }
        }
    }

    //Also known as 'Join event'
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

    //Also known as 'Leave event'
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

    fun getParticipants(event: EventDTO, callBack: MyCallBack){
        val pairs: ArrayList<DBEventParticipantPair> = ArrayList()
        val users: ArrayList<UserDTO> = ArrayList()
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

    //TODO: remmeber to filter out a users own events by checking each events owner and comparing to the logged in user
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

}