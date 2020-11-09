package group24.oplevelserbekaemperensomhed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.data.LocalData;
import group24.oplevelserbekaemperensomhed.data.UserDTO;
import group24.oplevelserbekaemperensomhed.profile.ProfilePicSliderPagerAdapter;


public class FragmentProfile extends Fragment {

    private ViewPager mPager;
    private TabLayout tabLayout;
    private ArrayList<TextView> profileTextViews;
    private ArrayList<ImageView> icons;

    private UserDTO userData;

    private LinearLayout linearLayout;

    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profileTextViews = new ArrayList<>();
        icons = new ArrayList<>();

        TextView textView = v.findViewById(R.id.aprofile_nameAge);
        initializeView(v);
        updateProfile();
        handlePfpSlider(v);
        handleActivityChanges(v);
        handleAboutSection();

        return v;
    }

    private void initializeView(View view){
        linearLayout = view.findViewById(R.id.profileInfoLinearLayout);
        profileTextViews.add((TextView) view.findViewById(R.id.aprofile_nameAge));
        profileTextViews.add((TextView) view.findViewById(R.id.aprofile_bio));
        icons.add((ImageView) view.findViewById(R.id.aprofile_addressIcon));
        icons.add((ImageView) view.findViewById(R.id.aprofile_occupationIcon));
        icons.add((ImageView) view.findViewById(R.id.aprofile_educationIcon));
    }

    private void handleActivityChanges(View view){
        ImageView editProfileButton = view.findViewById(R.id.aprofile_editProfileButton);
        ImageView backButton = view.findViewById(R.id.aprofile_backButton);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement this
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement this
            }
        });

    }


    private void handlePfpSlider(View view){
        mPager = view.findViewById(R.id.aprofile_viewpager);
        tabLayout = view.findViewById(R.id.aprofile_tablayout);
        tabLayout.setupWithViewPager(mPager, true);
        tabLayout.setTabTextColors(Color.RED, Color.WHITE);
        ProfilePicSliderPagerAdapter pagerAdapter = new ProfilePicSliderPagerAdapter(getActivity().getSupportFragmentManager(), userData.getProfilePictures());
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

    private void updateProfile(){
        LocalData localData = LocalData.INSTANCE;
        userData = localData.getUserData();
        handleProfileInfo();
        handleProfileIcons();
    }

    @SuppressLint("SetTextI18n")
    private void handleProfileInfo(){
        if (userData.getName() != null){
            profileTextViews.get(0).setText(userData.getName());
            if (userData.getAge()!=null){
                String text = userData.getName() + ", " + userData.getAge();
                profileTextViews.get(0).setText(text);
            }
        }
    }


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