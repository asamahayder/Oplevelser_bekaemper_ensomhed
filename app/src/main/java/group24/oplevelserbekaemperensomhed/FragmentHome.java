package group24.oplevelserbekaemperensomhed;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.data.DummyData;
import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;

public class FragmentHome extends Fragment implements EventItemClickListener{

    private ViewPager2 viewPager;
    private EventsAdapter adapter = null;
    private DummyData dummyData;
    ArrayList<EventDTO> events;

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

    /*@Override
    public void onResume() {
        super.onResume();
        System.out.println("*********************************************************************************************************");
        dummyData = new DummyData();
        if (adapter == null){
            FirebaseDAO firebaseDAO = new FirebaseDAO();
            firebaseDAO.getEvents(new MyCallBack() {
                @Override
                public void onCallBack(@NotNull Object object) {
                    ArrayList<EventDTO> events = (ArrayList<EventDTO>)object;
                    initializeAdapter(events);
                    viewPager.setAdapter(adapter);
                }
            });
        }

    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseDAO firebaseDAO = new FirebaseDAO();

        events = new ArrayList<>();
        adapter = new EventsAdapter(events, this);

        //dummyData = new DummyData();
        firebaseDAO.getEvents(new MyCallBack() {
            @Override
            public void onCallBack(@NotNull Object object) {
                ArrayList<EventDTO> events = (ArrayList<EventDTO>)object;
                updateAdapter(events);
            }
        });

    }

    private void updateAdapter(ArrayList<EventDTO> events){
        this.events.clear();
        this.events.addAll(events);
        adapter.notifyDataSetChanged();
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