package group24.oplevelserbekaemperensomhed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import group24.oplevelserbekaemperensomhed.data.EventDTO;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder>{

    private List<EventDTO> eventItems;
    private final ItemClickListener eventClickListener;

    public EventsAdapter(List<EventDTO> eventItems, ItemClickListener eventClickListener) {
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
        holder.eventOwnerProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventClickListener.onProfileItemClick(position, eventItem.getEventCreator(), holder.itemView);
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
        TextView eventPrice;
        TextView eventCategory;
        TextView eventOwner;
        ImageView eventCategoryIcon;
        ImageView eventOwnerProfilePicture;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventAddress = itemView.findViewById(R.id.eventAddress);
            eventImage = itemView.findViewById(R.id.eventPicture);
            eventPrice = itemView.findViewById(R.id.eventPrice);
            eventCategory = itemView.findViewById(R.id.eventCategory);
            eventCategoryIcon = itemView.findViewById(R.id.eventCategoryIcon);
            eventOwnerProfilePicture = itemView.findViewById(R.id.eventOwnerProfilePicture);
            eventOwner = itemView.findViewById(R.id.eventOwnerName);
        }
        void setEventData(EventDTO eventItem){
            eventTitle.setText(eventItem.getEventTitle());
            eventAddress.setText(eventItem.getAddress());
            eventPrice.setText(eventItem.getPrice());
            eventCategory.setText(eventItem.getCategory());
            handleCategories(eventItem.getCategory());
            eventOwner.setText(eventItem.getEventCreator().getName());

        }

        private void handleCategories(String categoryName){
            switch (categoryName){
                case "Sport": eventCategoryIcon.setImageResource(R.drawable.ic_baseline_directions_run_24); break;
                case "Fun": eventCategoryIcon.setImageResource(R.drawable.ic_baseline_movie_24);break;
                case "Nightlife": eventCategoryIcon.setImageResource(R.drawable.ic_baseline_music_note_24);break;
                case "Culture": eventCategoryIcon.setImageResource(R.drawable.ic_baseline_museum_24);break;
                case "Education": eventCategoryIcon.setImageResource(R.drawable.ic_baseline_school_15);break;
                case "Game": eventCategoryIcon.setImageResource(R.drawable.ic_baseline_videogame_asset_24);break;
                case "Food": eventCategoryIcon.setImageResource(R.drawable.ic_baseline_fastfood_24);break;
                default: eventCategoryIcon.setImageResource(R.drawable.ic_baseline_error_24);
            }
        }
        void initializePicture(EventDTO eventItem){
            Picasso.get().load(eventItem.getPictures().get(0)).into(eventImage);
            Picasso.get().load(eventItem.getEventCreator().getProfilePictures().get(0)).into(eventOwnerProfilePicture);
        }
    }
}
