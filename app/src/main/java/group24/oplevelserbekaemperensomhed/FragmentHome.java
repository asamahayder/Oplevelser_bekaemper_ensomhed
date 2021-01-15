package group24.oplevelserbekaemperensomhed;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.data.UserDTO;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler;

public class FragmentHome extends Fragment implements ItemClickListener {

    private ViewPager2 viewPager;
    private EventsAdapter adapter = null;
    private ArrayList<EventDTO> events = new ArrayList<>();

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
        FirebaseDAO firebaseDAO = new FirebaseDAO();
        firebaseDAO.getEvents(new MyCallBack() {
            @Override
            public void onCallBack(@NotNull Object object) {
                events = (ArrayList<EventDTO>)object;
                initializeAdapter();
                viewPager.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseDAO firebaseDAO = new FirebaseDAO();

        //dummyData = new DummyData();
        firebaseDAO.getEvents(new MyCallBack() {
            @Override
            public void onCallBack(@NotNull Object object) {
                events = (ArrayList<EventDTO>)object;
                initializeAdapter();
            }
        });



    }

    private void initializeAdapter(){
        adapter = new EventsAdapter(events, this);
    }


    @Override
    public void onEventItemClick(int position, EventDTO event, View title) {
        Intent intent = new Intent(getContext(), ActivityFragmentHandler.class);
        intent.putExtra("event", event);
        intent.putExtra("other", "other");
        startActivity(intent);

    }

    @Override
    public void onProfileItemClick(int position, UserDTO user, View title) {
        Intent intent = new Intent(getContext(), ActivityFragmentHandler.class);
        intent.putExtra("profile", user);
        intent.putExtra("other", "other");
        startActivity(intent);
    }
}