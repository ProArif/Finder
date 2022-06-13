package com.nodeers.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nodeers.finder.datamodels.MobileDataModel;
import com.nodeers.finder.fragments.FoundFragment;
import com.nodeers.finder.fragments.GetInFragment;
import com.nodeers.finder.fragments.LostFragment;
import com.nodeers.finder.fragments.LostMobileFragment;
import com.nodeers.finder.fragments.PoliceGetInFragment;
import com.nodeers.finder.fragments.ReportFragment;
import com.nodeers.finder.fragments.SettingsFragment;


public class MainActivity extends BaseActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navView;
    private Dialog dialog;

    private ActionBar toolbar;
    private Fragment fragment;
    private TextView tv_blink;

    private FirebaseUser  mUser;
    private FirebaseAuth mAuth;

    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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


        btmNav.setBackground(null);
        btmNav.setItemIconTintList(null);
        btmNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.lost:
                        toolbar.setTitle("Lost?");
                        fragment = new LostFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.found:
                        toolbar.setTitle("Found?");
                        loadFragment(new FoundFragment());

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

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){

                    case R.id.lost:
                        toolbar.setTitle("Lost");
                        loadFragment(new LostFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.mobile_side_nav:
                        toolbar.setTitle("Lost Mobile");
                        loadFragment(new LostMobileFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.found_side_nav:
                        toolbar.setTitle("Found");
                        loadFragment(new FoundFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.report:
                        toolbar.setTitle("Report");
                        loadFragment(new ReportFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.profile:
                        if (mUser != null) {
                            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            // No user is signed in
                            drawerLayout.closeDrawer(GravityCompat.START);
                            showGetInOptionsDialog();
                            Toast.makeText(MainActivity.this,"Please login!",Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case R.id.add:
                        if (mUser != null) {
                            toolbar.setTitle("Upload");
                    // User is signed in
                            showDialog();
                        } else {
                    // No user is signed in
                    Toast.makeText(MainActivity.this,"Please login to add a post",Toast.LENGTH_LONG).show();
                    showGetInOptionsDialog();
                }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }

                return false;
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }
        if (item.getItemId() == R.id.search){
            startActivity(new Intent(this,SearchResultsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

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
        //builder.setTitle("Choose One");
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
        ImageButton btn_mobile = customLayout.findViewById(R.id.imageButtonMobile);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        btn_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this,AddPersonDataActivity.class));
            }
        });

        btn_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this,AddVehicleDataActivity.class));
            }
        });

        btn_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showMobileDataForm();
            }
        });


    }

    //for mobile data input
    public void showMobileDataForm(){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter below information's");


        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.mobile_data_form, null);
        builder.setView(customLayout);

        EditText edtModel = customLayout.findViewById(R.id.edtModelName);
        EditText edtIMEI = customLayout.findViewById(R.id.edtIMEI);
        Button btnCancel = customLayout.findViewById(R.id.cancel);
        Button btnSubmitData = customLayout.findViewById(R.id.submitMobileData);


        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);

        btnSubmitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model = edtModel.getText().toString().trim();
                String imei = edtIMEI.getText().toString().trim();

                if (model.isEmpty()){
                    edtModel.setError("Please enter your model name");
                }
                else if (imei.isEmpty()){
                    edtIMEI.setError("Please enter your imei");
                }
                else if (imei.length() != 15){
                    edtIMEI.setError("Please enter your imei correctly");
                }
                else{
                    MobileDataModel mobileDataModel = new MobileDataModel();
                    mobileDataModel.setModelName(model);
                    mobileDataModel.setMobileImei(imei);
                    mDatabase = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    dbRef = mDatabase.getReference("lost mobiles");
                    showProgressBAr();
                    mobileDataModel = new MobileDataModel(mobileDataModel.getModelName(),mobileDataModel.getMobileImei());

                    if (mUser != null){
                        Log.e("firebase", "entered user not null");


                        dbRef.push().setValue(mobileDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.e("firebase", "entered task complete");
                                    hideProgressBar();
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this,"Submitted successfully",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(MainActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                    Log.e("firebase", String.valueOf(task.getResult()));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressBar();
                                dialog.dismiss();
                                Log.e("firebase", String.valueOf(e.getMessage()));
                            }
                        });
                    }
                    else{
                        hideProgressBar();
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,"Please login",Toast.LENGTH_LONG).show();

                    }
                }
            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showProgressBAr(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideProgressBar(){
        dialog.dismiss();
    }

    //for custom get in choice options
    public void showGetInOptionsDialog (){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a category to login");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

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