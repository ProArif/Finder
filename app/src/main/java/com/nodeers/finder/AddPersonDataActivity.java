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
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nodeers.finder.datamodels.LostPersonDataModel;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


public class AddPersonDataActivity extends AppCompatActivity {

    private Spinner choice_l_w;
    private Spinner choice_color;
    private Spinner choice_police;
    private Spinner choice_superior;
    private LinearLayout invisible_layout;
    private Button btnSubmit,btnWantedSubmit,btnFoundSubmit;

    private EditText edtName,edtFathername,edtGFName,edtMotherName,edtDOB,edtCaseNo;
    private String name,fName,GFname,mName,dob,img_uri,body_color,uId,case_no;
    private ImageView img_upload;
    private Uri img;

    private ArrayList<LostPersonDataModel> dataModel_lost_person ;
    private LostPersonDataModel dataModel = new LostPersonDataModel();
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;
    private FirebaseUser mUser;
    private StorageReference storageReference;

    private ProgressBar progressBar;
    private Dialog dialog;

    private ActivityResultLauncher<Intent> chooseImageActivityResultLauncher;

    private boolean clicked_img = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_data);

        resultLauncher();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = mUser.getUid();

        store_data = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = store_data.getReference("lost persons");
        storageReference = FirebaseStorage.getInstance().getReference("lost_persons");


        edtName = findViewById(R.id.editTextName);
        edtFathername = findViewById(R.id.editTextFathersName);
        edtGFName = findViewById(R.id.editTextGrFathersName);
        edtMotherName = findViewById(R.id.editTextMothersName);
        edtDOB = findViewById(R.id.editTextDob);
        edtCaseNo = findViewById(R.id.editTextCaseNo);

        //upload_img = findViewById(R.id.upload_image);
        img_upload = findViewById(R.id.image);

        progressBar = findViewById(R.id.loading_wait);

        choice_l_w = findViewById(R.id.choice_spinner_lost_found);
        choice_color = findViewById(R.id.spnr_body_color);
        choice_police = findViewById(R.id.choice_police);
        choice_superior = findViewById(R.id.choice_superior_officer);

        btnSubmit = findViewById(R.id.submitPersonData);
        btnWantedSubmit = findViewById(R.id.submitWantedData);
        btnFoundSubmit = findViewById(R.id.submitFoundData);

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
                        btnSubmit.setVisibility(View.VISIBLE);
                        btnWantedSubmit.setVisibility(View.GONE);
                        break;
                    case 1:
                        Toast.makeText(AddPersonDataActivity.this,"Wanted selected",Toast.LENGTH_LONG).show();
                        invisible_layout.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        btnWantedSubmit.setVisibility(View.VISIBLE);

                        break;

                    case 2:
                        invisible_layout.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.GONE);
                        btnWantedSubmit.setVisibility(View.GONE);
                        btnFoundSubmit.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                btnSubmit.setVisibility(View.VISIBLE);
            }
        });

        choice_superior.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    btnWantedSubmit.setVisibility(View.VISIBLE);
                }else{
                    btnWantedSubmit.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPersonDataActivity.this);
                    builder.setTitle("Unauthorized Operation");
                    builder.setMessage("You can not upload Unauthorized Data. Select Yes if your superior knows and then upload the data");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(true);
                    dialog.show();
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

        choice_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //Toast.makeText(AddPersonDataActivity.this, "Lost selected", Toast.LENGTH_LONG).show();
                        body_color = "White";
                        dataModel.setBody_color(body_color);
                        break;
                    case 1:
                        //Toast.makeText(AddPersonDataActivity.this, "Wanted selected", Toast.LENGTH_LONG).show();
                        body_color = "Black";
                        dataModel.setBody_color(body_color);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        img_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(AddPersonDataActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                if(checkAndRequestPermissions(AddPersonDataActivity.this)){
                    chooseImage(AddPersonDataActivity.this);

                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_data();
                if (name.isEmpty()){
                    edtName.setError("Please enter your name");
                }
                else if (fName.isEmpty()){
                    edtFathername.setError("Please enter your fathers name");
                }
                else if (dob.isEmpty()){
                    edtDOB.setError("Please select your Date of Birth");
                }
                else{
                    uploadToStorage();
                    //Toast.makeText(AddPersonDataActivity.this,"entered else block",Toast.LENGTH_LONG).show();
                    Log.e("firebase", "entered else block");

                }
            }
        });

        btnWantedSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initialize_data();
                case_no = edtCaseNo.getText().toString().trim();
                dataModel.setCase_num(case_no);

                if (name.isEmpty()){
                    edtName.setError("Please enter your name");

                }
                else if (fName.isEmpty()){
                    edtFathername.setError("Please enter your fathers name");

                }
                else if (dob.isEmpty()){
                    edtDOB.setError("Please select your Date of Birth");

                }
                else if (case_no.isEmpty()){
                    edtCaseNo.setError("Please enter Case/GD number");
                }
                else{
                    storageReference = FirebaseStorage.getInstance().getReference("wanted_persons");
                    dbRef = store_data.getReference("wanted_persons");

                    uploadToStorage();
                    //Toast.makeText(AddPersonDataActivity.this,"entered else block",Toast.LENGTH_LONG).show();
                    Log.e("firebase", "entered wanted else block");
                }

            }
        });

        btnFoundSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_data();
                if (name.isEmpty()){
                    edtName.setError("Please enter your name");
                }
                else if (fName.isEmpty()){
                    edtFathername.setError("Please enter your fathers name");
                }
                else if (dob.isEmpty()){
                    edtDOB.setError("Please select your Date of Birth");
                }
                else{
                    storageReference = FirebaseStorage.getInstance().getReference("found_entities");
                    dbRef = store_data.getReference("found_entities");

                    uploadToStorage();
                    //Toast.makeText(AddPersonDataActivity.this,"entered else block",Toast.LENGTH_LONG).show();
                    Log.e("firebase", "entered else block");

                }
            }
        });

    }



    private void save_data(){

        showProgressBAr();
        LostPersonDataModel dataModel1 = new LostPersonDataModel(dataModel.getName(),dataModel.getFather_name()
                ,dataModel.getGrandf_name(), dataModel.getMother_name(),dataModel.getBody_color(),dataModel.getDob()
                ,dataModel.getImgUrl(),dataModel.getCase_num());

        if (mUser != null){
            Log.e("firebase", "entered user not null");
            Log.e("firebase", dataModel1.getName() + dataModel1.getBody_color());

            dbRef.push().setValue(dataModel1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.e("firebase", "entered task complete");
                        hideProgressBar();
                        Toast.makeText(AddPersonDataActivity.this,"Uploaded successfully",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(AddPersonDataActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                        Log.e("firebase", String.valueOf(task.getResult()));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
                    Log.e("firebase", String.valueOf(e.getMessage()));
                }
            });
        }
        else{
            hideProgressBar();
            Toast.makeText(AddPersonDataActivity.this,"Please login",Toast.LENGTH_LONG).show();

        }
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

    private void uploadToStorage(){

        if (!clicked_img){
            Toast.makeText(this,"Please select an image",Toast.LENGTH_LONG).show();
        }
        else {
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Bitmap bitmap = ((BitmapDrawable) img_upload.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = storageReference.child(uId +"."+UUID.randomUUID());

            ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dataModel.setImgUrl(String.valueOf(uri));
                            progressDialog.dismiss();
                            save_data();
                            empty_fields();
                            Blink_and_showDialog();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.e("failed upld",e.getMessage());
                }
            });
        }


