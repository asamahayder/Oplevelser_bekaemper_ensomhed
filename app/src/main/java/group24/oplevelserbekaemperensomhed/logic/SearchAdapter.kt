package group24.oplevelserbekaemperensomhed.logic

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler
import group24.oplevelserbekaemperensomhed.view.search.ActivitySearch
import kotlinx.android.synthetic.main.activity_search_1_recyclerview.view.*
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(val searchResult: ArrayList<EventDTO>, val context: ActivitySearch) : RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder>(), Filterable {

    private val searchResultFull = searchResult.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.activity_search_1_recyclerview,
            parent,
            false
        )
        Log.d(TAG, "Search ViewHolder Initialization")
        return SearchAdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchAdapterViewHolder, position: Int) {
        if (searchResult.size != 0) {
            val currentItem = searchResult[position]

            imageGliderURLs(currentItem.pictures[0], holder.eventImage)
            imageGliderURLs(currentItem.eventCreator!!.profilePictures[0], holder.profilePic)

            val locationText = if (currentItem.address != "Online") {
                Log.d(SearchHomeAdapterHorizontal.TAG, "Split string ${currentItem.address}")
                val stringArray = currentItem.address.split(",")
                stringArray[0]
            } else {
                currentItem.address
            }
            holder.locText.text = locationText

            holder.eventTitle.text = currentItem.eventTitle
            holder.cateText.text = currentItem.category
            holder.locText.text = locationText
            holder.priceText.text = currentItem.price

            holder.eventImage.setOnClickListener {
                handleActivityToFragmentChange("event", currentItem)
            }
            holder.profilePic.setOnClickListener {
                handleActivityToFragmentChange("profile", currentItem)
            }
        }
    }

    private fun handleActivityToFragmentChange(bundleTag: String, currentItem: EventDTO) {
        val intent = Intent(context, ActivityFragmentHandler::class.java)
        if (bundleTag == "profile") {
            intent.putExtra(bundleTag, currentItem.eventCreator)
            intent.putExtra("other", "other")
        } else {
            intent.putExtra(bundleTag, currentItem)
            intent.putExtra("other", "other")
        }
        context.startActivity(intent)
    }

    private fun imageGliderURLs(imageURL: String, imageView: ImageView) {
        Picasso.get()
            .load(imageURL)
            .fit()
            .centerCrop()
            .into(imageView)
    }

    override fun getItemCount() = searchResult.size

    class SearchAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.activity_search_1_recyclerview_firstPic
        val eventTitle: TextView = itemView.activity_search_1_recyclerview_firstPicTitle
        val locText: TextView = itemView.activity_search_1_recyclerview_firstPicLocationText
        val priceText: TextView = itemView.activity_search_1_recyclerview_firstPicPriceText
        val cateText: TextView = itemView.activity_search_1_recyclerview_firstPicCategoryText
        val profilePic: ImageView = itemView.activity_search_1_recyclerview_firstPicProfilePic
    }

    override fun getFilter() = searchFilter

    private val searchFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            val filteredList = ArrayList<EventDTO>()

            if (constraint == null || constraint.isEmpty()){
                Log.d(TAG, "Filtering for : Nothing as there is no constraint")
                filteredList.addAll(searchResultFull)
            }
            else {
                val filterPattern = constraint.toString().toLowerCase().trim()
                Log.d(TAG, "Filtering for : $filterPattern")
                for (item in searchResultFull) {
                    if (item.eventTitle.toLowerCase().contains(filterPattern)){
                        filteredList.add(item)
                    }
                }
                Log.d(TAG,"All searchResults size = ${searchResultFull.size}")
                Log.d(TAG,"All filteredResults size = ${filteredList.size}")
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            searchResult.clear()
            searchResult.addAll(results!!.values as ArrayList<EventDTO>)
            notifyDataSetChanged()
        }
    }

    companion object {
        const val TAG = "adapterSearch"
    }
}