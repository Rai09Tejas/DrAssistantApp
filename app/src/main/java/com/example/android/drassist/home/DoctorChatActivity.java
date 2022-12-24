package com.example.android.drassist.home;


import android.content.SharedPreferences;
import android.speech.RecognizerIntent;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.adapters.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DoctorChatActivity extends AppCompatActivity {

    String phoneNo,patientName,userType;

    ImageButton send;
    Toolbar toolbar;
    Button prescribeButton;
    private ImageView mic;
    EditText chatEditText;

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference chatRef;
    FirebaseAuth auth;

    UsersHelperClass post;
    ChatDetails chatDetails;

    SharedPreferences shared;
    SharedPreferences.Editor sharedEditor;

    RecyclerView recyclerView;
    ChatAdapter adapter;
    ArrayList<Chat> chatList;


    String sentMessage = "";

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_chat_screen);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        phoneNo = getIntent().getStringExtra("phoneNo");

        shared = getSharedPreferences("UserDetails", 0);
        sharedEditor = shared.edit();

        userType = shared.getString("userType","");

        reference = database.getReference("users/" + phoneNo);
        patientName = getIntent().getStringExtra("pName");
        chatRef = database.getReference("chats/" + phoneNo + "/chat");
        recyclerView = findViewById(R.id.doctorChatRecycler);

        chatList = new ArrayList<>();
        post=new UsersHelperClass();

        getData();

        creatRecycler();

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    chatList.add(chat);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send=findViewById(R.id.doc_chat_send_button);
        prescribeButton =findViewById(R.id.prescribePatientButton);
        mic =findViewById(R.id.doc_chat_mic);
        chatEditText =findViewById(R.id.doc_chat_edittext);

        send.setOnClickListener(view -> {
            storeMessageToDB();
            sentMessage = "";
        });

        prescribeButton.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),PrescribePatientFormScreen.class);
            i.putExtra("phoneNo",phoneNo);
            i.putExtra("userType",userType);
            startActivity(i);
        });

        mic.setOnClickListener(v -> {
            Intent intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), " " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(patientName);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                sentMessage = Objects.requireNonNull(result).get(0);
                chatEditText.setText(sentMessage);
            }
        }
    }

    private void storeMessageToDB() {
        String text = chatEditText.getText().toString();
        if (!sentMessage.isEmpty() || !text.equals("")) {
            Chat chat = new Chat();
            if (!sentMessage.isEmpty()) {
                chat.setMessage(sentMessage);
            } else {
                chat.setMessage(text);
            }
            chat.setSentBy(userType);
            long unixTime = System.currentTimeMillis() / 1000L;
            chat.setTime(unixTime);
            chatRef.child(unixTime + "").setValue(chat);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats/" + phoneNo );
//            ref.setValue("abba",phoneNo);
            chatDetails = new ChatDetails(phoneNo,(System.currentTimeMillis()/1000L),post.getName(),post.getAddress(),post.getEmail());

            ref.child("chatDetails").setValue(chatDetails);
            sentMessage = "";
            chatEditText.setText("");
            recyclerList();
        }
    }

    public void getData() {
        System.out.println(reference);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot+"this is data " +phoneNo);
                post = dataSnapshot.getValue(UsersHelperClass.class);
                System.out.println(post+"this is post");

                post.setName(post.getName());
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




    private void creatRecycler() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerList();


    }

    private void recyclerList() {
        System.out.println("recyclerView userType: "+userType);
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(this, chatList,userType);

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
