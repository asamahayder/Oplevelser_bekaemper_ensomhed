package group24.oplevelserbekaemperensomhed.logic

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import group24.oplevelserbekaemperensomhed.FragmentEventInfo
import group24.oplevelserbekaemperensomhed.view.profile.FragmentProfile
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.EventDTO
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
        holder.titleText.text = currentItem.eventTitle
        holder.nameText.text = currentItem.eventCreator.name
        holder.locationText.text = currentItem.address
        Picasso.get()
            .load(currentItem.pictures[0])
            .fit()
            .centerCrop()
            .into(holder.eventImage)
        Picasso.get()
            .load(currentItem.eventCreator.profilePictures[0])
            .fit()
            .centerCrop()
            .into(holder.profileImage)

        holder.eventImage.setOnClickListener {
            Log.d(TAG, "Clicking on Event")
            handleFragmentChange("event", currentItem)
        }
        holder.profileImage.setOnClickListener {
            Log.d(TAG, "Clicking on Profile")
            handleFragmentChange("profile", currentItem)
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

    private fun handleFragmentChange(bundleTag: String, currentItem: EventDTO) {
        val bundle = Bundle()
        val fragment: Fragment
        fragment = if (bundleTag == "profile"){
            bundle.putParcelable(bundleTag, currentItem.eventCreator)
            bundle.putString("other","other")
            FragmentProfile()
        } else {
            bundle.putParcelable(bundleTag, currentItem)
            FragmentEventInfo()
        }
        fragment.arguments = bundle
        if (context.fragmentManager != null){
            context.fragmentManager!!.beginTransaction()
                .replace(R.id.mainFragment, fragment)
                .addToBackStack(R.string.fragment_search.toString())
                .commit()
        }
    }
}