package group24.oplevelserbekaemperensomhed.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import group24.oplevelserbekaemperensomhed.R
import kotlinx.android.synthetic.main.fsearch_recyclerview_home2.view.*

class SearchHomeAdapterHorizontal(private var mContext: Context)  : RecyclerView.Adapter<SearchHomeAdapterHorizontal.SearchHomeViewHolderColumn>() {

    init {
        setHasStableIds(true)
    }

    private var itemList: List<SearchHomeItemHorizontal> = ArrayList()

    fun setItemList(data: List<SearchHomeItemHorizontal>){
        if (itemList != data){
            itemList = data
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHomeViewHolderColumn {
        val itemView = LayoutInflater.from(mContext).inflate(
            R.layout.fsearch_recyclerview_home2,
            parent,
            false
        )
        return SearchHomeViewHolderColumn(itemView)
    }

    override fun onBindViewHolder(holder: SearchHomeViewHolderColumn, position: Int) {
        val currentItem = itemList[position]
        holder.titleText.text = currentItem.title
        holder.nameText.text = currentItem.name
        holder.locationText.text = currentItem.location
        Picasso.get()
            .load(currentItem.eventImage)
            .fit()
            .centerCrop()
            .into(holder.eventImage)
        Picasso.get()
            .load(currentItem.profileImage)
            .fit()
            .centerCrop()
            .into(holder.profileImage)
    }

    override fun getItemCount() = itemList.size

    class SearchHomeViewHolderColumn(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.fsearch_recyclerview_home2_eventimage
        val profileImage: ImageView = itemView.fsearch_recyclerview_home2_profileimage
        val titleText: TextView = itemView.fsearch_recyclerview_home2_titletext
        val nameText: TextView = itemView.fsearch_recyclerview_home2_nametext
        val locationText: TextView = itemView.fsearch_recyclerview_home2_locationtext
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object {
        const val TAG = "adapterH"
    }
}