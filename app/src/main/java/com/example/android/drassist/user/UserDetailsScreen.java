package com.example.android.drassist.user;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetailsScreen extends AppCompatActivity {

    String phoneNo, pUserType,uid;
    TextView pName, pBloodGroup, pHeight, pWeight, pSex, pAddress, pEmail, pPhone;
    Button logoutButton ;


    SharedPreferences shared;
    SharedPreferences.Editor sharedEditor;

    UsersHelperClass post;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        auth = FirebaseAuth.getInstance();

        phoneNo=getIntent().getStringExtra("phoneNo");

        pName = findViewById(R.id.patientName);
        pBloodGroup = findViewById(R.id.patientBloodGroup);
        pHeight = findViewById(R.id.patientHeight);
        pWeight = findViewById(R.id.patientWeight);
        pSex = findViewById(R.id.patientSex);
        pAddress = findViewById(R.id.patientAddress);
        pEmail = findViewById(R.id.patientEmail);
        pPhone = findViewById(R.id.patientPhone);

        getUserData();


        logoutButton=findViewById(R.id.logout);

        logoutButton.setOnClickListener(view -> {
            shared = getSharedPreferences("UserDetails", 0);
            sharedEditor = shared.edit();

            auth = FirebaseAuth.getInstance();
            sharedEditor.putString("isLogin", false + "");
            sharedEditor.apply();

            auth.signOut();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

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
                Log.i(TAG, "getData: " + post.toString());
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
