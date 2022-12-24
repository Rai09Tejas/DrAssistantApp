package com.example.android.drassist.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.drassist.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupActivity extends AppCompatActivity {

    EditText NameText,AddressText,SexText,BloodGroupText,HeightText,WeightText,EmailAddressText,PhoneText;
    Button Register;
    TextView AlreadyRegisteredText;
    String userType = "patient";

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_signup);

         NameText= findViewById(R.id.Name);
         AddressText = findViewById(R.id.Address);
         SexText = findViewById(R.id.Sex);
         BloodGroupText = findViewById(R.id.BloodGroup);
         HeightText = findViewById(R.id.Height);
         WeightText = findViewById(R.id.Weight);
         EmailAddressText = findViewById(R.id.EmailAddress);
         PhoneText = findViewById(R.id.Phone);
         Register = findViewById(R.id.RegisterButton);
         AlreadyRegisteredText = findViewById(R.id.alreadyRegistered);

         AlreadyRegisteredText.setOnClickListener(view -> {
             Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
         });

        Register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 rootNode =FirebaseDatabase.getInstance();
                 reference = rootNode.getReference("users");

                 registerUser(view);
             }
         });

    }

    public void registerUser(View view) {
        if(!validateName() | !validatePhoneNo() | !validateEmail()|!validateAddress()|!validateHeight()|!validateBloodGroup()|!validateSex()|!validateWeight() )
        {
            return;
        }

        String name = NameText.getText().toString();
        String height = HeightText.getText().toString();
        String weight = WeightText.getText().toString();
        String bloodGroup = BloodGroupText.getText().toString();
        String sex = SexText.getText().toString();
        String email = EmailAddressText.getText().toString();
        String phoneNo = PhoneText.getText().toString();
        String address = AddressText.getText().toString();

        Intent i = new Intent(getApplicationContext(), VerifyPhoneOtpActivity.class);
        i.putExtra("phoneNo",phoneNo);
        i.putExtra("name",name);
        i.putExtra("sex",sex);
        i.putExtra("bloodGroup",bloodGroup);
        i.putExtra("height",height);
        i.putExtra("weight",weight);
        i.putExtra("address",address);
        i.putExtra("email",email);
        i.putExtra("userType",userType);
        i.putExtra("intentFrom","signup");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);




//        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
//        SignupActivity.this.startActivity(i);
//
//        SignupActivity.this.recreate();

    }

    private Boolean validateName() {
        String val = NameText.getText().toString();
        if (val.isEmpty()) {
            NameText.setError("Field cannot be empty");
            return false;
        }
        else {
            NameText.setError(null);
            return true;
        }
    }
    private Boolean validateAddress() {
        String val = AddressText.getText().toString();
        if (val.isEmpty()) {
            AddressText.setError("Field cannot be empty");
            return false;
        }
        else {
            AddressText.setError(null);
            return true;
        }
    }
    private Boolean validateSex() {
        String val = SexText.getText().toString();
        if (val.isEmpty()) {
            SexText.setError("Field cannot be empty");
            return false;
        }
        else {
            SexText.setError(null);
            return true;
        }
    }
    private Boolean validateHeight() {
        String val = HeightText.getText().toString();
        if (val.isEmpty()) {
            HeightText.setError("Field cannot be empty");
            return false;
        }
        else {
            HeightText.setError(null);
            return true;
        }
    }
    private Boolean validateWeight() {
        String val = WeightText.getText().toString();
        if (val.isEmpty()) {
            WeightText.setError("Field cannot be empty");
            return false;
        }
        else {
            WeightText.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = EmailAddressText.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            EmailAddressText.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            EmailAddressText.setError("Invalid email address");
            return false;
        } else {
            EmailAddressText.setError(null);
            return true;
        }
    }

    public Boolean validateBloodGroup(){
       
        if(BloodGroupText != null) {
            String[] bloodGroups = BloodGroupText.getResources().getStringArray(R.array.blood_groups);
            int len = bloodGroups.length;
            for (int i = 0; i < len; i++) {
                if(BloodGroupText.equals(bloodGroups[i]))
                    return false;
            }
        }else{
            BloodGroupText.setError("Invalid Blood Group");
            return true;
        }
        return true;
    }


    private Boolean validatePhoneNo() {
        String val = PhoneText.getText().toString();
        if (val.isEmpty()) {
            PhoneText.setError("Field cannot be empty");
            return false;
        } else {
            PhoneText.setError(null);
            return true;
        }
    }


}
