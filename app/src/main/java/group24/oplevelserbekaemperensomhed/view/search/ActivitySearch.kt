package group24.oplevelserbekaemperensomhed.view.search

import android.os.Bundle
import android.text.InputType
import android.util.EventLog
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

// Handles the search activity where searching through the results is done

class ActivitySearch : AppCompatActivity() {

    private var events = ArrayList<EventDTO>()
    private lateinit var adapter: SearchAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var db: FirebaseDAO
    private val localData = LocalData

    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initializeView()
    }

    private fun initializeView() {
        // Instantiates the search adapter
        adapter = SearchAdapter(events, context)
        searchView = findViewById(R.id.activity_search_searchview)
        recyclerView = findViewById(R.id.activity_search_recyclerview)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        swipeRefresh = findViewById(R.id.activity_search_refresh)

        // handles swipe down to refresh gesture. Fetches new results from the database
        swipeRefresh.setOnRefreshListener {
            events.clear()
            searchView.setQuery("",false)
            firebaseQuery()
            swipeRefresh.isRefreshing = false
        }

        // If the user got here from pressing an category, update the recyclerview accordingly
        if (intent.extras != null) {
            if (intent.extras!!["eventList"] != null) {
                events = intent.getParcelableArrayListExtra("eventList")!!

                // instantiates the adapter with categorized events
                adapter = SearchAdapter(events, context)
                searchView.queryHint = events[0].category
                searchView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                searchView.inputType = InputType.TYPE_NULL
            }
        } else {
            if (events.size > 0) {
                events.clear()
            }
            // Removes search keyboard
            searchView.isIconified = false
            searchView.isIconified = true

            // Queries the databse
            firebaseQuery()
        }
        recyclerView.adapter = adapter

        // handles querying the search bar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // because filtering happens in realtime, nothing is implemented here
                //FIXME
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filters the events depending on what is typed into the search bar
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    // Fetches events from the database with use of a callback interface
    private fun firebaseQuery() {
        db = FirebaseDAO()
        db.getEvents(object :MyCallBack{
            override fun onCallBack(`object`: Any) {
                // Upadtes the searchresults and updates the adapter
                val events = `object` as ArrayList<EventDTO>
                localData.searchResultsEvents = events
                adapter = SearchAdapter(events, context)
                recyclerView.adapter = adapter
            }
        })

    }

    // Handles getting back to the same view on the previous activity
    // (if the user had scrolled down, opened this activity and gone back, he'd still be on the
    // same place when going back again)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Finishes the activity
    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

    companion object {
        const val TAG = "ActivitySearch"
    }
}