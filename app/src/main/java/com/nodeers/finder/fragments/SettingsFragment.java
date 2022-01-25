package com.nodeers.finder.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nodeers.finder.LogOutActivity;
import com.nodeers.finder.MainActivity;
import com.nodeers.finder.R;


public class SettingsFragment extends Fragment {

    private CardView cv_logout;
    private FirebaseUser users;
    private FirebaseAuth mAuth;


    public SettingsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View ve = inflater.inflate(R.layout.fragment_settings, container, false);


        mAuth = FirebaseAuth.getInstance();
        users = mAuth.getCurrentUser();

        cv_logout = ve.findViewById(R.id.card_view_logout);

        cv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (users != null) {
                    // signout user
                    alertsignout();


                } else {
                    // No user is signed in
                    Toast.makeText(getContext(),"No user signed in!!",Toast.LENGTH_LONG).show();
                }

            }
        });



        return ve;
    }

    public void alertsignout()
    {
        AlertDialog.Builder SignOutAlertDialog = new
                AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        SignOutAlertDialog.setTitle("Confirm SignOut");

        // Setting Dialog Message
        SignOutAlertDialog.setMessage("Are you sure you want to Signout?");

        // Setting Positive "Yes" Btn
        SignOutAlertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        mAuth.signOut();
                        Toast.makeText(getContext(),"Successfully logged out!",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getActivity(),
                                LogOutActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });

        // Setting Negative "NO" Btn
        SignOutAlertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Toast.makeText(getContext(),
                                "You clicked on NO", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        SignOutAlertDialog.show();


    }

}