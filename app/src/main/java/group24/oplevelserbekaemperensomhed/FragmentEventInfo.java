package group24.oplevelserbekaemperensomhed;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter;


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
    private LinearLayout joinButton;

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
        eventBackButton = v.findViewById(R.id.aevent_info_backButton);
        categoryIcon = v.findViewById(R.id.aevent_info_category_icon);
        categoryName = v.findViewById(R.id.aevent_info_category_text);
        timeStart = v.findViewById(R.id.aevent_info_clock_start_text);
        timeEnd = v.findViewById(R.id.aevent_info_clock_end_text);
        joinButton = v.findViewById(R.id.event_info_submitButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnSubmmit();
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
        handleCategories();

        //TODO there is probably a better way to do this:
        //Setting date field
        String date = event.getEventDate().getDate();


        eventTimeTextView.setText(date);


        //back button functionality
        eventBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }else{
                    Toast toast = Toast.makeText(getActivity(), "Could not get fragment manager", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        handlePictureSlider(v);

        return v;
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

    private void handleOnSubmmit(){
        //TODO implement join event here
    }

}