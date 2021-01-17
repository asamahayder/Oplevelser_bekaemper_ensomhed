package group24.oplevelserbekaemperensomhed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

//Inspired by https://droidbyme.medium.com/android-material-design-tabs-tab-layout-with-swipe-884085ae80ff

public class FragmentList extends Fragment {

    private TabLayout tabLayout;
    private TabAdapter tabAdapter;



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

        tabLayout.addTab(tabLayout.newTab().setText("joined"));
        tabLayout.addTab(tabLayout.newTab().setText("created"));

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