package com.example.android.drassist.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.adapters.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientListFragment extends Fragment {

    DatabaseReference patientListRef;
    String phoneNo;
    RecyclerView recyclerView;
    ArrayList<UsersHelperClass> patientList;
    UserListAdapter adapter;

    UsersHelperClass post;

    public PatientListFragment() {
    }

    public PatientListFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    public void setPatientList(ArrayList<UsersHelperClass> patientList) {
        this.patientList = patientList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_patient_list, container, false);
        patientListRef = FirebaseDatabase.getInstance().getReference("users/");

        recyclerView = view.findViewById(R.id.recyclerView);

        patientList = new ArrayList<>();
        getList();
        creatRecycler();
        // Inflate the layout for this fragment
        return view;
    }


    private void getList() {
        patientListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                patientList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    System.out.println("--------"+postSnapshot);
                    UsersHelperClass university = postSnapshot.getValue(UsersHelperClass.class);
                    patientList.add(university);

                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });
        creatRecycler();
    }


    private void creatRecycler() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        patientList = new ArrayList<>();
        addItems();

        recyclerView.scrollTo(0, 0);
    }

    private void addItems() {
        adapter = new UserListAdapter(getContext(), patientList);
        recyclerView.setAdapter(adapter);

    }
}