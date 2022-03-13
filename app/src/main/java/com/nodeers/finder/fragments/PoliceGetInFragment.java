package com.nodeers.finder.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.nodeers.finder.ProfileActivity;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.UsersData;

import java.util.concurrent.TimeUnit;


public class PoliceGetInFragment extends Fragment {

    private EditText edtPhone,edtThana,edtDist;
    private Button btnSendCode,btnVerify;
    private String phone,thana,dist;
    private ProgressBar progressBar;
    private LinearLayout layoutVerify;

    private PinView pinView;
    private String verificationCode, mVerificationId;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private PhoneAuthCredential credential;

    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
    private FirebaseUser mUser;
    private StorageReference storageReference;
    private String uId;
    private Dialog dialog;
    private UsersData data = new UsersData();


    public PoliceGetInFragment() {
        // Required empty public constructor
    }


    public static PoliceGetInFragment newInstance(String param1, String param2) {
        PoliceGetInFragment fragment = new PoliceGetInFragment();
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

        mDatabase = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = mDatabase.getReference("users");


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_police_get_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        edtPhone = v.findViewById(R.id.edt_police_phn);
        edtThana = v.findViewById(R.id.edt_police_thana);
        edtDist = v.findViewById(R.id.edt_police_dist);

        progressBar = v.findViewById(R.id.loading);
        layoutVerify = v.findViewById(R.id.layoutVerifyPolice);
        pinView = v.findViewById(R.id.VerifyPin);



        btnSendCode = v.findViewById(R.id.send_code_police);
        btnVerify = v.findViewById(R.id.btn_verify_police);

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeData();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });


        return v;
    }
    private void showProgressBAr(){
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideProgressBar(){
        dialog.dismiss();
    }

    private void save_user(String userId){
        showProgressBAr();
        data.setImgUrl("none");
        data = new UsersData(data.getName(), data.getImgUrl(),data.getPhn(),
                data.getmUserId(), data.getUserType(),data.getThana(),data.getDist());

        dbRef.child(userId).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressBar();
                startActivity(new Intent(getContext(), ProfileActivity.class));

                Toast.makeText(getContext(),"Profile Created",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initializeData() {
        thana = edtThana.getText().toString().trim();
        dist = edtDist.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();

        if (phone.isEmpty()){
            edtPhone.setError("Please enter your phone no.");
        }
        else if (phone.length() != 11){
            edtPhone.setError("Please enter your phone no. correctly");
        }
        else if (!phone.contains("01320")){
            edtPhone.setError("Police phone no. must contain 01320");
            showGeneralLogin();
        }

        else if (thana.isEmpty()){
            edtThana.setError("Please enter your Thana");
        }
        else if (dist.isEmpty()){
            edtDist.setError("Please enter your District");
        }
        else{
            sendOTP();
        }


    }

    public void showGeneralLogin (){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Not Police?");
        builder.setMessage("Got here by mistake? Go to General Login Instead?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();
            }
        });

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.category_chooser, null);
        builder.setView(customLayout);

        ImageButton btn_police = customLayout.findViewById(R.id.imageButtonPolice);
        ImageButton btn_general = customLayout.findViewById(R.id.imageButtonGeneral);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        btn_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadFragment(new PoliceGetInFragment());
            }
        });

        btn_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadFragment(new GetInFragment());
            }
        });


    }


    private void sendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        btnSendCode.setVisibility(View.INVISIBLE);

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("verification", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("verify", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
                progressBar.setVisibility(View.GONE);
                btnSendCode.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("code sent", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                String mResendToken = token.toString();

                progressBar.setVisibility(View.GONE);
                btnSendCode.setVisibility(View.VISIBLE);
                layoutVerify.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),"Successfully code send!",Toast.LENGTH_LONG).show();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+88" + phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        data.setPhn(phone);


    }

    public void verify(){
        verificationCode = pinView.getText().toString().trim();
        Log.e("code",verificationCode);
        if (verificationCode.isEmpty()){
            Toast.makeText(getContext(),"Please enter your code",Toast.LENGTH_LONG).show();
        }else{
            credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            signInWithPhoneAuthCredential(credential);

        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "signInWithCredential:success");
                            Toast.makeText(getContext(),"Sign in completed",Toast.LENGTH_LONG).show();

                            data.setName("Your Name");
                            data.setUserType("police");
                            data.setThana(thana);
                            data.setDist(dist);

                            mUser = task.getResult().getUser();
                            if (mUser != null){
                                uId = mUser.getUid();
                                data.setmUserId(uId);
                                save_user(uId);
                            }

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("failed", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}