package com.example.servicenovigrad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimeZone;

public class CustomerSearchBranches extends AppCompatActivity {
    DatabaseReference branchDataRef;
    ArrayList<EmployeeData> branches;
    ListView listViewBranches;
    ArrayAdapter<EmployeeData> branchAdapter;
    int day;
    EditText citySort;
    EditText serviceSort;
    EditText postalCodeSort;
    private Spinner dropdown;


    enum DaysOfTheWeek {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    }



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search_branches);


        citySort = (EditText) findViewById(R.id.city);
        serviceSort = (EditText) findViewById(R.id.service);
        postalCodeSort = (EditText) findViewById(R.id.postalCode);

        //get the spinner from the xml.
        dropdown = findViewById(R.id.days);

        //create a list of items for the spinner.
        dropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DaysOfTheWeek.values()));
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        branchDataRef = FirebaseDatabase.getInstance().getReference("UserData");
        listViewBranches = (ListView) findViewById(R.id.listViewBranches);
        branches = new ArrayList<>();

        listViewBranches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (CustomerSearchBranches.this, CustomerSearchBranches.class);
                intent.putExtra( "branch" , branches.get(position).getId());
                startActivity(intent);
                finish();
            }
        });

        listViewBranches.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return true;
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        branchDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                branches.clear();
                for (DataSnapshot postSnapshot : dataSnapShot.getChildren()) {
                    EmployeeData branch = postSnapshot.getValue(EmployeeData.class);
                    if (branch.getRole() == UserData.UserRole.EMPLOYEE && branch.isActive()) {
                        branches.add(branch);
                    }
                }
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , branches);
                listViewBranches.setAdapter(branchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataBaseError) {

            }
        });
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.openIncreasing: {
                branches.sort(EmployeeData.openingHours(day));
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , branches);
                break;
            }
            case R.id.openDecreasing: {
                //PLEASE IMPLEMENT A EDIT TEXT FIELD ASKING FOR THE DAY OF THE WEEK THEY WANNA SORT BY
                branches.sort(EmployeeData.openingHours(day).reversed());
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , branches);
                break;
            }
            case R.id.closeDecreasing: {
                //PLEASE IMPLEMENT A EDIT TEXT FIELD ASKING FOR THE DAY OF THE WEEK THEY WANNA SORT BY
                branches.sort(EmployeeData.closingHours(day).reversed());
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , branches);
                break;
            }
            case R.id.closeIncreasing: {
                //PLEASE IMPLEMENT A EDIT TEXT FIELD ASKING FOR THE DAY OF THE WEEK THEY WANNA SORT BY
                branches.sort(EmployeeData.closingHours(day));
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , branches);
                break;
            }
            case R.id.searchCity: {
                String city = citySort.getText().toString().trim();
                if (city == null) {
                    citySort.setError("Cannot be Empty");
                    return;
                }
                ArrayList<EmployeeData> temp = new ArrayList<>();
                for(EmployeeData data : branches) {
                    if (data.getAddress().getCity().equals(city)) {
                        temp.add(data);
                    }
                }
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , temp);
                break;
            }
            case R.id.searchServiceType: {
                String serv = serviceSort.getText().toString().trim();
                if (serv == null) {
                    serviceSort.setError("Cannot be Empty");
                    return;
                }
                ArrayList<EmployeeData> temp = new ArrayList<>();
                for(EmployeeData data : branches) {
                    if (data.getServiceNames().contains(serv)) {
                        temp.add(data);
                    }
                }
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , temp);
                break;
            }
            case R.id.searchPostalCode: {
                String po = postalCodeSort.getText().toString().trim();
                if (po == null) {
                    postalCodeSort.setError("Cannot be Empty");
                    return;
                }
                ArrayList<EmployeeData> temp = new ArrayList<>();
                for(EmployeeData data : branches) {
                    if (data.getAddress().getPostalCode().substring(0,3).equals(po)) {
                        temp.add(data);
                    }
                }
                branchAdapter =
                        new ArrayAdapter<>(CustomerSearchBranches.this, android.R.layout.simple_list_item_1 , temp);
                break;
            }
        }
        //probably redunant to have the set adapter again but im not risking it
        listViewBranches.setAdapter(branchAdapter);
        listViewBranches.invalidateViews();
        listViewBranches.refreshDrawableState();
    }




}
