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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import com.nodeers.finder.datamodels.VehicleDataModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class AddVehicleDataActivity extends AppCompatActivity {

    private Spinner vehicle_l_f;
    private Button btnSubmit,btnFoundData;
    private EditText edtRegNo,edtEngineNo;
    private ImageView vehicle_img;
    private String regNo,engineNo,imgUrl,uId;
    private Uri img;
    private Dialog dialog;

    private VehicleDataModel dataModel = new VehicleDataModel();

    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
    private FirebaseUser mUser;
    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> chooseVehicleImageActivityResultLauncher;

    private boolean clicked_img = false;

    SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private Date date;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle_data);

        resultLauncher();


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = mDatabase.getReference("lost vehicles");
        storageReference = FirebaseStorage.getInstance().getReference("lost_vehicles");

        vehicle_l_f = findViewById(R.id.spnr_lost_found_vehicle);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choice_array3, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set prompt message
        vehicle_l_f.setPrompt("Select Lost/Found");

        // Apply the adapter to the spinner
        vehicle_l_f.setAdapter(adapter);

        edtRegNo = findViewById(R.id.editTextVehicleRegNo);
        edtEngineNo = findViewById(R.id.editTextEngineNo);
        btnSubmit = findViewById(R.id.btnSubmitVehicleData);
        btnFoundData = findViewById(R.id.btnSubmitFoundVData);
        vehicle_img = findViewById(R.id.imageVehicle);

        vehicle_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions(AddVehicleDataActivity.this)){
                    chooseImage(AddVehicleDataActivity.this);

                }
            }
        });

        vehicle_l_f.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Log.e("lost","lost selected");
                        btnFoundData.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        btnFoundData.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize();

                if (regNo.isEmpty()){
                    edtRegNo.setError("Please Enter Registration No. ");
                }
                else if (engineNo.isEmpty()){
                    edtEngineNo.setError("Please Enter Engine No. ");
                }
                else{
                    uploadToStorage();
                }
            }
        });

        btnFoundData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize();

                if (regNo.isEmpty()){
                    edtRegNo.setError("Please Enter Registration No. ");
                }
                else if (engineNo.isEmpty()){
                    edtEngineNo.setError("Please Enter Engine No. ");
                }
                else{

                    storageReference = FirebaseStorage.getInstance().getReference("found_entities");
                    dbRef = mDatabase.getReference("found_entities");

                    uploadToStorage();
                }
            }
        });


    }

    private void initialize(){
        regNo = edtRegNo.getText().toString().trim();
        engineNo = edtEngineNo.getText().toString().trim();

        dataModel.setRegNo(regNo);
        dataModel.setName(engineNo);
    }

    private void uploadToStorage() {

        if (!clicked_img){
            Toast.makeText(this,"Please select an image",Toast.LENGTH_LONG).show();
        }
        else{
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Bitmap bitmap = ((BitmapDrawable) vehicle_img.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = storageReference.child(uId +"."+ UUID.randomUUID());

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

    private void save_data(){

        String formattedDate;
        date = calendar.getTime();
        formattedDate = sfd.format(date);
        showProgressBAr();
        dataModel = new VehicleDataModel(dataModel.getRegNo(),dataModel.getName(),
                dataModel.getImgUrl(),date.getTime(),formattedDate);

        if (mUser != null){
            Log.e("firebase", "entered user not null");


            dbRef.push().setValue(dataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.e("firebase", "entered task complete");
                        hideProgressBar();
                        Toast.makeText(AddVehicleDataActivity.this,"Uploaded successfully",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(AddVehicleDataActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
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
            Toast.makeText(AddVehicleDataActivity.this,"Please login",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (ContextCompat.checkSelfPermission(AddVehicleDataActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(AddVehicleDataActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                    //checkAndRequestPermissions(this);
                } else {
                    chooseImage(AddVehicleDataActivity.this);
                }
                break;
        }
    }

    private void resultLauncher(){
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        chooseVehicleImageActivityResultLauncher = registerForActivityResult(
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
                                imgUrl = img.toString().trim();
                                //Picasso.get().load(img_uri).into(upload_img);
                                Picasso.get().load(imgUrl).into(vehicle_img);
                                //dataModel.setImgUrl(img_uri);
                                Log.e("Image URI",imgUrl);

                            }else if (extras != null){
                                clicked_img = true;
                                //img_uri = extras.getString("data");
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                //upload_img.setImageBitmap(imageBitmap);
                                vehicle_img.setImageBitmap(imageBitmap);

                            }
                            else{
                                Log.e("error camera",data.getExtras().toString());
                            }


                        }
                    }
                });
    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){

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
                        chooseVehicleImageActivityResultLauncher.launch(takePicture);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                        Toast.makeText(AddVehicleDataActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    chooseVehicleImageActivityResultLauncher.launch(pickPhoto);
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

    private void empty_fields() {
        edtEngineNo.setText("");
        edtRegNo.setText("");
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
                startActivity(new Intent(AddVehicleDataActivity.this,MainActivity.class));
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