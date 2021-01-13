package group24.oplevelserbekaemperensomhed.view.search

import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.util.EventLog
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.DateDTO
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.logic.SearchAdapter
import group24.oplevelserbekaemperensomhed.logic.firebase.DBEvent
import group24.oplevelserbekaemperensomhed.logic.firebase.DBUser
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack

class ActivitySearch : AppCompatActivity() {

    private var events = ArrayList<EventDTO>()
    private lateinit var adapter: SearchAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private lateinit var db: FirebaseDAO
    private val localData = LocalData

    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initializeView()
    }

    private fun initializeView() {
        adapter = SearchAdapter(events, context)
        searchView = findViewById(R.id.activity_search_1_searchview)
        recyclerView = findViewById(R.id.activity_search_1_recyclerview)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        if (intent.extras != null) {
            if (intent.extras!!["eventList"] != null) {
                events = intent.getParcelableArrayListExtra("eventList")!!

                adapter = SearchAdapter(events, context)
                searchView.queryHint = events[0].category
                searchView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                searchView.inputType = InputType.TYPE_NULL
            }
        } else {
            if (events.size > 0) {
                events.clear()
            }
            searchView.isIconified = false
            searchView.isIconified = true
            firebaseQuery()
        }
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    /*private fun firebaseQuery() {
        db = FirebaseDAO()
        Log.d(TAG, "Getting all events")
        db.getAllEvents(object : MyCallBack {
            override fun onCallBack(dbEvents: Any) {
                val eventDataList = dbEvents as ArrayList<DBEvent>
                Log.d(TAG, "Getting all eventCreators")
                    for (dbEvent in eventDataList) {
                    db.getUser(dbEvent.eventCreator, object : MyCallBack {
                        override fun onCallBack(`dbUser`: Any) {
                            val userData = dbUser as DBUser
                            val user = UserDTO(userData.name,userData.age,userData.address,userData.occupation,userData.education,userData.about,userData.gender,null,userData.profilePictures.toCollection(ArrayList()))
                            val event = EventDTO(user,null,dbEvent.eventDescription,dbEvent.eventTitle,
                                DateDTO(dbEvent.eventDate[0],dbEvent.eventDate[1],dbEvent.eventDate[2]),dbEvent.eventLikes,dbEvent.category,dbEvent.address,dbEvent.price,dbEvent.pictures.toCollection(ArrayList()))
                            events.add(event)
                            localData.searchResultsEvents = events
                            adapter = SearchAdapter(events, context)
                            recyclerView.adapter = adapter
                            Log.d(TAG, "Updating adapter")
                        }
                    })
                }
            }
        })
    }*/

    private fun firebaseQuery() {
        db = FirebaseDAO()
        Log.d(TAG, "Getting all events")

        db.getEvents(object :MyCallBack{
            override fun onCallBack(`object`: Any) {
                val events = `object` as ArrayList<EventDTO>
                localData.searchResultsEvents = events
                adapter = SearchAdapter(events, context)
                recyclerView.adapter = adapter
                Log.d(TAG, "Updating adapter")
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

    companion object {
        const val TAG = "ActivitySearch"
    }
}