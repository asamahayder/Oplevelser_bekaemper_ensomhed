package group24.oplevelserbekaemperensomhed.logic.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import group24.oplevelserbekaemperensomhed.data.DateDTO
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.view.search.ActivitySearch


class FirebaseDAO{

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
                    if (dbUser != null) {
                        var user: UserDTO = UserDTO(dbUser.name,dbUser.age,dbUser.address,dbUser.occupation,dbUser.education,dbUser.about,dbUser.gender,ArrayList(),dbUser.profilePictures.toCollection(ArrayList<String>()))
                        callBack.onCallBack(user)
                    }
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
                            val user = `object` as UserDTO
                            val event = EventDTO(user,null,dbEvent.eventDescription,dbEvent.eventTitle,
                                DateDTO(dbEvent.eventDate[0],dbEvent.eventDate[1],dbEvent.eventDate[2]),dbEvent.eventLikes,dbEvent.category,dbEvent.address,dbEvent.price,dbEvent.pictures.toCollection(ArrayList()))
                            list.add(event)
                            callBack.onCallBack(list)
                        }
                    })
                }
            }
        })
    }

    fun createEvent(event: EventDTO, callBack: MyCallBack){
        val date: List<String> = listOf(event.eventDate.date, event.eventDate.startTime, event.eventDate.endTime)
        val pictures: List<String> = event.pictures
        val participants: List<String> = listOf(event.participants?.get(0)?.name.toString())

        //TODO instead of the 'testUser' placeholder, use a real user.
        val dbEvent: DBEvent = DBEvent(address = event.address, category = event.category, eventCreator = "testUser", eventDate = date, eventDescription = event.eventDescription, eventLikes = event.eventLikes, eventTitle = event.eventTitle, price = event.price, pictures = pictures, participants = participants)
        db.collection("events").add(dbEvent).addOnSuccessListener { callBack.onCallBack("success") }.addOnFailureListener{
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

}