//        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()){
//                    throw task.getException();
//                }
//
//                return storageReference.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    dataModel.setImgUrl(String.valueOf(downloadUri));
//                    progressDialog.dismiss();
//                    save_data();
//                    empty_fields();
//                    Blink_and_showDialog();
//
//                } else {
//                    progressDialog.dismiss();
//                    Log.e("failed upld",task.getException().getMessage());
//                }
//            }
//        });

//            storageReference.child(uId).child(UUID.randomUUID().toString()).putBytes(data)
//                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if (task.isSuccessful()){
//                                //String uri = String.valueOf(task.getResult());
//                                String uri = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
//
//                                dataModel.setImgUrl(uri);
//                                progressDialog.dismiss();
//
//                                save_data();
//                                empty_fields();
//                                Blink_and_showDialog();
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e("Faild upld",e.getMessage());
//                    Toast.makeText(AddPersonDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    empty_fields();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                     //Progress Listener for loading
//                        // percentage on the dialog box
//                            double progress
//                                    = (100.0
//                                    * snapshot.getBytesTransferred()
//                                    / snapshot.getTotalByteCount());
//                            progressDialog.setMessage(
//                                    "Uploaded "
//                                            + (int)progress + "%");
//                        }
//
//            });


    }


    private void empty_fields(){
        edtName.setText("");
        edtDOB.setText("");
        edtFathername.setText("");
        edtGFName.setText("");
        edtMotherName.setText("");

    }

    private void initialize_data(){

        name = edtName.getText().toString().trim();
        fName = edtFathername.getText().toString().trim();
        GFname = edtGFName.getText().toString().trim();
        mName = edtMotherName.getText().toString().trim();
        dob = edtDOB.getText().toString().trim();

        dataModel.setName(name);
        dataModel.setFather_name(fName);
        dataModel.setDob(dob);
        dataModel.setGrandf_name(GFname);
        dataModel.setMother_name(mName);

    }

    private void resultLauncher(){
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        chooseImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            clicked_img = true;
                            Intent data = result.getData();
                            Bundle extras =data.getExtras();
                            if (data.getExtras() == null){
                                img = data.getData();
                                img_uri = img.toString().trim();
                                //Picasso.get().load(img_uri).into(upload_img);
                                Picasso.get().load(img_uri).into(img_upload);
                                //dataModel.setImgUrl(img_uri);
                                Log.e("Image URI",img_uri);

                            }else if (extras != null){
                                clicked_img = true;
                                //img_uri = extras.getString("data");
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                //upload_img.setImageBitmap(imageBitmap);
                                img_upload.setImageBitmap(imageBitmap);

                            }
                            else{
                                Log.e("error camera",data.getExtras().toString());
                            }


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

        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,
                R.array.choice_superior, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        choice_superior.setPrompt("Select One");
// Apply the adapter to the spinner
        choice_superior.setAdapter(adapter5);


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

    private void Blink_and_showDialog(){

            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // set the custom layout
            final View customLayout = getLayoutInflater().inflate(R.layout.uploaded_successfully_msg, null);
            builder.setView(customLayout);

        TextView tv_blink = customLayout.findViewById(R.id.blinktext);
        Button btn_continue = customLayout.findViewById(R.id.continue_app);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //loadFragment(new AddLostFoundPersonDataFragment());
                    startActivity(new Intent(AddPersonDataActivity.this,MainActivity.class));
                }
            });

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


}