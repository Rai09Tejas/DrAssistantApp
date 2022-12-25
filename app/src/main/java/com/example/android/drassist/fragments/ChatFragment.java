package com.example.android.drassist.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.adapters.PatientListAdapter;
import com.example.android.drassist.home.ChatDetails;
import com.example.android.drassist.home.PatientList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    SharedPreferences shared;
    SharedPreferences.Editor sharedEditor;

    ArrayList<PatientList> patientList;
    PatientListAdapter adapter;

    DatabaseReference patientListRef;

    RecyclerView recyclerView;
    UsersHelperClass post;
    ChatDetails data;
    PatientList patient;
    FirebaseAuth auth;
    DatabaseReference ref;
    public ChatFragment() {
    }

    public ChatFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        patientListRef = FirebaseDatabase.getInstance().getReference("chats/");

        recyclerView = view.findViewById(R.id.patientListRecyclerView);

        patientList = new ArrayList<>();
        getData();
        creatRecycler();

        // Inflate the layout for this fragment
        return view;
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

//        creatRecycler();
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        patientList = new ArrayList<>();
        addItems();

        recyclerView.scrollTo(0, 0);
    }

    private void addItems() {
        adapter = new PatientListAdapter(getContext(), patientList);
        recyclerView.setAdapter(adapter);

    }
}