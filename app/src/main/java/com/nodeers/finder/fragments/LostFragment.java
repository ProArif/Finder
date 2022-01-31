package com.nodeers.finder.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nodeers.finder.R;
import com.nodeers.finder.adapters.LostAdapter;
import com.nodeers.finder.datamodels.LostPersonDataModel;

import java.util.ArrayList;


public class LostFragment extends Fragment {

    private ViewPager2 viewPager;
    private LostAdapter lostAdapter;
    private TabLayout tabLayout;

    //declare variables for grid data
    private GridView gridView;
    private ArrayList<LostPersonDataModel> dataModel_lost_person ;
    private FirebaseFirestore store_data;

    public LostFragment() {
        // Required empty public constructor
    }


    public static LostFragment newInstance(String param1, String param2) {
        LostFragment fragment = new LostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tabLayout = view.findViewById(R.id.tab_layout);
        lostAdapter = new LostAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(lostAdapter);

        //initialize variables for grid data


        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0){
                            tab.setText("Lost Person?");
                        }
                        if (position == 1){
                            tab.setText("Lost Vehicle?");
                        }
                    }
                }).attach();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lost, container, false);
    }
}