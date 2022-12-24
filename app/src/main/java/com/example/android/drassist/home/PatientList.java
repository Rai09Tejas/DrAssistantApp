package com.example.android.drassist.home;


public class PatientList {
    String patientName,patientEmail,patientAddress,patientPhone;

    public PatientList() {
    }

    public PatientList(String patientName, String patientEmail, String patientAddress,String patientPhone) {
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientAddress = patientAddress;
        this.patientPhone=patientPhone;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }
}
