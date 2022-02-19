package com.nodeers.finder.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nodeers.finder.AddPersonDataActivity;
import com.nodeers.finder.AddVehicleDataActivity;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.LostPersonDataModel;
import com.nodeers.finder.datamodels.UsersData;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    private TextView tv_upPerson,tv_upVehicle,tv_profile_name,tv_profile_phn;


    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;
    private ArrayList<UsersData> usersData;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        store_data = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = store_data.getReference("users");

        usersData = new ArrayList<>();
        
        // Inflate the layout for this fragment
        View page = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_upPerson = page.findViewById(R.id.tv_upldprsnData);
        tv_upVehicle = page.findViewById(R.id.tv_upldVhclData);
        tv_profile_name = page.findViewById(R.id.user_profile_name);
        tv_profile_phn = page.findViewById(R.id.user_profile_phn);

        loadData();

        tv_upPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddPersonDataActivity.class));
            }
        });

        tv_upVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddVehicleDataActivity.class));
            }
        });


        return page;
    }

    private void loadData() {

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersData.clear();

                for (DataSnapshot dsnapshot : snapshot.getChildren()) {

                    UsersData data = dsnapshot.getValue(UsersData.class);
                    if (data != null) {

                        String name = data.getName();
                        String phn = data.getPhn();

                        tv_profile_name.setText(name);
                        tv_profile_phn.setText(phn);
                    }

                    //data.setImgUrl(dsnapshot.child("imgUrl").getValue().toString());
                    //Log.e("entered snapshot",data.getName());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Profile Retrieval Error",error.getMessage());
            }
        });


    }
}