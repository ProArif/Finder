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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nodeers.finder.datamodels.UsersData;
import com.nodeers.finder.fragments.LostFragment;
import com.nodeers.finder.fragments.ReportFragment;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profile_img;
    private ImageView backFromPfl;
    private Button btnUpload,btnSearch,btnReport;
    private String userIdd;
    private TextView blinkingTxt,tv_profile_name,tv_profile_phn,tvEditName,tvChangeImg;
    private FirebaseUser mAppUser;
    private FirebaseDatabase store_data;
    private DatabaseReference dbRef;
    private StorageReference storageReference;
    private UsersData data;
    private Dialog dialog;

    private ActivityResultLauncher<Intent> chooseVehicleImageActivityResultLauncher;
    private boolean clicked_img = false;
    private Uri img;
    private String imgUrl;
    private UsersData dataModel = new UsersData();

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        btnUpload = findViewById(R.id.upload_data);
        btnSearch = findViewById(R.id.search_data);
        btnReport = findViewById(R.id.browse);
        tv_profile_name = findViewById(R.id.user_profile_name);
        tv_profile_phn = findViewById(R.id.user_profile_phn);
        profile_img = findViewById(R.id.profile_img);
        backFromPfl = findViewById(R.id.backFromPfl);
        tvEditName = findViewById(R.id.tvEditName);
        tvChangeImg = findViewById(R.id.chngImg);

        //setting blinking text to animate
        blinkingTxt = findViewById(R.id.blinkingText);
        blinkingTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        blinkingTxt.setSelected(true);
        blinkingTxt.setSingleLine(true);

        resultLauncher();

        store_data = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = store_data.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("users");

        mAppUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mAppUser != null) {
            userIdd = mAppUser.getUid();
            loadData();
        }

        data = new UsersData();


        tvEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForUpdateDetails();
            }
        });

        backFromPfl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAppUser != null){
                    showDialog();
                }else {
                    Toast.makeText(ProfileActivity.this,"Please login to add a post",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SearchResultsActivity.class));
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions(ProfileActivity.this)){
                    chooseImage(ProfileActivity.this);

                }

            }
        });

        tvChangeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions(ProfileActivity.this)){
                    chooseImage(ProfileActivity.this);

                }
            }
        });
    }

    public boolean checkAndRequestPermissions(final Activity context) {
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

    private void chooseImage(Context context){

        final CharSequence[] optionsMenu = {"Take Photo", "Exit" }; // create a menuOption Array
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
                        Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                }
//                else if(optionsMenu[i].equals("Choose from Gallery")){
//                    // choose from  external storage
//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    chooseVehicleImageActivityResultLauncher.launch(pickPhoto);
//                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
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
                                Picasso.get().load(imgUrl).into(profile_img);
                                uploadToStorage();
                                //Log.e("Image URI",imgUrl);

                            }
                            else if (extras != null){
                                clicked_img = true;
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                profile_img.setImageBitmap(imageBitmap);

                                uploadToStorage();

                            }
                            else{
                                Log.e("error camera",data.getExtras().toString());
                            }


                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                    openSettings();

                } else if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                    openSettings();

                } else {
                    chooseImage(ProfileActivity.this);
                }
                break;
        }
    }

    public void openSettings(){
        Intent intent = new Intent();

        Uri uri = Uri.fromParts("package",this.getPackageName(),null);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri);
        startActivity(intent);
    }

    private void show_msg(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Show permission explanation dialog...
            AlertDialog alert = new AlertDialog.Builder(this).setMessage("Without the permissions you cannot access the function")
                    .setCancelable(false)
                    .setPositiveButton("Okay, I understand", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkAndRequestPermissions(ProfileActivity.this);
                        }
                    }).setNegativeButton("No, Exit the App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            alert.setTitle("Requires Permission");
            alert.show();
        }else{
            //Never ask again selected, or device policy prohibits the app from having that permission.
            //So, disable that feature, or fall back to another situation...
        }
    }

    private void uploadToStorage() {

        if (!clicked_img){
            Toast.makeText(ProfileActivity.this,"Please select an image",Toast.LENGTH_LONG).show();
        }
        else {
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Bitmap bitmap = ((BitmapDrawable) profile_img.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = storageReference.child(userIdd + "." + UUID.randomUUID());

            ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dataModel.setImgUrl(String.valueOf(uri));
                            progressDialog.dismiss();
                            saveImg();
                            Blink_and_showDialog();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.e("failed upld", e.getMessage());
                }
            });
        }


    }

    private void saveImg(){
        showProgressBAr();
        dbRef.child(userIdd).child("imgUrl").setValue(dataModel.getImgUrl()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    hideProgressBar();
                    Toast.makeText(ProfileActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showDialogForUpdateDetails (){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Update");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.update_profile_details, null);
        builder.setView(customLayout);

        Button btn_update = customLayout.findViewById(R.id.updateName);
        EditText edtName = customLayout.findViewById(R.id.edtNamePfl);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nam = edtName.getText().toString().trim();
                if (nam.isEmpty()){
                    edtName.setError("Please Enter your name!");
                }else {
                    dialog.dismiss();
                    showProgressBAr();
                    dbRef.child(userIdd).child("name").setValue(nam).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                hideProgressBar();
                                Toast.makeText(ProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

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

    private void loadExistingData(){
        dbRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //needs to check Uid and then display accurate data according to logged in user.

                data.setName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                data.setPhn(Objects.requireNonNull(snapshot.child("phn").getValue()).toString());
                data.setImgUrl(snapshot.child("imgUrl").getValue().toString());

                String name = data.getName();
                String phn = data.getPhn();
                tv_profile_name.setText(name);
                tv_profile_phn.setText(phn);
                String url = data.getImgUrl();
                Picasso.get().load(url).into(profile_img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Profile Retrieval Error",error.getMessage());
            }
        });
    }

    private void loadData() {

        showProgressBAr();
        dbRef.child(userIdd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                hideProgressBar();
                //needs to check Uid and then display accurate data according to logged in user.

                data.setName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                data.setPhn(Objects.requireNonNull(snapshot.child("phn").getValue()).toString());
                data.setImgUrl(snapshot.child("imgUrl").getValue().toString());

                String name = data.getName();
                String phn = data.getPhn();
                tv_profile_name.setText(name);
                tv_profile_phn.setText(phn);
                String url = data.getImgUrl();
                Picasso.get().load(url).into(profile_img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressBar();
                Log.e("Profile Retrieval Error",error.getMessage());
            }
        });


    }


    private void showDialog (){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose One");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.person_vehicle_chooser_layout, null);
        builder.setView(customLayout);

        ImageButton btn_person = customLayout.findViewById(R.id.imageButtonPerson);
        ImageButton btn_vehicle = customLayout.findViewById(R.id.imageButtonCar);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        btn_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(ProfileActivity.this,AddPersonDataActivity.class));
            }
        });

        btn_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(ProfileActivity.this,AddVehicleDataActivity.class));
            }
        });


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
                //startActivity(new Intent(ProfileActivity.this,MainActivity.class));
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