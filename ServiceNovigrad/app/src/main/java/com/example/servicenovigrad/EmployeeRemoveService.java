package com.example.servicenovigrad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeeRemoveService extends AppCompatActivity {
    DatabaseReference databaseServices;
    List<Service> services;
    List<DatabaseReference> ref;
    ListView listView;
    private EmployeeData employee;
    private DatabaseReference employeeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_remove_service);
        databaseServices = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/ServicesOffered");
        listView = (ListView) findViewById(R.id.listView);
        employeeRef = FirebaseDatabase.getInstance().getReference("UserData").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        employeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employee = dataSnapshot.getValue(EmployeeData.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ((TextView) findViewById(R.id.dataType)).setText("Services");
        ((TextView) findViewById(R.id.instructions)).setText("Tap and hold on the services you want to remove");
        services = new ArrayList<>();
        ref = new ArrayList<>();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeRemoveService.this);
                final DatabaseReference reference = ref.get(i);
                builder.setCancelable(true);
                builder.setTitle("Remove this service");
                builder.setMessage(services.get(i).toString());
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.removeValue();
                                employee.getServiceNames().remove(services.get(i).getServiceName());
                                employeeRef.child(employee.getId()).setValue(employee);
                                Toast.makeText(getApplicationContext(), "Service Deleted", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EmployeeRemoveService.this,"Cancelled",Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }


    @Override
    protected void onStart() {

        super.onStart();
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                services.clear();
                ref.clear();
                for (DataSnapshot postSnapshot : dataSnapShot.getChildren()) {
                    ref.add(postSnapshot.getRef());
                    Service service = postSnapshot.getValue(Service.class);
                    services.add(service);
                }
                ArrayAdapter<Service> serviceAdapter =
                        new ArrayAdapter<>(EmployeeRemoveService.this, android.R.layout.simple_list_item_1 , services);
                listView.setAdapter(serviceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataBaseError) {

            }
        });
    }

}
