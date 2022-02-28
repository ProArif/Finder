package com.nodeers.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nodeers.finder.adapters.LostPersonGridVAdapter;
import com.nodeers.finder.datamodels.LostPersonDataModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SearchResultsActivity extends AppCompatActivity {

    private int click = 0;

    private EditText edtSearchTxt;
    private Button btnSearch;
    private Spinner category_choice, date_choice,p_v_choice;

    private String searchTxt,selectedDate, startDate,endDate;
    private String category = "person";
    private GridView search_gv;

    private DatabaseReference dbRef;
    private Query query;
    private final FirebaseDatabase mDb = FirebaseDatabase.getInstance("https://finder-67a87-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private LostPersonGridVAdapter adapter;
    private ArrayList<LostPersonDataModel> dataModel_search ;
    private Calendar cal = GregorianCalendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        dbRef = mDb.getReference("lost persons");

        search_gv = findViewById(R.id.search_grid);
        dataModel_search = new ArrayList<>();
        adapter = new LostPersonGridVAdapter(this,dataModel_search);
        search_gv.setAdapter(adapter);

        //handleIntent(getIntent());

        edtSearchTxt = findViewById(R.id.searchEdT);
        btnSearch = findViewById(R.id.btnSearch);
        category_choice = findViewById(R.id.search_category);
        date_choice = findViewById(R.id.search_date_category);
        //p_v_choice = findViewById(R.id.search_person_vehicle);

        //btnSearch.setVisibility(View.INVISIBLE);

        Date netDate = new Date(); // current time from here
        SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        startDate =  sfd.format(netDate);

        setAdaptersSearch();

//        p_v_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position){
//                    case 0:
//                        Toast.makeText(SearchResultsActivity.this, "Person", Toast.LENGTH_SHORT).show();
//                        category = "person";
//                        break;
//                    case 1:
//                        Toast.makeText(SearchResultsActivity.this, "Vehicle", Toast.LENGTH_SHORT).show();
//                        category = "vehicle";
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                category = "person";
//                query = dbRef.orderByChild(date);
//            }
//        });

        category_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        click = 0;
                        Toast.makeText(SearchResultsActivity.this,"Please select any choices",Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        click = 1;
                        dbRef = mDb.getReference("lost persons");

                        break;
                    case 2:
                        click = 2;
                        dbRef = mDb.getReference("wanted_persons");


                        break;
                    case 3:
                        click = 3;
                        dbRef = mDb.getReference("found_entities");

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SearchResultsActivity.this,"Please select any choices",Toast.LENGTH_LONG).show();
            }
        });

        date_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:


                        break;
                    case 1:
                        cal.setTime(new Date());
                        cal.add(Calendar.DAY_OF_YEAR, -6);
                        Date weekBeforeDate = cal.getTime();
                        SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        endDate =  sfd.format(weekBeforeDate);

                        query = dbRef.orderByChild("date").startAt(startDate).endAt(endDate);
                        Log.e("query",startDate);
                        Log.e("query date",endDate);

                        Toast.makeText(SearchResultsActivity.this, "By Week", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(SearchResultsActivity.this, "By Month", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(SearchResultsActivity.this, "Input", Toast.LENGTH_SHORT).show();
                        openDatePickerDialog();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                query = dbRef.orderByChild(startDate);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTxt = edtSearchTxt.getText().toString().trim();
                if (searchTxt.isEmpty()){
                    edtSearchTxt.setError("Please enter search text");
                }else{

                    query = dbRef.orderByChild("name").startAt(searchTxt);
                    load_data(query);
                    searchTxt = "";
                }


            }
        });

    }

    private void load_data(Query query){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataModel_search.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    LostPersonDataModel data = snapshot1.getValue(LostPersonDataModel.class);
                    if (data != null) {
                        data.setName(snapshot1.child("name").getValue().toString());
                        data.setImgUrl(snapshot1.child("imgUrl").getValue().toString());
                        Log.e("entered search snapshot",data.getName());

                        dataModel_search.add(data);
                    }

//                    if (dataModel_search.isEmpty()) {
//                        // create an alert builder
//                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultsActivity.this);
//                        builder.setMessage("No Data Found Matching Your Search!");
//                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                        // create and show the alert dialog
//                        AlertDialog dialog = builder.create();
//                        dialog.setCancelable(true);
//                        dialog.show();
//                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchResultsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("search failed",error.getMessage());
            }
        });
    }

    public void openDatePickerDialog() {

        Calendar cal = Calendar.getInstance();
        // Get Current Date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String sDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                            edtSearchTxt.setText(sDate);
                            selectedDate = sDate;
                            Log.e("DOB selected",selectedDate);


                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setAdaptersSearch(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choice_category_search, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        category_choice.setPrompt("Select Lost/Found");

        // Apply the adapter to the spinner
        category_choice.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.search_date_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        date_choice.setPrompt("Select Date");

        // Apply the adapter to the spinner
        date_choice.setAdapter(adapter2);

//        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
//                R.array.choice_array, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //set prompt message
//        p_v_choice.setPrompt("Select One");
//
//        // Apply the adapter to the spinner
//        p_v_choice.setAdapter(adapter3);

    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        //handleIntent(intent);
    }

//    private void handleIntent(Intent intent) {
//
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            //use the query to search your data somehow
//        }
//    }

}