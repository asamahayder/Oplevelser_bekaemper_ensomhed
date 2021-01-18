package group24.oplevelserbekaemperensomhed.view.collection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import group24.oplevelserbekaemperensomhed.R;

//Inspired by https://droidbyme.medium.com/android-material-design-tabs-tab-layout-with-swipe-884085ae80ff


//This class is the main class for the List menu item in the nav bar. It holds the two different created/joined fragments and puts them inside a tablayout.
public class FragmentList extends Fragment {

    private TabLayout tabLayout;

    public FragmentList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        tabLayout = v.findViewById(R.id.fragment_list_tabLayout);

        //Creating tabs
        tabLayout.addTab(tabLayout.newTab().setText("joined"));
        tabLayout.addTab(tabLayout.newTab().setText("created"));

        //starts on fragmentListJoined by default
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new FragmentListJoined()).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0){
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new FragmentListJoined()).commit();
                }else{
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new FragmentListCreated()).commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

}