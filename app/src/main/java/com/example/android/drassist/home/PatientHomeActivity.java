package com.example.android.drassist.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.adapters.ChatAdapter;
import com.example.android.drassist.auth.LoginActivity;
import com.example.android.drassist.user.UserDetailsScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PatientHomeActivity extends AppCompatActivity {

    String phoneNo, userType, uid;


    UsersHelperClass post;
    ChatDetails chatDetails;

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference chatRef;
    FirebaseAuth auth;

    private ImageView iv_mic,userProfile;
    private EditText messageInput;
    private ImageView send;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    RecyclerView recyclerView;
    ChatAdapter adapter;
    ArrayList<Chat> chatList;

    String sentMessage = "";

    SharedPreferences shared;
    SharedPreferences.Editor sharedEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        phoneNo = getIntent().getStringExtra("phoneNo");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        reference = database.getReference("users/" + phoneNo);
        chatRef = database.getReference("chats/" + phoneNo + "/chat");
        Log.i(TAG, "onCreate: " + reference);

        getData();

        userProfile = findViewById(R.id.userProfile);

        userProfile.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), UserDetailsScreen.class);
            intent.putExtra("phoneNo", phoneNo);
            startActivity(intent);
        });

        iv_mic = findViewById(R.id.iv_mic);
        messageInput = findViewById(R.id.messageBox);
        send = findViewById(R.id.sendButton);

        iv_mic.setOnClickListener(v -> {
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
                Toast.makeText(PatientHomeActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.chatRecyclerView);
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

        send.setOnClickListener(view -> {
            storeMessageToDB();
            sentMessage = "";
        });
    }

    private void creatRecycler() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerList();

    }


    private void recyclerList() {
        System.out.println("recyclerView userType: "+userType);
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(this, chatList,"patient");

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                if (adapter.getItemCount() != 0) {
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                }else{
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void storeMessageToDB() {
        String text = messageInput.getText().toString();
        if (!sentMessage.isEmpty() || !text.equals("")) {
            Chat chat = new Chat();
            if (!sentMessage.isEmpty()) {
                chat.setMessage(sentMessage);
            } else {
                chat.setMessage(text);
            }
            chat.setSentBy(post.userType);
            long unixTime = System.currentTimeMillis() / 1000L;
            chat.setTime(unixTime);
            chatRef.child(unixTime + "").setValue(chat);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats/" + phoneNo);
//            ref.setValue("abba",phoneNo);
            chatDetails = new ChatDetails(phoneNo, (System.currentTimeMillis() / 1000L), post.getName(), post.getAddress(), post.getEmail());

            ref.child("chatDetails").setValue(chatDetails);
            sentMessage = "";
            messageInput.setText("");
            recyclerList();
//            creatRecycler();
        }
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
                messageInput.setText(sentMessage);
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
                shared = getSharedPreferences("UserDetails", 0);
                sharedEditor = shared.edit();

                auth = FirebaseAuth.getInstance();
                sharedEditor.putString("isLogin", false + "");
                sharedEditor.apply();

                auth.signOut();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}

