package com.example.android.drassist.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.drassist.R;
import com.example.android.drassist.home.Chat;
import com.example.android.drassist.home.PatientList;
import com.example.android.drassist.user.PatientDetailsScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    Context context;
    ArrayList<PatientList> patientList;

    public PatientListAdapter(Context context, ArrayList<PatientList> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    public void setFilteredItem(ArrayList<PatientList> list) {
        this.patientList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.patient_list_item, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientList chat = patientList.get(position);
        System.out.println("patient List:  " + patientList);

        holder.pName.setText(chat.getPatientName());
        holder.pMail.setText(chat.getPatientEmail());
        holder.pAddress.setText(chat.getPatientAddress());

        System.out.println("jbfjhsfjh" + chat.getPatientName() + " " + chat.getPatientEmail());

        holder.parentLayout.setOnClickListener((view) -> {
            Intent i = new Intent(context, PatientDetailsScreen.class);
            i.putExtra("patientName", patientList.get(position).getPatientName());
            i.putExtra("patientPhone", patientList.get(position).getPatientPhone());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView pName, pMail, pAddress;
        androidx.cardview.widget.CardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pName = itemView.findViewById(R.id.patientName);
            pMail = itemView.findViewById(R.id.patientEmail);
            pAddress = itemView.findViewById(R.id.patientAddress);
            parentLayout = itemView.findViewById(R.id.parent_item_patient_list);

        }
    }
}
