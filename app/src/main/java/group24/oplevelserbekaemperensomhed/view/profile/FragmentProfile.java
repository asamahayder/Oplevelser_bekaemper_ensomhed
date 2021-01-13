package group24.oplevelserbekaemperensomhed.view.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter;


public class FragmentProfile extends Fragment {

    private ArrayList<TextView> profileTextViews;

    private UserDTO userData;

    private LinearLayout linearLayout;

    private final String TAG = "fragmentProfile";

    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profileTextViews = new ArrayList<>();

        initializeView(v);

        return v;
    }

    private void initializeView(View view){
        linearLayout = view.findViewById(R.id.fragment_profile_infoLinearLayout);
        ImageView editProfileButton = view.findViewById(R.id.fragment_profile_editButton);
        profileTextViews.add((TextView) view.findViewById(R.id.fragment_profile_nameAge));
        ImageView backButton = view.findViewById(R.id.fragment_profile_backButton);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userData = bundle.getParcelable("profile");
            if (bundle.getString("other") != null) {
                editProfileButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getFragmentManager() != null) {
                            getFragmentManager().popBackStack();
                        }
                    }
                });
            }
        }

        linearLayout.removeAllViews();

        updateProfile();
        handlePfpSlider(view);
        handleAboutSection();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AProfileEdit.class);
                startActivity(intent);
            }
        });

    }


    private void handlePfpSlider(View view){
        ViewPager mPager = view.findViewById(R.id.fragment_profile_viewpager);
        TabLayout tabLayout = view.findViewById(R.id.fragment_profile_tabLayout);
        tabLayout.setupWithViewPager(mPager, true);
        tabLayout.setTabTextColors(Color.RED, Color.WHITE);
        assert getFragmentManager() != null;
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), userData.getProfilePictures(), R.layout.fragment_profile_event_1_viewpager, null);
        mPager.setAdapter(pagerAdapter);

        if (tabLayout.getTabCount() == 1){
            tabLayout.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0,0,10,0);
            tab.requestLayout();
        }
    }

    private void updateProfile(){
        if (userData == null){
            LocalData localData = LocalData.INSTANCE;
            userData = localData.getUserData();
        }
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