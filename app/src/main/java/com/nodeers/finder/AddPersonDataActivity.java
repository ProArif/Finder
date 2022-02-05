package com.nodeers.finder;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nodeers.finder.datamodels.LostPersonDataModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPersonDataActivity extends AppCompatActivity {

    private Spinner choice_l_w,choice_color,choice_police;
    private LinearLayout invisible_layout;
    private Button btnSubmit,btnWantedSubmit;

    private EditText edtName,edtFathername,edtGFName,edtMotherName,edtDOB;
    private String name,fName,GFname,mName,dob,img_uri;
    private CircleImageView upload_img;
    private Uri img;

    private ArrayList<LostPersonDataModel> dataModel_lost_person ;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;

    private ActivityResultLauncher<Intent> chooseImageActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_data);

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        chooseImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes

                            Intent data = result.getData();
                            Bundle extras =data.getExtras();
                            if (data.getExtras() == null){
                                img = data.getData();
                                img_uri = img.toString().trim();
                                Picasso.get().load(img_uri).into(upload_img);
                                Log.e("Image URI",img_uri);

                            }else if (extras != null){
                                //img_uri = extras.getString("data");
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                upload_img.setImageBitmap(imageBitmap);

                                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
                                byte[] byteArray = bao.toByteArray();
                                img_uri = Base64.encodeToString(byteArray, Base64.URL_SAFE);

                                //Picasso.get().load(img_uri).into(upload_img);
                                Log.e("Image URI","enterd else if extras !=null");
                                Log.e("Image URI",img_uri);
                            }
                            else{
                                Log.e("error camera",data.getExtras().toString());
                            }


                        }
                    }
                });

        edtName = findViewById(R.id.editTextName);
        edtFathername = findViewById(R.id.editTextFathersName);
        edtGFName = findViewById(R.id.editTextGrFathersName);
        edtMotherName = findViewById(R.id.editTextMothersName);
        edtDOB = findViewById(R.id.editTextDob);
        upload_img = findViewById(R.id.upload_image);

        choice_l_w = findViewById(R.id.choice_spinner_lost_found);
        choice_color = findViewById(R.id.spnr_body_color);
        choice_police = findViewById(R.id.choice_police);

        btnSubmit = findViewById(R.id.submitPersonData);
        btnWantedSubmit = findViewById(R.id.submitWantedData);

        invisible_layout = findViewById(R.id.foundVisibleOptions);

        setAdapters();

        //choice_l_f spinner visible edittext for founded
        choice_l_w.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(AddPersonDataActivity.this,"Lost selected",Toast.LENGTH_LONG).show();
                        invisible_layout.setVisibility(View.GONE);
                        break;
                    case 1:
                        Toast.makeText(AddPersonDataActivity.this,"Wanted selected",Toast.LENGTH_LONG).show();
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

        edtDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    openDatePickerDialog(view);
                }
            }
        });
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog(view);
            }
        });

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(AddPersonDataActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                if(checkAndRequestPermissions(AddPersonDataActivity.this)){
                    chooseImage(AddPersonDataActivity.this);
                }
            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (img != null) {
            outState.putString("cameraImageUri", img.toString().trim());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            img = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (ContextCompat.checkSelfPermission(AddPersonDataActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(AddPersonDataActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                    //checkAndRequestPermissions(this);
                } else {
                    chooseImage(AddPersonDataActivity.this);
                }
                break;
        }
    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){
        ContentValues values = new ContentValues();


        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo

                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        chooseImageActivityResultLauncher.launch(takePicture);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                        Toast.makeText(AddPersonDataActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    chooseImageActivityResultLauncher.launch(pickPhoto);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }
    // function to check permission
    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    1);
            return false;
        }
        return true;
    }

    private void setAdapters() {
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.choice_array2, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set prompt message
        choice_l_w.setPrompt("Select Lost/Found");

// Apply the adapter to the spinner
        choice_l_w.setAdapter(adapter2);


        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.choice_color, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        choice_color.setPrompt("Select Body Color");
// Apply the adapter to the spinner
        choice_color.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.choice_police, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        choice_police.setPrompt("Select One");
// Apply the adapter to the spinner
        choice_police.setAdapter(adapter4);
    }

    public void openDatePickerDialog(final View v) {

        Calendar cal = Calendar.getInstance();
        // Get Current Date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    switch (v.getId()) {
                        case R.id.editTextDob:
                            ((EditText)v).setText(selectedDate);
                            dob = selectedDate;
                            Log.e("DOB selected",dob);
                            break;
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    private void initialize_data(){

        name = edtName.getText().toString().trim();
        fName = edtFathername.getText().toString().trim();
        GFname = edtGFName.getText().toString().trim();
        mName = edtMotherName.getText().toString().trim();
        dob = edtDOB.getText().toString().trim();

    }

    private void validate_input(){

    }
}