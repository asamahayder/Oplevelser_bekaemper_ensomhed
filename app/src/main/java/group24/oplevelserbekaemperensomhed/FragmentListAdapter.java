package group24.oplevelserbekaemperensomhed;


import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler;

public class FragmentListAdapter extends RecyclerView.Adapter<FragmentListAdapter.ViewHolder> {

    private ArrayList<EventDTO> events;
    private View.OnClickListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final ImageView imageView;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            itemName = view.findViewById(R.id.fragment_list_item_name);
            imageView = view.findViewById(R.id.fragment_list_item_picture);
        }

        public TextView getItemName() {
            return itemName;
        }


        public void handleImage(String url, View.OnClickListener onClickListener){

            Picasso.get().load(url).into(imageView);
            imageView.setOnClickListener(onClickListener);
        }

    }

    public FragmentListAdapter(ArrayList<EventDTO> events, View.OnClickListener onClickListener) {
        this.events = events;
        this.onClickListener = onClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getItemName().setText(events.get(position).getEventTitle());
        viewHolder.handleImage(events.get(position).getEventCreator().getProfilePictures().get(0), onClickListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return events.size();
    }
}

