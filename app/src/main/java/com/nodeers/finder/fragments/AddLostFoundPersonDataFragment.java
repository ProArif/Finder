package com.nodeers.finder.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.nodeers.finder.R;


public class AddLostFoundPersonDataFragment extends Fragment {

    private Spinner choice_l_w,choice_color,choice_police;
    private LinearLayout invisible_layout;
    private Button btnSubmit,btnWantedSubmit;

    private EditText edtName,edtFathername,edtGFName,edtMotherName,edtDOB;
    private String name,fName,GFname,mName,dob,img_uri;

    public AddLostFoundPersonDataFragment() {
        // Required empty public constructor
    }

    public static AddLostFoundPersonDataFragment newInstance(String param1, String param2) {
        AddLostFoundPersonDataFragment fragment = new AddLostFoundPersonDataFragment();
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
        View v = inflater.inflate(R.layout.fragment_add_lost_person_data, container, false);

        edtName = v.findViewById(R.id.editTextName);
        edtFathername = v.findViewById(R.id.editTextFathersName);
        edtGFName = v.findViewById(R.id.editTextGrFathersName);
        edtMotherName = v.findViewById(R.id.editTextMothersName);
        edtDOB = v.findViewById(R.id.editTextDob);

        choice_l_w = v.findViewById(R.id.choice_spinner_lost_found);
        choice_color = v.findViewById(R.id.spnr_body_color);
        choice_police = v.findViewById(R.id.choice_police);

        btnSubmit = v.findViewById(R.id.submitPersonData);
        btnWantedSubmit = v.findViewById(R.id.submitWantedData);

        invisible_layout = v.findViewById(R.id.foundVisibleOptions);



        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.choice_array2, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set prompt message
        choice_l_w.setPrompt("Select Lost/Found");

// Apply the adapter to the spinner
        choice_l_w.setAdapter(adapter2);


        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                R.array.choice_color, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        choice_color.setPrompt("Select Body Color");
// Apply the adapter to the spinner
        choice_color.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getContext(),
                R.array.choice_police, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        choice_police.setPrompt("Select One");
// Apply the adapter to the spinner
        choice_police.setAdapter(adapter4);

        //choice_l_f spinner visible edittext for founded
        choice_l_w.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(getContext(),"Lost selected",Toast.LENGTH_LONG).show();
                        invisible_layout.setVisibility(View.GONE);
                        break;
                    case 1:
                        Toast.makeText(getContext(),"Wanted selected",Toast.LENGTH_LONG).show();
                        invisible_layout.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        btnWantedSubmit.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return v;
    }
}