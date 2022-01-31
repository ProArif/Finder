package com.nodeers.finder.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nodeers.finder.R;


public class LostPersonFragment extends Fragment {



    public LostPersonFragment() {
        // Required empty public constructor
    }


    public static LostPersonFragment newInstance(String param1, String param2) {
        LostPersonFragment fragment = new LostPersonFragment();
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
        View v = inflater.inflate(R.layout.fragment_lost_person, container, false);


        return v;
    }
}