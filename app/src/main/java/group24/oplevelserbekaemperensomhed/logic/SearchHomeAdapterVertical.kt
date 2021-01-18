package group24.oplevelserbekaemperensomhed.logic

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.view.search.FragmentSearchHome
import group24.oplevelserbekaemperensomhed.data.SearchHomeItem
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler
import group24.oplevelserbekaemperensomhed.view.search.ActivitySearch
import kotlinx.android.synthetic.main.fragment_search_home_1_recyclerview.view.*
import java.lang.reflect.Type

// The Vertical adapter that handles all the categories for the recyclerview from ActivitySearchHome

class SearchHomeAdapterVertical(
    private val context: FragmentSearchHome,
    private val itemList: MutableList<SearchHomeItem>
) : RecyclerView.Adapter<SearchHomeAdapterVertical.SearchHomeViewHolderRow>() {

    init {
        setHasStableIds(true)
    }

    // Opens the searchActivity and defines which category needs to loaded depending on what section was clicked on
    private fun handleActivityToFragmentChange(bundleTag: String, currentItem: ArrayList<EventDTO>) {
        val intent = Intent(context.activity, ActivitySearch::class.java)
        intent.putExtra(bundleTag, currentItem)
        context.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHomeViewHolderRow {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_search_home_1_recyclerview,
            parent,
            false
        )
        return SearchHomeViewHolderRow(itemView, context)
    }

    override fun onBindViewHolder(holder: SearchHomeViewHolderRow, position: Int) {
        val currentItem = itemList[position]
        holder.categoryText.text = currentItem.category

        // If a category section is clicked, open the searchActivity for re-use and only load the relevant events with that category
        holder.categoryButton.setOnClickListener {
            handleActivityToFragmentChange("eventList", currentItem.searchItemHorizontal as ArrayList<EventDTO>)
        }
        holder.horizontalAdapter.setItemList(currentItem.searchItemHorizontal)
    }

    override fun getItemCount() = itemList.size

    class SearchHomeViewHolderRow(itemView: View, context: FragmentSearchHome) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.fsearch_recyclerview_home1_category
        val categoryButton: ImageView = itemView.fsearch_recyclerview_home1_showmore_button
        private val recyclerView: RecyclerView = itemView.fsearch_recyclerview_home1_recyclerview
        val horizontalAdapter: SearchHomeAdapterHorizontal

        // creates the horizontal recyclerview which holds all the events
        init {
            recyclerView.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.recycledViewPool.setMaxRecycledViews(0, 0)
            horizontalAdapter = SearchHomeAdapterHorizontal(itemView.context, context)
            recyclerView.adapter = horizontalAdapter
        }

    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    companion object {
        const val TAG = "adapterV"
    }
}