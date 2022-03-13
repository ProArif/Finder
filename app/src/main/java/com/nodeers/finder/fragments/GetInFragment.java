package com.nodeers.finder.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nodeers.finder.ProfileActivity;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.UsersData;

import java.util.concurrent.TimeUnit;


public class GetInFragment extends Fragment {

    private EditText edtPhone;
    private Button btnSendCode,btnVerify;
    private String phone;
    private ProgressBar progressBar;
    private LinearLayout layoutVerify;
    private TextView tv_pbar;
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

    public GetInFragment() {
        // Required empty public constructor
    }


    public static GetInFragment newInstance(String param1, String param2) {
        GetInFragment fragment = new GetInFragment();
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

//        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = mDatabase.getReference("users");

        //storageReference = FirebaseStorage.getInstance().getReference("lost_vehicles");

        // Inflate the layout for this fragment
        View vw = inflater.inflate(R.layout.fragment_get_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        edtPhone = vw.findViewById(R.id.edt_email_phn);
        tv_pbar = vw.findViewById(R.id.txt_pbar);

        progressBar = vw.findViewById(R.id.loading);
        layoutVerify = vw.findViewById(R.id.layoutVerify);
        pinView = vw.findViewById(R.id.VerifyPin);

        btnSendCode = vw.findViewById(R.id.send_code);
        btnVerify = vw.findViewById(R.id.btn_verify);

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = edtPhone.getText().toString().trim();

                if (phone.isEmpty()){
                    edtPhone.setError("Please enter your phone no.");
                }
                else if (phone.length() != 11){
                    edtPhone.setError("Please enter your phone no. correctly");
                }
                else {
                    sendOTP();
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        return vw ;
    }



    private void save_user(String userId){
        showProgressBAr();
        data.setImgUrl("none");
        data = new UsersData(data.getName(), data.getImgUrl(),data.getPhn(), data.getmUserId(), data.getUserType());

        dbRef.child(userId).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressBar();
                startActivity(new Intent(getContext(),ProfileActivity.class));

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

    private void sendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        tv_pbar.setVisibility(View.VISIBLE);
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
                tv_pbar.setVisibility(View.GONE);
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
                tv_pbar.setVisibility(View.GONE);
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

        //set users phone number
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
                            //Log.d("signin", "signInWithCredential:success");
                            //Toast.makeText(getContext(),"Sign in completed",Toast.LENGTH_LONG).show();

                            data.setName("Your Name");
                            data.setUserType("general");
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







}