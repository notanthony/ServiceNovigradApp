package com.example.servicenovigrad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnthonySSViewer extends AppCompatActivity {
    DatabaseReference databaseServiceRequests;
    ArrayList<ServiceRequest> serviceRequests;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //need to make a new layout thats just the same as the one below
        setContentView(R.layout.activity_admin_disable_user);
        final String branchID = getIntent().getStringExtra("branch");
        databaseServiceRequests = FirebaseDatabase.getInstance().getReference(branchID+"/ServiceRequests");
        listView = (ListView) findViewById(R.id.listView);
        ((TextView) findViewById(R.id.dataType)).setText("Service Requests");
        ((TextView) findViewById(R.id.instructions)).setText("Tap on the service requests you want to make");
        serviceRequests = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent (AnthonySSViewer.this, AnthonySTViewer.class);
                intent.putExtra( "request", serviceRequests.get(i).getId());
                intent.putExtra( "branch", branchID);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onStart() {

        super.onStart();
        databaseServiceRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                serviceRequests.clear();
                for (DataSnapshot postSnapshot : dataSnapShot.getChildren()) {
                    ServiceRequest service = postSnapshot.getValue(ServiceRequest.class);
                    if (!service.isChecked()) {
                        serviceRequests.add(service);
                    }
                }
                ArrayAdapter<ServiceRequest> serviceAdapter =
                        new ArrayAdapter<>(AnthonySSViewer.this, android.R.layout.simple_list_item_1 , serviceRequests);
                listView.setAdapter(serviceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataBaseError) {

            }
        });
    }
}