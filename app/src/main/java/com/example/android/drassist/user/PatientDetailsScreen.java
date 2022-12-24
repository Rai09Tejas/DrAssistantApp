package com.example.android.drassist.user;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.home.DoctorChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientDetailsScreen extends AppCompatActivity {

    Button gotoChat;
    String phoneNo,patName;

    TextView pName, pBloodGroup, pHeight, pWeight, pSex, pAddress, pEmail, pPhone;
    androidx.appcompat.widget.Toolbar toolbar;

    UsersHelperClass post;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details_screen);

        phoneNo = getIntent().getStringExtra("patientPhone");
        patName = getIntent().getStringExtra("patientName");

        gotoChat = findViewById(R.id.logout);
        pName = findViewById(R.id.patientName);
        pBloodGroup = findViewById(R.id.patientBloodGroup);
        pHeight = findViewById(R.id.patientHeight);
        pWeight = findViewById(R.id.patientWeight);
        pSex = findViewById(R.id.patientSex);
        pAddress = findViewById(R.id.patientAddress);
        pEmail = findViewById(R.id.patientEmail);
        pPhone = findViewById(R.id.patientPhone);

        pName.setText(patName);

        gotoChat.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), DoctorChatActivity.class);
            i.putExtra("phoneNo",phoneNo);
            i.putExtra("pName",patName);
            i.putExtra("userType",post.getUserType());
            startActivity(i);
        });

        getUserData();


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Patient Details");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDetails() {
        pName.setText(post.name);
        pBloodGroup.setText(post.bloodGroup);
        pHeight.setText(post.height);
        pWeight.setText(post.weight);
        pSex.setText(post.sex);
        pAddress.setText(post.address);
        pEmail.setText(post.email);
        pPhone.setText(post.phoneNo);

    }


    public void getUserData() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users/" + phoneNo);

        Log.i(TAG, "getData: " + reference);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(UsersHelperClass.class);
//                Log.i(TAG, "getData: " + post.toString());
                System.out.println(post.name);
                post.setName(post.name);
                post.setSex(post.sex);
                post.setHeight(post.height);
                post.setWeight(post.weight);
                post.setAddress(post.address);
                post.setEmail(post.email);
                post.setPhoneNo(post.phoneNo);
                post.setBloodGroup(post.bloodGroup);
                post.setUserType(post.userType);

                setDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}