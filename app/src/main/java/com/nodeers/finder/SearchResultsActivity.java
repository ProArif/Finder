package com.nodeers.finder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class SearchResultsActivity extends AppCompatActivity {

    private EditText edtSearchTxt;
    private Button btnSearch;
    private Spinner category_choice, date_choice,p_v_choice;

    private String category,searchTxt,selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //handleIntent(getIntent());

        edtSearchTxt = findViewById(R.id.searchEdT);
        btnSearch = findViewById(R.id.btnSearch);
        category_choice = findViewById(R.id.search_category);
        date_choice = findViewById(R.id.search_date_category);
        p_v_choice = findViewById(R.id.search_person_vehicle);

        setAdaptersSearch();

        p_v_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(SearchResultsActivity.this, "Person", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(SearchResultsActivity.this, "Vehicle", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        category_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        category = "Lost";
                        break;
                    case 1:
                        category = "Wanted";

                        break;
                    case 2:
                        category = "Found";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                category = "lost";
            }
        });

        date_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        Toast.makeText(SearchResultsActivity.this, "By Week", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(SearchResultsActivity.this, "By Month", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(SearchResultsActivity.this, "Input", Toast.LENGTH_SHORT).show();
                        openDatePickerDialog();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                R.array.choice_array2, android.R.layout.simple_spinner_item);
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

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.choice_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set prompt message
        p_v_choice.setPrompt("Select One");

        // Apply the adapter to the spinner
        p_v_choice.setAdapter(adapter3);

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