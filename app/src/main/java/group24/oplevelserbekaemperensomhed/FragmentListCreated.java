package group24.oplevelserbekaemperensomhed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.data.LocalData;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;
import group24.oplevelserbekaemperensomhed.view.ActivityFragmentHandler;


public class FragmentListCreated extends Fragment {

    RecyclerView recyclerView;

    public FragmentListCreated() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_created, container, false);

        recyclerView = v.findViewById(R.id.fragment_list_created_recyclerview);

        getCreatedEvents();

        return v;
    }

    private void getCreatedEvents(){
        FirebaseDAO firebaseDAO = new FirebaseDAO();
        firebaseDAO.getCreatedEvents(new MyCallBack() {
            @Override
            public void onCallBack(@NotNull Object object) {
                ArrayList<EventDTO> events = (ArrayList<EventDTO>) object;
                FragmentListAdapter adapter = new FragmentListAdapter(getActivity(), events);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });
    }
}