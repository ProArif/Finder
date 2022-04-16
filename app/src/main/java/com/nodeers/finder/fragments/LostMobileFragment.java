package com.nodeers.finder.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nodeers.finder.R;


public class LostMobileFragment extends Fragment {



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
        return inflater.inflate(R.layout.fragment_lost_mobile, container, false);
    }
}