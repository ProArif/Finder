package com.nodeers.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.nodeers.finder.fragments.AddLostFoundPersonDataFragment;
import com.nodeers.finder.fragments.AddVehicleDataFragment;
import com.nodeers.finder.fragments.FoundFragment;
import com.nodeers.finder.fragments.GetInFragment;
import com.nodeers.finder.fragments.LostFragment;
import com.nodeers.finder.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;
    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new LostFragment());
        BottomNavigationView btmNav = findViewById(R.id.nav);
        toolbar = getSupportActionBar();
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new AddLostFoundPersonDataFragment());
                showDialog();
            }
        });
        
        btmNav.setBackground(null);

        btmNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.lost:
                        toolbar.setTitle("Lost Someone/Vehicle?");
                        fragment = new LostFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.found:
                        toolbar.setTitle("Found Someone/Vehicle?");
                        fragment = new FoundFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.profile:
                        toolbar.setTitle("Login/Profile");
                        fragment = new GetInFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.settings:
                        toolbar.setTitle("Settings");
                        fragment = new SettingsFragment();
                        loadFragment(fragment);
                        return true;

                }
                return false;
            }
        });


    }



    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //for custom choice options

    public void showDialog (){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose One");

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
                loadFragment(new AddLostFoundPersonDataFragment());
            }
        });

        btn_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadFragment(new AddVehicleDataFragment());
            }
        });


    }


}