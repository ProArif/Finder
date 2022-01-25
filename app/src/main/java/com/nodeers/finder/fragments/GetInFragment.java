package com.nodeers.finder.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.nodeers.finder.R;

import java.util.concurrent.TimeUnit;


public class GetInFragment extends Fragment {

    private EditText edtPhone;
    private Button btnSendCode,btnVerify;
    private String phone;
    private ProgressBar progressBar;
    private LinearLayout layoutVerify;


    private PinView pinView;
    private String verificationCode, mVerificationId;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private PhoneAuthCredential credential;

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
        // Inflate the layout for this fragment
        View vw = inflater.inflate(R.layout.fragment_get_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        edtPhone = vw.findViewById(R.id.edt_email_phn);

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

                            Fragment fragment = new ProfileFragment();
                            loadFragment(fragment);

                            FirebaseUser user = task.getResult().getUser();

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
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }





}