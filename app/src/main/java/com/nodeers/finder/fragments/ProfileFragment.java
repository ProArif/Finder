package com.nodeers.finder.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nodeers.finder.AddPersonDataActivity;
import com.nodeers.finder.AddVehicleDataActivity;
import com.nodeers.finder.MainActivity;
import com.nodeers.finder.R;
import com.nodeers.finder.SearchResultsActivity;
import com.nodeers.finder.datamodels.UsersData;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private CircleImageView profile_img;
    private Button btnUpload,btnSearch;
    private String userIdd;
    private TextView blinkingTxt,tv_profile_name,tv_profile_phn;

    private FirebaseUser mAppUser;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;

    private UsersData data;



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

        mAppUser = FirebaseAuth.getInstance().getCurrentUser();
        userIdd = mAppUser.getUid();

        data = new UsersData();
        
        // Inflate the layout for this fragment
        View page = inflater.inflate(R.layout.profile_page, container, false);

        btnUpload = page.findViewById(R.id.upload_data);
        btnSearch = page.findViewById(R.id.search_data);
        tv_profile_name = page.findViewById(R.id.user_profile_name);
        tv_profile_phn = page.findViewById(R.id.user_profile_phn);
        profile_img = page.findViewById(R.id.profile_img);

        blinkingTxt = page.findViewById(R.id.blinkingText);
        blinkingTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        blinkingTxt.setSelected(true);
        blinkingTxt.setSingleLine(true);

        loadData();


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAppUser != null){
                    showDialog();
                }else {
                    Toast.makeText(getContext(),"Please login to add a post",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchResultsActivity.class));
            }
        });

        return page;
    }

    private void loadData() {

        dbRef.child(userIdd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //needs to check Uid and then display accurate data according to logged in user.

                    data.setName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    data.setPhn(Objects.requireNonNull(snapshot.child("phn").getValue()).toString());

                    String name = data.getName();
                    String phn = data.getPhn();
                    tv_profile_name.setText(name);
                    tv_profile_phn.setText(phn);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Profile Retrieval Error",error.getMessage());
            }
        });


    }


    public void showDialog (){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose One");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.person_vehicle_chooser_layout, null);
        builder.setView(customLayout);

        ImageButton btn_person = customLayout.findViewById(R.id.imageButtonPerson);
        ImageButton btn_vehicle = customLayout.findViewById(R.id.imageButtonCar);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        btn_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getContext(),AddPersonDataActivity.class));
            }
        });

        btn_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getContext(),AddVehicleDataActivity.class));
            }
        });


    }
}