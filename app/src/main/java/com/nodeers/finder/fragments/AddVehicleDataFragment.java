package com.nodeers.finder.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.nodeers.finder.R;


public class AddVehicleDataFragment extends Fragment {

 private Spinner vehicle_l_f;
 private Button btnSubmit,btnFoundData;
 private EditText edtRegNo,edtEngineNo;

    public AddVehicleDataFragment() {
        // Required empty public constructor
    }


    public static AddVehicleDataFragment newInstance(String param1, String param2) {
        AddVehicleDataFragment fragment = new AddVehicleDataFragment();
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
        View vw = inflater.inflate(R.layout.fragment_add_vehicle_data, container, false);

        vehicle_l_f = vw.findViewById(R.id.spnr_lost_found_vehicle);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.choice_array2, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set prompt message
        vehicle_l_f.setPrompt("Select Lost/Found");

        // Apply the adapter to the spinner
        vehicle_l_f.setAdapter(adapter2);

        edtRegNo = vw.findViewById(R.id.editTextVehicleRegNo);
        edtEngineNo = vw.findViewById(R.id.editTextEngineNo);
        btnSubmit = vw.findViewById(R.id.btnSubmitVehicleData);
        btnFoundData = vw.findViewById(R.id.btnSubmitFoundVData);


        return vw;
    }
}