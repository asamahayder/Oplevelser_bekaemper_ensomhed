package group24.oplevelserbekaemperensomhed.search

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import group24.oplevelserbekaemperensomhed.R

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
        searchButton = getView()!!.findViewById(R.id.fsearch_searchbutton)
        bannerImage = getView()!!.findViewById(R.id.fsearch_bannerimage)
        recyclerView = getView()!!.findViewById(R.id.fsearch_recyclerview)
    }

}