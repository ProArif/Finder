package com.nodeers.finder.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.LostPersonDataModel;
import java.util.ArrayList;


public class LostPersonFragment extends Fragment {

    //declare variables for grid data
    private GridView gridView;
    private ArrayList<LostPersonDataModel> dataModel_lost_person ;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;

    public LostPersonFragment() {
        // Required empty public constructor
    }


    public static LostPersonFragment newInstance(String param1, String param2) {
        LostPersonFragment fragment = new LostPersonFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lost_person, container, false);

        //initialize variables for grid data
        gridView = v.findViewById(R.id.person_grid);
        dataModel_lost_person = new ArrayList<>();

        // initializing our variable for firebase
        // realtime db and getting its instance.
        store_data = FirebaseDatabase.getInstance();
        dbRef = store_data.getReference();

        loadData();

        return v;
    }

    private void loadData() {

    }
}