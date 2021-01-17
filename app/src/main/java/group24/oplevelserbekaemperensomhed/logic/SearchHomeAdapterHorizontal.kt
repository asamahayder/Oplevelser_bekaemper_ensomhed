package group24.oplevelserbekaemperensomhed.logic

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler
import group24.oplevelserbekaemperensomhed.view.search.FragmentSearchHome
import kotlinx.android.synthetic.main.fragment_search_home_2_recyclerview.view.*

class SearchHomeAdapterHorizontal(private var mContext: Context, val context: FragmentSearchHome)  : RecyclerView.Adapter<SearchHomeAdapterHorizontal.SearchHomeViewHolderColumn>() {

    init {
        setHasStableIds(true)
    }

    private var itemList: List<EventDTO> = ArrayList()

    fun setItemList(data: List<EventDTO>){
        if (itemList != data){
            itemList = data
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHomeViewHolderColumn {
        val itemView = LayoutInflater.from(mContext).inflate(
            R.layout.fragment_search_home_2_recyclerview,
            parent,
            false
        )
        Log.d(TAG, "Horizontal ViewHolder Initialization")
        return SearchHomeViewHolderColumn(itemView)
    }

    override fun onBindViewHolder(holder: SearchHomeViewHolderColumn, position: Int) {
        val currentItem = itemList[position]
        val titleText = if (currentItem.eventTitle.length > 14) {
            currentItem.eventTitle.substring(0,12) + "..."
        } else {
            currentItem.eventTitle
        }
        holder.titleText.text = titleText
        val nameText = if (currentItem.eventCreator!!.name!!.length > 8) {
            Log.d(TAG, "Split string ${currentItem.eventCreator!!.name}")
            currentItem.eventCreator!!.name!!.substring(0,8) + "..."
        } else {
            currentItem.eventCreator!!.name
        }
        holder.nameText.text = nameText
        val locationText = if (currentItem.address != "Online") {
            Log.d(TAG, "Split string ${currentItem.address}")
            val stringArray = currentItem.address.split(",")
            stringArray[0]
        } else {
            currentItem.address
        }
        holder.locationText.text = locationText
        Picasso.get()
            .load(currentItem.pictures[0])
            .fit()
            .centerCrop()
            .into(holder.eventImage)
        Picasso.get()
            .load(currentItem.eventCreator!!.profilePictures[0])
            .fit()
            .centerCrop()
            .into(holder.profileImage)

        holder.eventImage.setOnClickListener {
            Log.d(TAG, "Clicking on Event")
            handleFragmentActivityChange("event", currentItem)
        }
        holder.profileImage.setOnClickListener {
            Log.d(TAG, "Clicking on Profile")
            handleFragmentActivityChange("profile", currentItem)
        }
    }

    override fun getItemCount() = itemList.size

    class SearchHomeViewHolderColumn(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.fsearch_recyclerview_home2_eventimage
        val profileImage: ImageView = itemView.fsearch_recyclerview_home2_profileimage
        val titleText: TextView = itemView.fsearch_recyclerview_home2_titletext
        val nameText: TextView = itemView.fsearch_recyclerview_home2_nametext
        val locationText: TextView = itemView.fsearch_recyclerview_home2_locationtext
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    companion object {
        const val TAG = "adapterH"
    }

    private fun handleFragmentActivityChange(bundleTag: String, currentItem: EventDTO) {
        val intent = Intent(mContext, ActivityFragmentHandler::class.java)
        if (bundleTag == "profile") {
            intent.putExtra(bundleTag, currentItem.eventCreator)
            intent.putExtra("other", "other")
        } else {
            intent.putExtra(bundleTag, currentItem)
            intent.putExtra("other", "other")
        }
        mContext.startActivity(intent)
    }
}