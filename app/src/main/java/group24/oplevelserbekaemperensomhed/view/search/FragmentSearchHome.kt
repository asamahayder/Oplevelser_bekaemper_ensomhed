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
import group24.oplevelserbekaemperensomhed.logic.SearchHomeAdapterVertical
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter
import group24.oplevelserbekaemperensomhed.logic.firebase.*


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
        searchActivityButton.setOnClickListener {
            Log.d(TAG, "Search Activity clicked")
            val intent = Intent(activity, ActivitySearch::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }
        swipeRefresh.setOnRefreshListener {
            events.clear()
            bannerURLs.clear()
            bannerPictures.clear()
            firebaseQueryBanners()
            firebaseQueryEvents()
            swipeRefresh.isRefreshing = false
        }
        Log.d(TAG, "searchResultsEvents = ${localData.searchResultsEvents}")
        Log.d(TAG, "searchResultsBanners = ${localData.searchResultsBanners}")
        Log.d(TAG, "eventsSortedByCategories = $sortedCategoriesList")
        if (sortedCategoriesList.size == 0) {
            firebaseQueryEvents()
        } else {
            handleRecyclerView()
        }
        if (sortedCategoriesList.size == 0) {
            firebaseQueryBanners()
        } else {
            handleViewPager()
        }
    }

    private fun firebaseQueryBanners() {
        Log.d(TAG, "Querying firebase for banners")
        db.getAllBanners(object : MyCallBack {
            override fun onCallBack(`object`: Any) {
                val bannerDataList = `object` as ArrayList<DBBanner>
                for (banner in `object`) {
                    bannerPictures.add(banner.picture)
                    bannerURLs.add(banner.url)
                }
                if (viewPager.isAttachedToWindow) {
                    handleViewPager()
                }
                localData.searchResultsBanners = bannerDataList
                Log.d(TAG, "Firebase query for banners complete")
            }
        })
    }

    private fun handleViewPager() {
        val pagerAdapter = ViewPagerAdapter(childFragmentManager, bannerPictures, R.layout.fragment_search_home_1_viewpager, bannerURLs)
        viewPager.adapter = pagerAdapter
    }

    private fun firebaseQueryEvents(){
        Log.d(TAG, "Querying firebase for events")
        db.getEvents(object: MyCallBack{
            override fun onCallBack(`object`: Any) {
                val list:ArrayList<EventDTO> = `object` as ArrayList<EventDTO>
                sortedCategoriesList = sortEventsByCategory(list)
                localData.searchResultsEvents = list
                handleRecyclerView()
                Log.d(TAG, "Firebase query for events complete")
            }
        })
    }

    private fun handleRecyclerView() {
        adapter = SearchHomeAdapterVertical(this@FragmentSearchHome, sortedCategoriesList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.recycledViewPool.setMaxRecycledViews(0, 0)
        events = localData.searchResultsEvents
    }

    private fun sortEventsByCategory(eventList: ArrayList<EventDTO>): ArrayList<SearchHomeItem> {
        val searchHomeListVertical = ArrayList<SearchHomeItem>()
        for (i in eventList.indices) {
            val searchHomeListHorizontal: MutableList<EventDTO> = java.util.ArrayList()
            val category: String = eventList[i].category
            var isANewCategory = false
            for (j in eventList.indices) {
                val nextCategory: String = eventList[j].category
                val (eventCreator, participants, eventDescription, eventTitle, eventDate, eventLikes, category1, address, price, pictures) = eventList[j]
                val item = EventDTO(
                    eventCreator,
                    participants,
                    eventDescription,
                    eventTitle,
                    eventDate, eventLikes, category1, address, price, pictures
                )
                if (category == nextCategory) {
                    var isItemInSortedList = true
                    for (k in searchHomeListVertical.indices) {
                        if (searchHomeListVertical[k].category == category) {
                            isItemInSortedList = false
                            break
                        }
                    }
                    if (isItemInSortedList || searchHomeListVertical.size == 0) {
                        isANewCategory = true
                        searchHomeListHorizontal.add(item)
                    }
                }
            }
            if (isANewCategory) {
                val items = SearchHomeItem(category, searchHomeListHorizontal)
                searchHomeListVertical.add(items)
            }
        }
        return searchHomeListVertical
    }

    companion object {
        const val TAG = "FragmentSearchHome"
    }

}