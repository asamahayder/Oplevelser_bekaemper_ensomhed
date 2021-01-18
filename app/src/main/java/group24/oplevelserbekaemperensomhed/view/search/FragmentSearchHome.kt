package group24.oplevelserbekaemperensomhed.view.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.*
import group24.oplevelserbekaemperensomhed.logic.Logic
import group24.oplevelserbekaemperensomhed.logic.SearchHomeAdapterVertical
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter
import group24.oplevelserbekaemperensomhed.logic.firebase.*

// Handles the categorized search activity over events

class FragmentSearchHome : Fragment() {

    private lateinit var events: ArrayList<EventDTO>
    private lateinit var adapter: SearchHomeAdapterVertical

    private lateinit var searchActivityButton: View
    private lateinit var viewPager: ViewPager
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val db = FirebaseDAO()
    private val localData = LocalData

    private var bannerPictures = ArrayList<String>()
    private var bannerURLs = ArrayList<String>()
    private var bannerTitles = ArrayList<String>()
    private var sortedCategoriesList= ArrayList<SearchHomeItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeView()
    }

    private fun initializeView() {
        searchActivityButton = requireView().findViewById(R.id.fsearch_home_searchbutton)
        viewPager = requireView().findViewById(R.id.fsearch_viewpager)
        recyclerView = requireView().findViewById(R.id.fsearch_recyclerview)
        swipeRefresh = requireView().findViewById(R.id.fsearch_refresh)

        // Opens the search activity
        searchActivityButton.setOnClickListener {
            val intent = Intent(activity, ActivitySearch::class.java)
            // Disables the animation to make it look like we're still on the same activity
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }

        // handles swipe to refresh events via the database
        swipeRefresh.setOnRefreshListener {
            events.clear()
            bannerURLs.clear()
            bannerPictures.clear()

            // Updates the banners and events
            firebaseQueryBanners()
            firebaseQueryEvents()

            // Removes the loading bar after refreshing
            swipeRefresh.isRefreshing = false
        }

        // Fetches events if the list is empty else updates the recyclerview
        if (sortedCategoriesList.size == 0) {
            firebaseQueryEvents()
        } else {
            handleRecyclerView()
        }

        // Fetches banners if the list is empty else updates the recyclerview
        if (sortedCategoriesList.size == 0) {
            firebaseQueryBanners()
        } else {
            handleViewPager()
        }
    }

    // Queries the database for banners
    private fun firebaseQueryBanners() {
        db.getAllBanners(object : MyCallBack {
            override fun onCallBack(`object`: Any) {
                val bannerDataList = `object` as ArrayList<DBBanner>
                // Checks if it's an banner we're working with or nothing and adds it to the list
                for (banner in `object`) {
                    bannerPictures.add(banner.picture)
                    bannerURLs.add(banner.url)
                    bannerTitles.add(banner.title)
                }
                if (viewPager.isAttachedToWindow) {
                    handleViewPager()
                }
                // Updates the banners in the local search results
                localData.searchResultsBanners = bannerDataList
            }
        })
    }

    // Handles the viewpager which shows the banners
    private fun handleViewPager() {
        val list = ArrayList<ArrayList<String>>()
        list.add(bannerURLs)
        list.add(bannerTitles)

        // Creates an adapter with the banner layout and handles the pictures and text from the banners
        val pagerAdapter = ViewPagerAdapter(childFragmentManager, bannerPictures, R.layout.fragment_search_home_1_viewpager, list)
        viewPager.adapter = pagerAdapter
    }

    // Queries the firebase for events
    private fun firebaseQueryEvents(){
        db.getEvents(object: MyCallBack{
            override fun onCallBack(`object`: Any) {
                val list:ArrayList<EventDTO> = `object` as ArrayList<EventDTO>
                // Sorts the events gotten from the database
                val logic = Logic()
                sortedCategoriesList = logic.sortEventsByCategory(list)
                localData.searchResultsEvents = list
                handleRecyclerView()
            }
        })
    }

    // Updates the recyclerview
    private fun handleRecyclerView() {
        adapter = SearchHomeAdapterVertical(this@FragmentSearchHome, sortedCategoriesList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.recycledViewPool.setMaxRecycledViews(0, 0)
        events = localData.searchResultsEvents
    }

    companion object {
        const val TAG = "FragmentSearchHome"
    }

}