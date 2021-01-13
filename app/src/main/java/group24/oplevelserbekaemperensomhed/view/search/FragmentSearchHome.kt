package group24.oplevelserbekaemperensomhed.view.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.*
import group24.oplevelserbekaemperensomhed.logic.SearchHomeAdapterVertical
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter
import group24.oplevelserbekaemperensomhed.logic.firebase.*


class FragmentSearchHome : Fragment() {

    private val events = ArrayList<EventDTO>()
    private lateinit var adapter: SearchHomeAdapterVertical

    private lateinit var searchActivityButton: View
    private lateinit var viewPager: ViewPager
    private lateinit var recyclerView: RecyclerView

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
        searchActivityButton = view!!.findViewById(R.id.fsearch_home_searchbutton)
        viewPager = view!!.findViewById(R.id.fsearch_viewpager)
        recyclerView = view!!.findViewById(R.id.fsearch_recyclerview)
        searchActivityButton.setOnClickListener {
            Log.d(TAG, "Search Activity clicked")
            val intent = Intent(activity, ActivitySearch::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }
        if (localData.searchResultsEvents.size == 0) {
            firebaseQueryEvents()
        } else {
            handleRecyclerView()
        }
        if (localData.searchResultsBanners.size == 0) {
            firebaseQueryBanners()
        } else {
            handleViewPager()
        }
        //testing()
    }

    private fun firebaseQueryBanners() {
        Log.d(TAG, "Querying firebase for banners")
        db.getAllBanners(object : MyCallBack {
            override fun onCallBack(`dbBanners`: Any) {
                val bannerDataList = dbBanners as ArrayList<DBBanner>
                for (banner in dbBanners) {
                    bannerPictures.add(banner.picture)
                    bannerURLs.add(banner.url)
                }
                handleViewPager()
                localData.searchResultsBanners = bannerDataList
                Log.d(TAG, "Firebase query for banners complete")
            }
        })
    }

    private fun handleViewPager() {
        val pagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            bannerPictures, R.layout.fragment_search_home_1_viewpager, bannerURLs)
        viewPager.adapter = pagerAdapter
    }

    private fun firebaseQueryEvents(){
        db.getEvents(object: MyCallBack{
            override fun onCallBack(`object`: Any) {
                val list:ArrayList<EventDTO> = `object` as ArrayList<EventDTO>
                sortedCategoriesList = sortEventsByCategory(list)
                handleRecyclerView()
            }
        })
    }




    private fun handleRecyclerView() {
        adapter = SearchHomeAdapterVertical(this@FragmentSearchHome, sortedCategoriesList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.recycledViewPool.setMaxRecycledViews(0, 0)
        localData.searchResultsEvents = events
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

    private fun testing() {
        val dummy = DummyData()
        val dummyList = dummy.searchHomeList
        testing_viewpager()
        recyclerView.adapter = SearchHomeAdapterVertical(this, dummyList)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.recycledViewPool.setMaxRecycledViews(0, 0)

        Log.d(TAG, dummyList.size.toString())
    }



    private fun testing_viewpager() {
        val dummy = DummyData()
        val pagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            dummy.bannerList as ArrayList<String>, R.layout.fragment_search_home_1_viewpager,dummy.bannerList as ArrayList<String>)
        viewPager.adapter = pagerAdapter
    }

    companion object {
        const val TAG = "search"
    }

}