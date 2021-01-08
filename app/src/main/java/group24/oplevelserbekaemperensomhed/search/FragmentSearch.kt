package group24.oplevelserbekaemperensomhed.search

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.DummyData

class FragmentSearch : Fragment() {

    lateinit var searchButton: LinearLayout
    lateinit var bannerImage: ImageView
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeView()
    }

    private fun initializeView() {
        searchButton = view!!.findViewById(R.id.fsearch_searchbutton)
        bannerImage = view!!.findViewById(R.id.fsearch_bannerimage)
        recyclerView = view!!.findViewById(R.id.fsearch_recyclerview)

        testing()


    }

    private fun testing() {
        val dummy = DummyData()
        val dummyList = dummy.searchHomeList
        recyclerView.adapter = SearchHomeAdapterVertical(dummyList)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        recyclerView.recycledViewPool.setMaxRecycledViews(0,0)
        Log.d(TAG, dummyList.size.toString())
    }

    companion object {
        const val TAG = "search"
    }
}