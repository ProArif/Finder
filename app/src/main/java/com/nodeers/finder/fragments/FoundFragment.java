package com.nodeers.finder.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nodeers.finder.R;
import com.nodeers.finder.adapters.LostPersonGridVAdapter;
import com.nodeers.finder.datamodels.LostPersonDataModel;

import java.util.ArrayList;


public class FoundFragment extends Fragment {

    private GridView found_gv;
    private ArrayList<LostPersonDataModel> dataModel_lost_person ;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;

    private LostPersonGridVAdapter adapter;


    public FoundFragment() {
        // Required empty public constructor
    }


    public static FoundFragment newInstance(String param1, String param2) {
        FoundFragment fragment = new FoundFragment();
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
        View lay = inflater.inflate(R.layout.fragment_found, container, false);
        //initialize variables for grid data
        found_gv = lay.findViewById(R.id.found_grid);
        dataModel_lost_person = new ArrayList<>();

        adapter = new LostPersonGridVAdapter(getContext(),dataModel_lost_person);
        found_gv.setAdapter(adapter);

        // initializing our variable for firebase
        // realtime db and getting its instance.
        store_data = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = store_data.getReference("found_entities");

        loadData();

        return lay;
    }

    private void loadData() {

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataModel_lost_person.clear();

                for (DataSnapshot dsnapshot : snapshot.getChildren()) {

                    LostPersonDataModel data = dsnapshot.getValue(LostPersonDataModel.class);
                    if (data != null) {
                        data.setName(dsnapshot.child("name").getValue().toString());
                        data.setImgUrl(dsnapshot.child("imgUrl").getValue().toString());
                        Log.e("entered snapshot",data.getName());

                        dataModel_lost_person.add(data);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Retrieval Error",error.getMessage());
            }
        });

    }
}