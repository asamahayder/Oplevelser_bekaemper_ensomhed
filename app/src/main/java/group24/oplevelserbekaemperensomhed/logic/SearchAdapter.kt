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
        return SearchAdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchAdapterViewHolder, position: Int) {
        if (searchResult.size != 0) {
            val currentItem = searchResult[position]

            // Inserts images into the imageviews with use of Picasso and the URLs of the pictures
            imageGliderURLs(currentItem.pictures[0], holder.eventImage)
            imageGliderURLs(currentItem.eventCreator!!.profilePictures[0], holder.profilePic)

            // Handles if the text is too long for the layout, to prevent layout changes the location needs
            // to be handled that it doesn't get too big
            val locationText = if (currentItem.address != "Online") {
                Log.d(SearchHomeAdapterHorizontal.TAG, "Split string ${currentItem.address}")
                val stringArray = currentItem.address.split(",")
                stringArray[0]
            } else {
                currentItem.address
            }

            // Inserts all the text onto the layout
            holder.locText.text = locationText
            holder.eventTitle.text = currentItem.eventTitle
            holder.cateText.text = currentItem.category
            holder.locText.text = locationText
            holder.priceText.text = currentItem.price

            // Handles click listeners so that the user is able to click and open new activities
            holder.eventImage.setOnClickListener {
                handleActivityToFragmentChange("event", currentItem)
            }
            holder.profilePic.setOnClickListener {
                handleActivityToFragmentChange("profile", currentItem)
            }
        }
    }

    private fun handleActivityToFragmentChange(bundleTag: String, currentItem: EventDTO) {
        // Because the user has clicked on someone else's event/profile, a "other" tag is sent with
        // to update the view accordingly
        // ActivityFragmentHandler is used so that we can re use profile and event fragments
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

    // loads an image URL into an imageview
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

    // Gets the current filter for the search bar
    override fun getFilter() = searchFilter

    // Handles searching with the search bar, so that the adapter gets filtered
    private val searchFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            val filteredList = ArrayList<EventDTO>()
            // If there is no filter text in the search bar, nothing happens and all events are added to the list to be shown
            if (constraint == null || constraint.isEmpty()){
                filteredList.addAll(searchResultFull)
            }
            else {
                // The filter pattern is the text written into the search field
                val filterPattern = constraint.toString().toLowerCase().trim()
                // Iterates through the adapter to see which events fit the filter
                for (item in searchResultFull) {
                    // Filters for title of each event
                    if (item.eventTitle.toLowerCase().contains(filterPattern)){
                        // Adds items that fit to the filtered list
                        filteredList.add(item)
                    }
                }
            }
            // Updates the search results by clearing the searchResult list and adding the filtered list to the searchResult
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        // Updates the filtered list search results
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