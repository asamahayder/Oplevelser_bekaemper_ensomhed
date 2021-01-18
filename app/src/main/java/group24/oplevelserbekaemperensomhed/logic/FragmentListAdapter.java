package group24.oplevelserbekaemperensomhed.logic;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.R;
import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.data.UserDTO;
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler;


//The adpater for the recyclerview that holds the lists inside the List menu.
public class FragmentListAdapter extends RecyclerView.Adapter<FragmentListAdapter.ViewHolder> {

    private ArrayList<EventDTO> events;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView itemCategory;
        private final TextView itemDate;
        private final ImageView imageView;
        private final ConstraintLayout constraintLayout;


        public ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.fragment_list_item_name);
            imageView = view.findViewById(R.id.fragment_list_item_picture);
            itemCategory = view.findViewById(R.id.fragment_list_item_category);
            itemDate = view.findViewById(R.id.fragment_list_item_date);
            constraintLayout = view.findViewById(R.id.fragment_list_item_layout);
        }

        public TextView getItemName() {
            return itemName;
        }

        public TextView getItemCategory() {
            return itemCategory;
        }

        public TextView getItemDate() {
            return itemDate;
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public void handleImage(String url, final Context context, final UserDTO userDTO){
            Picasso.get().load(url).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityFragmentHandler.class);
                    intent.putExtra("profile", userDTO);
                    intent.putExtra("other", "other");
                    context.startActivity(intent);
                }
            });
        }

    }

    public FragmentListAdapter(Context context, ArrayList<EventDTO> events) {
        this.events = events;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.getItemName().setText(events.get(position).getEventTitle());
        viewHolder.getItemDate().setText(events.get(position).getEventDate().getDate());
        viewHolder.getItemCategory().setText(events.get(position).getCategory());
        viewHolder.handleImage(events.get(position).getEventCreator().getProfilePictures().get(0), context, events.get(position).getEventCreator());

        viewHolder.getConstraintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityFragmentHandler.class);
                intent.putExtra("event", events.get(position));
                intent.putExtra("other", "other");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

