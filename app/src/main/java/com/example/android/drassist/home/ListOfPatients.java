package com.example.android.drassist.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.adapters.PatientListAdapter;
import com.example.android.drassist.adapters.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfPatients extends AppCompatActivity {

    DatabaseReference patientListRef;
    String phoneNo;
    RecyclerView recyclerView;
    ArrayList<UsersHelperClass> patientList;
    UserListAdapter adapter;
    UsersHelperClass post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_patients);
        phoneNo = getIntent().getStringExtra("phoneNo");

        patientListRef = FirebaseDatabase.getInstance().getReference("users/");

        recyclerView = findViewById(R.id.recyclerView);

        patientList = new ArrayList<>();
        getList();
        creatRecycler();
    }

    private void getList() {
        patientListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                patientList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    UsersHelperClass university = postSnapshot.getValue(UsersHelperClass.class);
                    patientList.add(university);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });
    }


    private void creatRecycler() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        patientList = new ArrayList<>();
        addItems();

        recyclerView.scrollTo(0, 0);
    }

    private void addItems() {
        adapter = new UserListAdapter(this, patientList);
        recyclerView.setAdapter(adapter);

    }
}