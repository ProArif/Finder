package com.nodeers.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nodeers.finder.fragments.AddLostFoundPersonDataFragment;
import com.nodeers.finder.fragments.AddVehicleDataFragment;
import com.nodeers.finder.fragments.FoundFragment;
import com.nodeers.finder.fragments.GetInFragment;
import com.nodeers.finder.fragments.LostFragment;
import com.nodeers.finder.fragments.PoliceGetInFragment;
import com.nodeers.finder.fragments.ProfileFragment;
import com.nodeers.finder.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navView;

    private ActionBar toolbar;
    private Fragment fragment;
    private TextView tv_blink;

    private FirebaseUser  mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        navView = findViewById(R.id.side_nav);
        navView.setItemIconTintList(null);


        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //for safetynet app verification
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

//        tv_blink = findViewById(R.id.blinktext);
//        BlinkTxtAnime();
        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    mUser = mAuth.getCurrentUser();

                }else
                {

                }
            }
        });

        loadFragment(new LostFragment());
        BottomNavigationView btmNav = findViewById(R.id.nav);
        toolbar = getSupportActionBar();
        //FloatingActionButton fab = findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mUser != null) {
//                    // User is signed in
//                    showDialog();
//                } else {
//                    // No user is signed in
//                    Toast.makeText(MainActivity.this,"Please login to add a post",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
        
        btmNav.setBackground(null);
        btmNav.setItemIconTintList(null);
        btmNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.lost:
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
                        if (mUser != null) {
                            fragment = new ProfileFragment();
                            loadFragment(fragment);
                        } else {
                            // No user is signed in
                            showGetInOptionsDialog();
                            Toast.makeText(MainActivity.this,"Please login!",Toast.LENGTH_LONG).show();
                        }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    private void BlinkTxtAnime(){
        // adding the color to be shown
        ObjectAnimator animator = ObjectAnimator.ofInt(tv_blink, "backgroundColor", Color.BLUE, Color.RED, Color.GREEN);

        // duration of one color
        animator.setDuration(500);
        animator.setEvaluator(new ArgbEvaluator());

        // color will be show in reverse manner
        animator.setRepeatCount(Animation.REVERSE);

        // It will be repeated up to infinite time
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
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

    //for custom choice options
    public void showGetInOptionsDialog (){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a category to login");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.category_chooser, null);
        builder.setView(customLayout);

        ImageButton btn_police = customLayout.findViewById(R.id.imageButtonPolice);
        ImageButton btn_general = customLayout.findViewById(R.id.imageButtonGeneral);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        btn_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadFragment(new PoliceGetInFragment());
            }
        });

        btn_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadFragment(new GetInFragment());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}