package com.example.android.drassist.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.adapters.ChatAdapter;
import com.example.android.drassist.adapters.PatientListAdapter;
import com.example.android.drassist.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DoctorHomeActivity extends AppCompatActivity {
    String phoneNo;

    DatabaseReference patientListRef;

    RecyclerView recyclerView;
    TextView ListOfPatient;

    androidx.appcompat.widget.Toolbar toolbar;

    SharedPreferences shared;
    SharedPreferences.Editor sharedEditor;

    ArrayList<PatientList> patientList;
    PatientListAdapter adapter;
    UsersHelperClass post;
    ChatDetails data;
    PatientList patient;
    FirebaseAuth auth;

    DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);

        phoneNo = getIntent().getStringExtra("phoneNo");

        patientListRef = FirebaseDatabase.getInstance().getReference("chats/");

        recyclerView = findViewById(R.id.patientListRecyclerView);

        patientList = new ArrayList<>();

        getData();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListOfPatient=findViewById(R.id.listofp);
        ListOfPatient.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),ListOfPatients.class);
            startActivity(i);
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String text) {
        ArrayList<PatientList> filteredList = new ArrayList<PatientList>();

        for (PatientList item : patientList) {
            if (item.getPatientName().toLowerCase().contains(text.toLowerCase()) || item.getPatientPhone().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (!filteredList.isEmpty()) {
            Toast.makeText(this,"No Patient Found",Toast.LENGTH_LONG).show();
        }
        else{
            adapter.setFilteredItem(filteredList);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            shared = getSharedPreferences("UserDetails", 0);
            sharedEditor = shared.edit();

            auth = FirebaseAuth.getInstance();
            sharedEditor.putString("isLogin", false + "");
            sharedEditor.apply();

            auth.signOut();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        patientListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String chatWith = dataSnapshot.getKey();
                    data = new ChatDetails();
                    data = dataSnapshot.child("chatDetails").getValue(ChatDetails.class);
                    System.out.println("--------" + data.getChatWith());

                    patient = new PatientList(data.getChatName(), data.getChatEmail(), data.getChatAddress(), data.getChatWith());

                }
                patientList.add(patient);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        creatRecycler();
    }

    private void setPatientData(String chatWith) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    post = singleSnapshot.getValue(UsersHelperClass.class);


                    System.out.println("patient: " + patient);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);

    }

    private void creatRecycler() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        patientList = new ArrayList<>();
        addItems();

        recyclerView.scrollTo(0, 0);
    }

    private void addItems() {
        adapter = new PatientListAdapter(this, patientList);
        recyclerView.setAdapter(adapter);

    }
}
