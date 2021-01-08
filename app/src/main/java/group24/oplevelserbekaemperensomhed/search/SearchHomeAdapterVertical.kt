package group24.oplevelserbekaemperensomhed.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import group24.oplevelserbekaemperensomhed.R
import kotlinx.android.synthetic.main.fsearch_recyclerview_home1.view.*

class SearchHomeAdapterVertical(private val itemList: List<SearchHomeItemVertical>) : RecyclerView.Adapter<SearchHomeAdapterVertical.SearchHomeViewHolderRow>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHomeViewHolderRow {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.fsearch_recyclerview_home1,
            parent,
            false
        )
        return SearchHomeViewHolderRow(itemView)
    }

    override fun onBindViewHolder(holder: SearchHomeViewHolderRow, position: Int) {
        val currentItem = itemList[position]
        holder.categoryText.text = currentItem.category
        holder.horizontalAdapter.setItemList(currentItem.searchItemHorizontal)
    }

    override fun getItemCount() = itemList.size

    class SearchHomeViewHolderRow(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.fsearch_recyclerview_home1_category
        private val recyclerView: RecyclerView = itemView.fsearch_recyclerview_home1_recyclerview
        val horizontalAdapter: SearchHomeAdapterHorizontal

        init {
            recyclerView.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.recycledViewPool.setMaxRecycledViews(0, 0)
            horizontalAdapter = SearchHomeAdapterHorizontal(itemView.context)
            recyclerView.adapter = horizontalAdapter
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object {
        const val TAG = "adapterV"
    }
}