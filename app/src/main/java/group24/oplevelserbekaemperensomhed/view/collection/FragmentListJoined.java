package group24.oplevelserbekaemperensomhed.view.collection;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.R;
import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.logic.FragmentListAdapter;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;


//The fragment for the Joined Events -part of the List menu
public class FragmentListJoined extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView textView;

    public FragmentListJoined() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        getJoinedEvents();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_joined, container, false);

        recyclerView = v.findViewById(R.id.fragment_list_joined_recyclerview);
        progressBar = v.findViewById(R.id.fragment_list_joined_progressBar);
        textView = v.findViewById(R.id.fragment_list_joined_text);

        getJoinedEvents();

        return v;
    }

    //Fetches joined events from the database
    private void getJoinedEvents(){
        FirebaseDAO firebaseDAO = new FirebaseDAO();
        firebaseDAO.getJoinedEvents(new MyCallBack() {
            @Override
            public void onCallBack(@NotNull Object object) {
                ArrayList<EventDTO> events = (ArrayList<EventDTO>)object;
                if (events.isEmpty()){
                    textView.setVisibility(View.VISIBLE);
                }else{
                    textView.setVisibility(View.GONE);
                }
                FragmentListAdapter adapter = new FragmentListAdapter(getActivity(), events);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}