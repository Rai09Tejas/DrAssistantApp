package com.example.android.drassist.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.drassist.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginPhone ;
    TextView txtSignUp;
    TextView txtSignUp2;
    Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_login);

        loginPhone = (EditText) findViewById(R.id.LoginPhone);
        loginButton = (Button) findViewById(R.id.button);

        txtSignUp = findViewById(R.id.newUserSignUp);
        txtSignUp2 = findViewById(R.id.newUserSignUp2);

        txtSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(intent);
        });
        txtSignUp2.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(this::loginUser);
    }

    private Boolean validatePhone(){
        return true;
    }

    private void loginUser(View view){
        if(!validatePhone()){
            return;
        }
        else{
            isUser();
        }
    }

    private void isUser() {
        String phone = loginPhone.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query check = reference.orderByChild("phoneNo").equalTo(phone);

        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String phoneNoFromDB = snapshot.child(phone).child("phoneNo").getValue(String.class);
                    Intent intent = new Intent(getApplicationContext(), VerifyPhoneOtpActivity.class);
                    intent.putExtra("phoneNo",phoneNoFromDB);
                    intent.putExtra("intentFrom","login");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please enter registered Mobile Number or Create New User",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
