package dev.muvi.nhonga.model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dev.muvi.nhonga.helper.FirebaseConfig;

public class Product {


    private String user_id;
    private String product_id;
    private String product_name;
    private Double product_price;
    private String product_phone;
    private String product_description;
    private List<String> images;


    public Product() {
        DatabaseReference prodRef = FirebaseConfig.getReferenceFirebase()
                .child("product");
        setProduct_id(prodRef.push().getKey());
    }



    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }


    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = Double.parseDouble(product_price);
    }

    public String getProduct_phone() {
        return product_phone;
    }

    public void setProduct_phone(String product_phone) {
        this.product_phone = product_phone;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public void removeOn(){
        DatabaseReference dbref = FirebaseConfig.getReferenceFirebase();
        DatabaseReference adsRef = dbref
                .child("products")
                .child(getUser_id())
                .child(getProduct_id());
        adsRef.removeValue();

    }


    public void salvar(){
        DatabaseReference dbref = FirebaseConfig.getReferenceFirebase();
        DatabaseReference adsRef = dbref.child("products")
                .child(getUser_id())
                .child(getProduct_id());
        adsRef.setValue(this);
    }
}
