package com.nodeers.finder.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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


public class WantedFragment extends Fragment {


    private GridView wanted_gv;
    private ArrayList<LostPersonDataModel> dataModel_lost_person ;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;

    private LostPersonGridVAdapter adapter;

    private Dialog dialog;

    public WantedFragment() {
        // Required empty public constructor
    }


    public static WantedFragment newInstance(String param1, String param2) {
        WantedFragment fragment = new WantedFragment();
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
        View layout = inflater.inflate(R.layout.fragment_wanted, container, false);

        //initialize variables for grid data
        wanted_gv = layout.findViewById(R.id.wanted_grid);
        dataModel_lost_person = new ArrayList<>();

        adapter = new LostPersonGridVAdapter(getContext(),dataModel_lost_person);
        wanted_gv.setAdapter(adapter);

        // initializing our variable for firebase
        // realtime db and getting its instance.
        store_data = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = store_data.getReference("wanted_persons");

        loadData();

        return layout;
    }

    private void loadData() {
        showProgressBAr();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataModel_lost_person.clear();

                if (snapshot.exists()){
                    hideProgressBar();
                    for (DataSnapshot dsnapshot : snapshot.getChildren()) {

                        LostPersonDataModel data = dsnapshot.getValue(LostPersonDataModel.class);
                        data.setName(dsnapshot.child("name").getValue().toString());
                        data.setImgUrl(dsnapshot.child("imgUrl").getValue().toString());
                        Log.e("entered snapshot",data.getName());

                        dataModel_lost_person.add(data);
                    }
                }else {
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