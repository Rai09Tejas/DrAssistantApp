package com.example.android.drassist.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android.drassist.R;
import com.example.android.drassist.UsersHelperClass;
import com.example.android.drassist.adapters.PatientListAdapter;
import com.example.android.drassist.adapters.VPFragmentAdapter;
import com.example.android.drassist.auth.LoginActivity;
import com.example.android.drassist.fragments.ChatFragment;
import com.example.android.drassist.fragments.NewPatientFragment;
import com.example.android.drassist.fragments.PatientListFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorHomeActivity extends AppCompatActivity {
    String phoneNo;

    DatabaseReference patientListRef;

    RecyclerView recyclerView;
    TextView ListOfPatient;

    androidx.appcompat.widget.Toolbar toolbar;

    SharedPreferences shared;
    SharedPreferences.Editor sharedEditor;

    ArrayList<PatientList> patientList;
    ArrayList<UsersHelperClass> list;
    PatientListAdapter adapter;
    UsersHelperClass post;
    ChatDetails data;
    PatientList patient;
    FirebaseAuth auth;

    DatabaseReference ref;

    TabLayout tab;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);

        phoneNo = getIntent().getStringExtra("phoneNo");

        patientListRef = FirebaseDatabase.getInstance().getReference("chats/");
        ref = FirebaseDatabase.getInstance().getReference("users/");

        recyclerView = findViewById(R.id.patientListRecyclerView);

        patientList = new ArrayList<>();
        list = new ArrayList<>();

        getData();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tab = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager2);

        VPFragmentAdapter adapter = new VPFragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFrag(new ChatFragment(),"CHATS");
        adapter.addFrag(new PatientListFragment(),"PATIENT LIST");
        adapter.addFrag(new NewPatientFragment(),"NEW PATIENT");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.setOnTabSelectedListener(onTabSelectedListener(viewPager));

//        ListOfPatient=findViewById(R.id.listofp);
//        ListOfPatient.setOnClickListener(view -> {
//            Intent i = new Intent(getApplicationContext(),ListOfPatients.class);
//            startActivity(i);
//        });


    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager pager) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String text) {
        ArrayList<PatientList> filteredList = new ArrayList<PatientList>();

        for (PatientList item : patientList) {
            if (item.getPatientName().toLowerCase().contains(text.toLowerCase()) || item.getPatientPhone().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (!filteredList.isEmpty()) {
            Toast.makeText(this,"No Patient Found",Toast.LENGTH_LONG).show();
        }
        else{
            adapter.setFilteredItem(filteredList);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            shared = getSharedPreferences("UserDetails", 0);
            sharedEditor = shared.edit();

            auth = FirebaseAuth.getInstance();
            sharedEditor.putString("isLogin", false + "");
            sharedEditor.apply();

            auth.signOut();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


    private void getData() {
        patientListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    System.out.println("--------"+postSnapshot);
                    UsersHelperClass university = postSnapshot.getValue(UsersHelperClass.class);
                    list.add(university);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });
        PatientListFragment pat = new PatientListFragment();
        pat.setPatientList(list);
    }


}
