package dev.muvi.nhonga.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import dev.muvi.nhonga.helper.FirebaseConfig;

public class Advertiser implements Serializable {

    private String user_id;
    private String img_url;
    private String name;
    private String filter_name;
    private String category;
    private String nrCategory;
    private String address;

    public Advertiser() {

    }

    public String getFilter_name() {
        return filter_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.filter_name = name.toLowerCase();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNrCategory() {
        return nrCategory;
    }

    public void setNrCategory(String nrCategory) {
        this.nrCategory = nrCategory;
    }

    public void salvar(){
        DatabaseReference dbref = FirebaseConfig.getReferenceFirebase();
        DatabaseReference adsRef = dbref.child("advertiser")
                                        .child(getUser_id());
        adsRef.setValue(this);
    }
}
