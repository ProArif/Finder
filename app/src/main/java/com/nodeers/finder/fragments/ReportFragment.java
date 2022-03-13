package com.nodeers.finder.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nodeers.finder.AddVehicleDataActivity;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.ReportModel;


public class ReportFragment extends Fragment {


    private Button btnReport;
    private EditText edtReportTxt;
    private String reportTxt;
    private String userIdd;

    private Dialog dialog;

    private FirebaseUser mUser;
    private FirebaseDatabase mData;
    private DatabaseReference dbRef;
    private ReportModel model = new ReportModel();

    public ReportFragment() {
        // Required empty public constructor
    }


    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
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
        View reportPage = inflater.inflate(R.layout.fragment_report, container, false);

        mData = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = mData.getReference("reports");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            userIdd = mUser.getUid();
            model.setUserId(userIdd);
        }

        edtReportTxt = reportPage.findViewById(R.id.edtReportTxt);
        btnReport = reportPage.findViewById(R.id.btnReport);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportTxt = edtReportTxt.getText().toString().trim();
                if (reportTxt.isEmpty()){
                    edtReportTxt.setError("Please enter your message here!!!");
                }else{
                    model.setReportText(reportTxt);
                    if (mUser != null) {
                        model = new ReportModel(model.getUserId(), model.getReportText());
                        showProgressBAr();
                        dbRef.push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    hideProgressBar();
                                    showMessage();
                                }else{
                                    Toast.makeText(getContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                    Log.e("firebase", String.valueOf(task.getResult()));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressBar();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Please login to post a report", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return reportPage;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showMessage(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setMessage("Report Submitted successfully");
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loadFragment(new LostFragment());
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
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
}