package group24.oplevelserbekaemperensomhed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import group24.oplevelserbekaemperensomhed.data.EventDTO;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder>{
    private List<EventDTO> eventItems;
    private final EventItemClickListener eventClickListener;

    public EventsAdapter(List<EventDTO> eventItems, EventItemClickListener eventClickListener) {
        this.eventItems = eventItems;
        this.eventClickListener = eventClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, final int position) {
        final EventDTO eventItem = eventItems.get(position);

        holder.setEventData(eventItems.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventClickListener.onEventItemClick(position, eventItem, holder.itemView);
            }
        });

        holder.initializePicture(eventItems.get(position));
    }

    @Override
    public int getItemCount() {
        return eventItems.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{
        TextView eventTitle;
        TextView eventAddress;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventAddress = itemView.findViewById(R.id.eventAdress);
            eventImage = itemView.findViewById(R.id.image_view_profile_pic);
        }
        void setEventData(EventDTO eventItem){
            eventTitle.setText(eventItem.getEventTitle());
            eventAddress.setText(eventItem.getAddress());
        }
        void initializePicture(EventDTO eventItem){
            Picasso.get().load(eventItem.getPictureURL()).into(eventImage);
        }
    }
}
