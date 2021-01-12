package group24.oplevelserbekaemperensomhed;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import group24.oplevelserbekaemperensomhed.data.DummyData;
import group24.oplevelserbekaemperensomhed.data.EventDTO;

public class FragmentHome extends Fragment implements EventItemClickListener{

    private ViewPager2 viewPager;
    private EventsAdapter adapter;
    private DummyData dummyData;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        viewPager = view.findViewById(R.id.view_pager_main);
        viewPager.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("*********************************************************************************************************");
        dummyData = new DummyData();
        adapter = new EventsAdapter(dummyData.getList(), this);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dummyData = new DummyData();
        adapter = new EventsAdapter(dummyData.getList(), this);
    }


    @Override
    public void onEventItemClick(int position, EventDTO event, View title) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", event);

        Fragment destinationFragment = new FragmentEventInfo();
        destinationFragment.setArguments(bundle);

        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(R.id.mainFragment, destinationFragment).addToBackStack(getString(R.string.fragment_home)).commit();
        }else{
            Toast toast = Toast.makeText(getActivity(), "Couldn't get fragment manager", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}