package group24.oplevelserbekaemperensomhed.view.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.R;
import group24.oplevelserbekaemperensomhed.data.LocalData;
import group24.oplevelserbekaemperensomhed.data.UserDTO;
import group24.oplevelserbekaemperensomhed.logic.Logic;
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter;
import group24.oplevelserbekaemperensomhed.settings.Settings;

// Handles profile details of every user

public class FragmentProfile extends Fragment {

    private ArrayList<TextView> profileTextViews;

    private UserDTO userData;

    private LinearLayout linearLayout;

    private final String TAG = "FragmentProfile";

    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        profileTextViews = new ArrayList<>();
        initializeView(v);
        return v;
    }

    private void initializeView(final View view){
        linearLayout = view.findViewById(R.id.fragment_profile_infoLinearLayout);
        ImageView editProfileButton = view.findViewById(R.id.fragment_profile_editButton);
        profileTextViews.add((TextView) view.findViewById(R.id.fragment_profile_nameAge));
        ImageView backButton = view.findViewById(R.id.fragment_profile_backButton);

        Bundle bundle = getArguments();
        // If there is a bundle, then it will contain a user object
        if (bundle != null) {
            userData = bundle.getParcelable("profile");
            // Checks if this is another user and not ones self
            if (bundle.getString("other") != null) {
                editProfileButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            }
        // Else it must be the current user profile that was clicked, therefore get the singleton user object
        } else {
            LocalData localData = LocalData.INSTANCE;
            userData = localData.getUserData();
            // Sets the backbutton to be the settings button (this is used when profile is a
            // fragment of the mainactivity, else it will be a normal backbutton)
            backButton.setVisibility(View.VISIBLE);
            backButton.setImageResource(R.drawable.ic_baseline_settings_24);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Settings.class);
                    startActivity(intent);
                }
            });
        }
        linearLayout.removeAllViews();

        // Updates the profile and the layout
        updateProfile();
        handlePfpSlider(view);
        handleAboutSection();

        // Handles the edit profile button (Is gone for when "other" bundle is passed)
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityEditProfile.class);
                startActivity(intent);
            }
        });

    }

    // Handles the profile picture viewpager and the bar beneath the images
    private void handlePfpSlider(View view){
        ViewPager mPager = view.findViewById(R.id.fragment_profile_viewpager);

        // Creates the tablayout beneath the profile pictures
        TabLayout tabLayout = view.findViewById(R.id.fragment_profile_tabLayout);
        tabLayout.setupWithViewPager(mPager, true);
        tabLayout.setTabTextColors(Color.RED, Color.WHITE);
        assert getFragmentManager() != null;

        // Instantiates the viewpager adapter
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), userData.getProfilePictures(), R.layout.fragment_profile_event_1_viewpager, null);
        mPager.setAdapter(pagerAdapter);

        // Sets the current picture's tablayout line to be visible
        if (tabLayout.getTabCount() == 1){
            tabLayout.setVisibility(View.INVISIBLE);
        }

        // Programmatically defines how the lines beneath the images will look like
        // depending on how many there are of profile pictures
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0,0,10,0);
            tab.requestLayout();
        }
    }

    // Updates the view when leaving the fragment and coming back
    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            initializeView(getView());
        }
    }

    // Handles updating profile
    private void updateProfile(){
        // Sets the user object to be localdata, if it's null
        if (userData == null){
            LocalData localData = LocalData.INSTANCE;
            userData = localData.getUserData();
        }
        // Updates the text fields and icons
        handleProfileInfo();
        handleProfileIcons();
    }

    // Sets all the text onto the fields
    @SuppressLint("SetTextI18n")
    private void handleProfileInfo(){
        if (userData.getName() != null){
            profileTextViews.get(0).setText(userData.getName());
            userData.getAge();

            // Calculates the age of the user based on the current date
            Logic logic = new Logic();
            String text = userData.getName() + ", " + logic.getAge(userData.getAge().get(0),userData.getAge().get(1),userData.getAge().get(2));
            profileTextViews.get(0).setText(text);
        }
    }

    // Programmatically updates the icons and locations depending on if there is an attribute or not
    private void handleProfileIcons(){
        if (userData.getAddress() != null){
            this.linearLayout.addView(createSmallInfoRow(R.drawable.ic_baseline_location_on_15, userData.getAddress()));
        }

        if (userData.getOccupation() != null){
            this.linearLayout.addView(createSmallInfoRow(R.drawable.ic_baseline_work_15, userData.getOccupation()));
        }

        if (userData.getEducation() != null){
            this.linearLayout.addView(createSmallInfoRow(R.drawable.ic_baseline_school_15, userData.getEducation()));
        }

    }

    // Creates the about section
    private void handleAboutSection(){
        if (userData.getAbout() != null){
            View view = new View(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
            params.setMargins(0,30,0,0);
            view.setLayoutParams(params);
            view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorEditBackground));

            TextView textView = new TextView(getActivity());
            textView.setText(getString(R.string.about));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorEditBackground));

            TextView aboutText = new TextView(getActivity());
            aboutText.setText(userData.getAbout());
            aboutText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            aboutText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorEditBackground));


            this.linearLayout.addView(view);
            this.linearLayout.addView(textView);
            this.linearLayout.addView(aboutText);
        }
    }

    // Handles the info rows and which ones come before each other
    private View createSmallInfoRow(int iconAddress, String txt){
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), iconAddress));
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(50,50);
        imageParams.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(imageParams);

        TextView textView = new TextView(getActivity());
        textView.setText(txt);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setPadding(50,10,10,10);
        textView.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorEditBackground));

        linearLayout.addView(imageView);
        linearLayout.addView(textView);

        return linearLayout;
    }

}