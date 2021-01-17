package group24.oplevelserbekaemperensomhed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.data.LocalData;
import group24.oplevelserbekaemperensomhed.data.UserDTO;
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler;


public class FragmentEventInfo extends Fragment {

    private EventDTO event;
    private TextView eventNameTextView;
    private TextView eventPlaceTextView;
    private TextView eventPriceTextView;
    private TextView eventTimeTextView;
    private TextView eventBioTextView;
    private ImageView eventBackButton;
    private ViewPager mPager;
    private TabLayout tabLayout;
    private ImageView categoryIcon;
    private TextView categoryName;
    private TextView timeStart;
    private TextView timeEnd;
    private TextView eventOwnerName;
    private LinearLayout joinButton;
    private ImageView joinButtonIcon;
    private TextView joinButtonText;
    private LinearLayout participantLayout;
    private boolean joined;

    private ProgressDialog progressDialog;

    private ArrayList<UserDTO> participants;

    public FragmentEventInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        eventNameTextView = v.findViewById(R.id.eventName);
        eventPlaceTextView = v.findViewById(R.id.aevent_info_address);
        eventPriceTextView = v.findViewById(R.id.aevent_info_price);
        eventTimeTextView = v.findViewById(R.id.aevent_info_time);
        eventBioTextView = v.findViewById(R.id.aevent_info_bio);
        eventBackButton = v.findViewById(R.id.activity_register_details_backButton);
        categoryIcon = v.findViewById(R.id.aevent_info_category_icon);
        categoryName = v.findViewById(R.id.aevent_info_category_text);
        timeStart = v.findViewById(R.id.aevent_info_clock_start_text);
        timeEnd = v.findViewById(R.id.aevent_info_clock_end_text);
        joinButton = v.findViewById(R.id.event_info_submitButton);
        eventOwnerName = v.findViewById(R.id.event_info_creator_name);
        participantLayout = v.findViewById(R.id.event_info_participants_list);
        joinButtonIcon = v.findViewById(R.id.event_info_submitButton_icon);
        joinButtonText = v.findViewById(R.id.event_info_submitButton_text);

        progressDialog = new ProgressDialog(getActivity());

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnSubmit();
            }
        });


        //Getting event from parent fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = bundle.getParcelable("event");
        }else{
            Toast toast = Toast.makeText(getActivity(), "Could not retrieve event", Toast.LENGTH_SHORT);
            toast.show();
        }


        //placing data inside the views
        eventNameTextView.setText(event.getEventTitle());
        eventPlaceTextView.setText(event.getAddress());
        eventPriceTextView.setText(event.getPrice());
        eventBioTextView.setText(event.getEventDescription());
        timeStart.setText("Start time: " + event.getEventDate().getStartTime());
        timeEnd.setText("End time: " + event.getEventDate().getEndTime());
        eventOwnerName.setText(event.getEventCreator().getName());
        handleCategories();

        //Setting date field
        String date = event.getEventDate().getDate();


        eventTimeTextView.setText(date);


        //back button functionality
        eventBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        handlePictureSlider(v);

        handleParticipants();

        return v;
    }

    //Checks if joined and changes submit button based on result
    private void updateParticipationStatus(ArrayList<UserDTO> participants){
        joined = false;
        LocalData localData = LocalData.INSTANCE;
        for (UserDTO participant : participants) {
            if (participant.getName().equals(localData.getUserData().getName())){
                joined = true;
            }
        }

        if (joined){
            joinButtonText.setText("Leave");
            //joinButtonIcon.setVisibility(View.VISIBLE);
        }else{
            joinButtonText.setText("Join");
            joinButtonIcon.setVisibility(View.GONE);
        }
    }

    private void handlePictureSlider(View view){
        mPager = view.findViewById(R.id.event_info_viewpager);
        tabLayout = view.findViewById(R.id.event_info_tablayout);
        tabLayout.setupWithViewPager(mPager, true);
        tabLayout.setTabTextColors(Color.RED, Color.WHITE);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), event.getPictures(), R.layout.fragment_profile_event_1_viewpager, null);
        mPager.setAdapter(pagerAdapter);

        if (tabLayout.getTabCount() == 1){
            tabLayout.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0,0,10,0);
            tab.requestLayout();
        }

    }

    private void handleCategories(){
        String categoryName = event.getCategory();
        switch (categoryName){
            case "Sport": categoryIcon.setImageResource(R.drawable.ic_baseline_directions_run_24); break;
            case "Fun": categoryIcon.setImageResource(R.drawable.ic_baseline_movie_24);break;
            case "Nightlife": categoryIcon.setImageResource(R.drawable.ic_baseline_music_note_24);break;
            case "Culture": categoryIcon.setImageResource(R.drawable.ic_baseline_museum_24);break;
            case "Education": categoryIcon.setImageResource(R.drawable.ic_baseline_school_15);break;
            case "Game": categoryIcon.setImageResource(R.drawable.ic_baseline_videogame_asset_24);break;
            case "Food": categoryIcon.setImageResource(R.drawable.ic_baseline_fastfood_24);break;
            default: categoryIcon.setImageResource(R.drawable.ic_baseline_error_24);
        }
        this.categoryName.setText(categoryName);
    }

    private void handleOnSubmit(){
        LocalData localData = LocalData.INSTANCE;
        progressDialog.setTitle("Please wait");
        progressDialog.show();

        FirebaseDAO firebaseDAO = new FirebaseDAO();
        if (joined){
            //TODO use delete pair here

            if (!event.getEventCreator().getName().equals(localData.getUserData().getName())){ //An owner cannot un-join his own event
                firebaseDAO.deleteParticipantPair(event.getEventTitle(), new MyCallBack() {
                    @Override
                    public void onCallBack(@NotNull Object object) {
                        String resultMessage = (String) object;
                        if (resultMessage.equals("success")){
                            handleParticipants();
                        }else{
                            Toast.makeText(getActivity(), "Could not delete pair", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "An owner has to participate in his own event", Toast.LENGTH_SHORT).show();
            }

        }else{
            firebaseDAO.createParticipantPair(event.getEventTitle(), new MyCallBack(){
                @Override
                public void onCallBack(@NotNull Object object) {
                    String message = (String)object;
                    if (message.equals("success")){
                        handleParticipants();
                    }else{
                        Toast.makeText(getActivity(), "Could not join", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                }
            });
        }


    }

    private void handleParticipants(){
        FirebaseDAO firebaseDAO = new FirebaseDAO();
        firebaseDAO.getParticipants(event, new MyCallBack() {
            @Override
            public void onCallBack(@NotNull Object object) {
                ArrayList<UserDTO> participants = (ArrayList<UserDTO>) object;
                setParticipants(participants);
                insertParticipants(participants);
                updateParticipationStatus(participants);

                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void insertParticipants(ArrayList<UserDTO> participants){
        participantLayout.removeAllViews();
        for (final UserDTO user : participants) {
            CardView cardView = new CardView(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int marginSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
            layoutParams.setMargins(marginSize, 0, marginSize, 0);
            cardView.setLayoutParams(layoutParams);
            cardView.setRadius(250);
            cardView.setPadding(10,0,10,0);

            ImageView imageView = new ImageView(getActivity());
            int imageSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(user.getProfilePictures().get(0)).into(imageView);
            cardView.addView(imageView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ActivityFragmentHandler.class);
                    intent.putExtra("profile", user);
                    intent.putExtra("other", "other");
                    getActivity().startActivity(intent);
                }
            });
            participantLayout.addView(cardView);


        }

    }

    public void setParticipants(ArrayList<UserDTO> participants) {
        this.participants = participants;
    }
}