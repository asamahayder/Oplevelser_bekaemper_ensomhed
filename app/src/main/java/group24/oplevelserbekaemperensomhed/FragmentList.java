package group24.oplevelserbekaemperensomhed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

//Inspired by https://droidbyme.medium.com/android-material-design-tabs-tab-layout-with-swipe-884085ae80ff

public class FragmentList extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter tabAdapter;



    public FragmentList() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        tabLayout = v.findViewById(R.id.fragment_list_tabLayout);
        viewPager = v.findViewById(R.id.fragment_list_viewPager);

        tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        tabAdapter.addFragment(new FragmentListJoined(), "Joined");
        tabAdapter.addFragment(new FragmentListCreated(), "Created");


        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);


        return v;
    }
}