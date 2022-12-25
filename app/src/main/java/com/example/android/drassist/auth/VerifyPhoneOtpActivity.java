package com.example.android.drassist.auth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.home.DoctorHomeActivity;
import com.example.android.drassist.home.PatientHomeActivity;
import com.google.firebase.FirebaseError;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneOtpActivity extends AppCompatActivity {

    String intentFrom, uid, verifyCodeBySystem, phoneNo, name, sex, bloodGroup, height, weight, address, email, userType;
    EditText otp;
    Button verifyButton;
    ProgressBar progressBar;
    TextView resendOpt,resendOpt2, Phone;

    UsersHelperClass helperClass;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    SharedPreferences shared;
    SharedPreferences.Editor sharedEditor;
    String isLogin;

    boolean isStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_verifyphoneotp);

        shared = getSharedPreferences("UserDetails", 0);
        sharedEditor = shared.edit();
        isLogin = shared.getString("isLogin", false + "");

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        verifyButton = findViewById(R.id.verifyOtp);
        otp = findViewById(R.id.enteredOtp);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        uid = FirebaseAuth.getInstance().getUid();
        phoneNo = getIntent().getStringExtra("phoneNo");
        intentFrom = getIntent().getStringExtra("intentFrom");
        if (Objects.equals(intentFrom, "signup")) {
            name = getIntent().getStringExtra("name");
            sex = getIntent().getStringExtra("sex");
            bloodGroup = getIntent().getStringExtra("bloodGroup");
            height = getIntent().getStringExtra("height");
            weight = getIntent().getStringExtra("weight");
            address = getIntent().getStringExtra("address");
            email = getIntent().getStringExtra("email");
            userType = getIntent().getStringExtra("userType");

            helperClass = new UsersHelperClass(uid, name, sex, bloodGroup, height, weight, address, email, phoneNo, userType);

        } else {
            getUserData();
        }

        sendVerificationCode(phoneNo);

        verifyButton.setOnClickListener(view -> {
            String code = otp.getText().toString();
            if (code.length() != 6) {
                otp.setError("Invalid OTP");
                otp.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);

        });

        resendOpt = findViewById(R.id.resend);
        resendOpt2 = findViewById(R.id.resend2);
        Phone = findViewById(R.id.phoneNumberOtp);

        Phone.setText(String.format("+91 %s", phoneNo));

        resendOpt.setOnClickListener(view -> {
            sendVerificationCode(phoneNo);
        });
        resendOpt2.setOnClickListener(view -> {
            sendVerificationCode(phoneNo);
        });
    }

    private void getUserData() {

        reference.child(phoneNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    UsersHelperClass value = snapshot.getValue(UsersHelperClass.class);

//                System.out.println("getUserdata-0000----------"+value.getName());
                    helperClass = new UsersHelperClass();
                    helperClass.setName(value.getName());
                    helperClass.setAddress(value.getAddress());
                    helperClass.setHeight(value.getHeight());
                    helperClass.setWeight(value.getWeight());
                    helperClass.setSex(value.getSex());
                    helperClass.setEmail(value.getEmail());
                    helperClass.setBloodGroup(value.getBloodGroup());
                    helperClass.setUserType(value.getUserType());
                    helperClass.setPhoneNo(phoneNo);
                    helperClass.setUid(uid);

                    userType = value.getUserType();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationCode(String phoneNo) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneOtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verifyCodeBySystem = s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential cred = PhoneAuthProvider.getCredential(verifyCodeBySystem, codeByUser);
        signInUserByCred(cred, uid, phoneNo, name, sex, bloodGroup, height, weight, address, email, userType);
    }

    private void signInUserByCred(PhoneAuthCredential cred, String uid, String phoneNo, String name, String sex, String bloodGroup, String height, String weight, String address, String email, String userType) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(cred).addOnCompleteListener(VerifyPhoneOtpActivity.this, task -> {
            if (task.isSuccessful()) {

                sharedEditor.putString("uid", uid);
                sharedEditor.putString("loginPhone", phoneNo);
                sharedEditor.putString("isLogin", true + "");
                sharedEditor.putString("userType", userType);
                sharedEditor.apply();

                if (!isStored()) {

                    System.out.println("data stored: " + helperClass);
                    reference.child(phoneNo).setValue(helperClass);
                }

                System.out.println("data is Not stored: -----------" + helperClass);
                Intent intent;
                System.out.println("------------" + userType);
                if (userType.equals("patient")) {
                    intent = new Intent(getApplicationContext(), PatientHomeActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), DoctorHomeActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                intent.putExtra("sex", sex);
                intent.putExtra("email", email);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("bloodGroup", bloodGroup);
                intent.putExtra("height", height);
                intent.putExtra("weight", weight);
                intent.putExtra("address", address);
                startActivity(intent);
            } else {
                Toast.makeText(VerifyPhoneOtpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isStored() {

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("users/");
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child(phoneNo).exists()) {
                        isStore = true;
                    } else {
                        isStore = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return isStore;
    }
}
