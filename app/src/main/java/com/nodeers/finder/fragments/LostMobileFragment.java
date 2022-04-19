package com.nodeers.finder.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nodeers.finder.R;
import com.nodeers.finder.adapters.MobileDataAdapter;
import com.nodeers.finder.datamodels.MobileDataModel;
import com.nodeers.finder.datamodels.VehicleDataModel;

import java.util.ArrayList;


public class LostMobileFragment extends Fragment {


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MobileDataModel> data;
    static View.OnClickListener myOnClickListener;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;

    private Dialog dialog;

    public LostMobileFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LostMobileFragment newInstance(String param1, String param2) {
        LostMobileFragment fragment = new LostMobileFragment();
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
        View v =  inflater.inflate(R.layout.fragment_lost_mobile, container, false);

        data = new ArrayList<>();
        recyclerView = v.findViewById(R.id.mobile_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MobileDataAdapter(data);
        recyclerView.setAdapter(adapter);


        store_data = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = store_data.getReference("lost mobiles");

        loadData();



        return v;
    }
    private void loadData() {

        showProgressBAr();
        dbRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();

                if (snapshot.exists()){
                    hideProgressBar();
                    for (DataSnapshot dsnapshot : snapshot.getChildren()) {

                        MobileDataModel mdata = dsnapshot.getValue(MobileDataModel.class);
                        mdata.setModelName(dsnapshot.child("modelName").getValue().toString());
                        mdata.setMobileImei(dsnapshot.child("mobileImei").getValue().toString());

                        data.add(mdata);
                    }
                }else{
                    hideProgressBar();
                    // create an alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("No Data Found!");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(true);
                    dialog.show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Retrieval Error",error.getMessage());
            }
        });

    }

    private void showProgressBAr(){
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideProgressBar(){
        dialog.dismiss();
    }
}