package dev.muvi.nhonga.model;

import com.google.firebase.database.DatabaseReference;

import dev.muvi.nhonga.helper.FirebaseConfig;

public class User {

    private String userid;
    private String name;
    private String phone;
    private String address;

    public String getProfileimageurl() {
        return profileimageurl;
    }

    public void setProfileimageurl(String profileimageurl) {
        this.profileimageurl = profileimageurl;
    }

    private String profileimageurl;


    public User() {
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public  void salvar(){

        DatabaseReference dbref = FirebaseConfig.getReferenceFirebase();
        DatabaseReference adsRef = dbref.child("user")
                .child(getUserid());
        adsRef.setValue(this);


    }
}
