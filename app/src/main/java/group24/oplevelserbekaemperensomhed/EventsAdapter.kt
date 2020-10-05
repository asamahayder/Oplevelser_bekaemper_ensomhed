package group24.oplevelserbekaemperensomhed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import group24.oplevelserbekaemperensomhed.Data.Event
import group24.oplevelserbekaemperensomhed.databinding.CardViewProfileBinding

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    private var events: List<Event>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EventViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_view_event,
            parent,
            false
        )
    )

    override fun getItemCount() = events?.size ?: 0

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        events?.let {
            holder.binding.event = it[position]
            holder.binding.executePendingBindings()
        }
    }

    fun setProfiles(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    inner class EventViewHolder(val binding: CardViewProfileBinding) :
        RecyclerView.ViewHolder(binding.root)

}