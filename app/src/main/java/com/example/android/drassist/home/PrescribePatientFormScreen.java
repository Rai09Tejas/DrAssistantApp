package com.example.android.drassist.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PrescribePatientFormScreen extends AppCompatActivity {

    EditText name, test, disease, symptoms, prescription;
    FloatingActionButton send, share, mic;
    androidx.appcompat.widget.Toolbar toolbar;

    String pname, ptest, pdisease, psymptoms, pprescription;
    String newLine = System.getProperty("line.separator");
    String output, phoneNo, userType;

    UsersHelperClass post;
    ChatDetails chatDetails;

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference chatRef;

    String sentMessage = "";
    int fieldNo;
    int[] fieldTouchCount = {0, 0, 0, 0, 0};

    Intent impIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescription_form_screen);

        name = findViewById(R.id.formName);
        test = findViewById(R.id.formTests);
        disease = findViewById(R.id.formDisease);
        symptoms = findViewById(R.id.formSymp);
        prescription = findViewById(R.id.formPrescription);
        send = findViewById(R.id.floatingActionButton);
        share = findViewById(R.id.sharefab);
        mic = findViewById(R.id.fab);

        phoneNo = getIntent().getStringExtra("phoneNo");
        userType = getIntent().getStringExtra("userType");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users/" + phoneNo);
        chatRef = database.getReference("chats/" + phoneNo + "/chat");

        fieldNo = 0;


        getData();
        getValues();


        send.setOnClickListener(view -> {
            createPresc();
            storeMessageToDB();
            Intent i = new Intent(getApplicationContext(), DoctorChatActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("phoneNo", phoneNo);
            i.putExtra("pName", post.getName());
            startActivity(i);
        });

        share.setOnClickListener(view ->{
            createPresc();
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String body = output;
            String sub = "Patient Prescription";
            myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
            myIntent.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(Intent.createChooser(myIntent, "Share Using"));
        });

        mic.setOnClickListener(view->{
            impIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intentCall();
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Patient Prescription Form");
        setSupportActionBar(toolbar);

        editTextListener();

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



    @SuppressLint("ClickableViewAccessibility")
    private void editTextListener() {
        name.setOnTouchListener((view, motionEvent) -> {
            if (fieldTouchCount[0] == 0) {
                impIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentCall();
                fieldNo = 1;
                fieldTouchCount[fieldNo - 1] += 1;
                System.out.println(fieldTouchCount[fieldNo - 1]);
                return false;
            }
            return false;
        });


        disease.setOnTouchListener(( view, motionEvent) -> {
            if (fieldTouchCount[1] == 0) {
                impIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentCall();
                fieldNo = 2;
                fieldTouchCount[fieldNo - 1] += 1;
                return false;
            }
            return false;
        });


        symptoms.setOnTouchListener((view, motionEvent) -> {
            if (fieldTouchCount[2] == 0) {
                impIntent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentCall();
                fieldNo = 3;
                fieldTouchCount[fieldNo - 1] += 1;
                return false;
            }
            return false;
        });

        test.setOnTouchListener((View.OnTouchListener) (view, motionEvent) -> {
            if (fieldTouchCount[3] == 0) {
                impIntent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentCall();
                fieldNo = 4;
                fieldTouchCount[fieldNo - 1] += 1;
                return false;
            }
            return false;
        });


        prescription.setOnTouchListener((View.OnTouchListener) (view, motionEvent) -> {
            if (fieldTouchCount[4] == 0) {
                impIntent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentCall();
                fieldNo = 5;
                fieldTouchCount[fieldNo - 1] += 1;
                return false;
            }
            return false;
        });

    }

    public void intentCall() {
        impIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        impIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        impIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(impIntent, 1);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        int end, start;

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                sentMessage = Objects.requireNonNull(result).get(0);
                switch (fieldNo) {
                    case 1:
                        start = Math.max(name.getSelectionStart(), 0);
                        end = Math.max(name.getSelectionEnd(), 0);
                        name.getText().replace(Math.min(start, end), Math.max(start, end),
                                sentMessage, 0, sentMessage.length());
                        break;
                    case 2:
                        start = Math.max(disease.getSelectionStart(), 0);
                        end = Math.max(disease.getSelectionEnd(), 0);
                        disease.getText().replace(Math.min(start, end), Math.max(start, end),
                                sentMessage, 0, sentMessage.length());
                        break;
                    case 3:
                        start = Math.max(symptoms.getSelectionStart(), 0);
                        end = Math.max(symptoms.getSelectionEnd(), 0);
                        symptoms.getText().replace(Math.min(start, end), Math.max(start, end),
                                sentMessage, 0, sentMessage.length());
                        break;
                    case 4:
                        start = Math.max(test.getSelectionStart(), 0);
                        end = Math.max(test.getSelectionEnd(), 0);
                        test.getText().replace(Math.min(start, end), Math.max(start, end),
                                sentMessage, 0, sentMessage.length());
                        break;
                    case 5:
                        start = Math.max(prescription.getSelectionStart(), 0);
                        end = Math.max(prescription.getSelectionEnd(), 0);
                        prescription.getText().replace(Math.min(start, end), Math.max(start, end),
                                sentMessage, 0, sentMessage.length());
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    public void getData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot + "this is data");
                post = dataSnapshot.getValue(UsersHelperClass.class);
                System.out.println(post + "this is post");

                post.setName(post.name);
                post.setSex(post.sex);
                post.setHeight(post.height);
                post.setWeight(post.weight);
                post.setAddress(post.address);
                post.setEmail(post.email);
                post.setPhoneNo(post.phoneNo);
                post.setBloodGroup(post.bloodGroup);
                post.setUserType(post.userType);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    private void storeMessageToDB() {
        Chat chat = new Chat();
        chat.setMessage(output);

        chat.setSentBy(userType);
        long unixTime = System.currentTimeMillis() / 1000L;
        chat.setTime(unixTime);
        chatRef.child(unixTime + "").setValue(chat);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats/" + phoneNo);
        chatDetails = new ChatDetails(phoneNo, (System.currentTimeMillis() / 1000L), post.getName(), post.getAddress(), post.getEmail());

        ref.child("chatDetails").setValue(chatDetails);
    }

    private void getValues() {
        pname = name.getText().toString();
        pdisease = disease.getText().toString();
        pprescription = prescription.getText().toString();
        psymptoms = symptoms.getText().toString();
        ptest = test.getText().toString();
    }

    private void createPresc() {
        getValues();
        System.out.println(pname);
        output = String.join(newLine,
                "PATIENT PRESCRIPTION",
                " ",
                "Name of Patient  :  " + pname,
                " ",
                "Disease  :  " + pdisease,
                " ",
                "Symptoms  :  " + psymptoms,
                " ",
                "Tests  :  " + ptest,
                " ",
                "Prescription  :  " + pprescription
        );
    }


}
