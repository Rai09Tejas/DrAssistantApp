package com.example.android.drassist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.android.drassist.auth.LoginActivity;
import com.example.android.drassist.home.DoctorHomeActivity;
import com.example.android.drassist.home.PatientHomeActivity;

public class AppHomeActivity extends AppCompatActivity {

    SharedPreferences shared;
    String isLoggedIn,userType,phone,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home);

        shared = getSharedPreferences("UserDetails", MODE_PRIVATE);

        isLoggedIn = shared.getString("isLogin",false+"");
        userType = shared.getString("userType","patient");
        phone = shared.getString("loginPhone","1111111111");
        uid = shared.getString("uid","uid");

        if(isLoggedIn.equals("true")){
            if(userType.equals("doctor")){
                Intent i = new Intent(getApplicationContext(), DoctorHomeActivity.class);
                i.putExtra("phoneNo",phone);
                startActivity(i);
            }else{
                Intent i = new Intent(getApplicationContext(), PatientHomeActivity.class);
                i.putExtra("phoneNo",phone);
                startActivity(i);
            }
        }else{
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }

    }
}