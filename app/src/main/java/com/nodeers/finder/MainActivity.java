package com.nodeers.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.nodeers.finder.fragments.FoundFragment;
import com.nodeers.finder.fragments.GetInFragment;
import com.nodeers.finder.fragments.LostFragment;
import com.nodeers.finder.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView btmNav;
    private ActionBar toolbar;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btmNav = findViewById(R.id.nav);
        toolbar = getSupportActionBar();

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


}