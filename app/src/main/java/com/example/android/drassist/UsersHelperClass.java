package com.example.android.drassist;

import android.app.Application;

public class UsersHelperClass  {
    public String name, sex,bloodGroup,height,weight,address, email, phoneNo, userType,uid;

    public UsersHelperClass() {
    }
    public UsersHelperClass(String uid ,String name,String  sex,String bloodGroup,String height,String weight,String address,String  email,String  phoneNo, String userType) {
        this.uid = uid;
        this.name = name;
        this.sex = sex;
        this.bloodGroup = bloodGroup;
        this.height = height;
        this.weight = weight;
        this.address = address;
        this.email = email;
        this.phoneNo = phoneNo;
        this.userType = userType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

}
