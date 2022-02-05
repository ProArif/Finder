package com.nodeers.finder.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.LostPersonDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddLostFoundPersonDataFragment extends Fragment {

    private Spinner choice_l_w,choice_color,choice_police;
    private LinearLayout invisible_layout;
    private Button btnSubmit,btnWantedSubmit;

    private EditText edtName,edtFathername,edtGFName,edtMotherName,edtDOB;
    private String name,fName,GFname,mName,dob,img_uri;
    private CircleImageView upload_img;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    String[] appPerms;


    private ArrayList<LostPersonDataModel> dataModel_lost_person ;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;

    public AddLostFoundPersonDataFragment() {
        // Required empty public constructor
    }

    public static AddLostFoundPersonDataFragment newInstance(String param1, String param2) {
        AddLostFoundPersonDataFragment fragment = new AddLostFoundPersonDataFragment();
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
        upload_img = v.findViewById(R.id.upload_image);

        choice_l_w = v.findViewById(R.id.choice_spinner_lost_found);
        choice_color = v.findViewById(R.id.spnr_body_color);
        choice_police = v.findViewById(R.id.choice_police);

        btnSubmit = v.findViewById(R.id.submitPersonData);
        btnWantedSubmit = v.findViewById(R.id.submitWantedData);

        invisible_layout = v.findViewById(R.id.foundVisibleOptions);

        setAdapters();

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

        appPerms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestPermissionLauncher.launch(appPerms);
                capturePhoto();

            }
        });
        return v;
    }



    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.e("activityResultLauncher", ""+result.toString());
                    Boolean areAllGranted = true;
                    for(Boolean b : result.values()) {
                        areAllGranted = areAllGranted && b;
                    }

                    if(areAllGranted) {
                        capturePhoto();
                    }
                }
            });

    private void capturePhoto() {

        // function to let's the user to choose image from camera or gallery

        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture != null){
                        Uri imgUri = takePicture.getData();
                        Picasso.get().load(imgUri).into(upload_img);
                        img_uri = imgUri.toString().trim();
                    }else{

                    }
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Uri img = pickPhoto.getData();
                    Picasso.get().load(img).into(upload_img);
                    img_uri = img.toString().trim();
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }





    private void setAdapters() {
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
    }





}