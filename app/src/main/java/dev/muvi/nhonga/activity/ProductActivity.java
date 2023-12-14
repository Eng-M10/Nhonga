package dev.muvi.nhonga.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import dev.muvi.nhonga.R;
import dev.muvi.nhonga.helper.FirebaseConfig;
import dev.muvi.nhonga.helper.UserFirebase;
import dev.muvi.nhonga.model.Product;
import dmax.dialog.SpotsDialog;

public class ProductActivity extends AppCompatActivity
implements View.OnClickListener{
    EditText edtName, edtPrice, edtPhone , edtDesc;
    Button btnSave;
    ImageView imgprod1 , imgprod2;
    private final int SELECT_GALLERY = 200;
    String urlimg = "";
    private List<String> photoselected = new ArrayList<>();
    private List<String> photoselectedUrl = new ArrayList<>();
    String usr;
    private DatabaseReference dbref;
    private StorageReference storageReference;
    private AlertDialog alertDialog;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_product);
        initComponents();
        usr = UserFirebase.getUserID();
        dbref = FirebaseConfig.getReferenceFirebase();
        storageReference = FirebaseConfig.getReferenceStorage();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.strnewproduct));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initComponents(){
        edtName = findViewById(R.id.edtProdName);
        edtPrice = findViewById(R.id.edtProdPrice);
        edtPhone = findViewById(R.id.edtProdPhone);
        edtDesc = findViewById(R.id.edtProdDesc);
        imgprod1 = findViewById(R.id.imgProduct1);
        imgprod2 = findViewById(R.id.imgProduct2);
        btnSave = findViewById(R.id.btnSaveProduct);

        imgprod1.setOnClickListener(this);
        imgprod2.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void SavingProduct(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.strSaving)
                .setCancelable(false)
                .build();

        alertDialog.show();


        for(int i=0; i<photoselected.size();i++){
            String img_url = photoselected.get(i);
            int size = photoselected.size();
            saveUploadphotos(img_url, size,i);
        }

    }

    private void saveUploadphotos(String imgUrl, int size, int i) {

        final StorageReference img = storageReference.child("images")
                .child("products")
                .child(product.getProduct_id())
                .child("img" + i);

        UploadTask uploadTask = img.putFile(Uri.parse(imgUrl));
        uploadTask.addOnSuccessListener(taskSnapshot -> img.getDownloadUrl().addOnCompleteListener(task -> {
            Uri urlimage = task.getResult();
            String addrOnFirebase = urlimage.toString();
            photoselectedUrl.add(addrOnFirebase);

            if(size == photoselectedUrl.size()){
                product.setImages(photoselectedUrl);
                product.salvar();

                alertDialog.dismiss();
                finish();
                Toast.makeText(ProductActivity.this,getString(R.string.strSuccessValid),Toast.LENGTH_LONG).show();
            }

        })).addOnFailureListener(e -> Toast.makeText(ProductActivity.this,getString(R.string.strUploadImageError),Toast.LENGTH_LONG).show());


    }


    private boolean isValid(String parr){
        return parr.length() > 2 ? true : false;
    }
    private Boolean _validate(String name, String price, String phone, String desc){

        if(name.isEmpty() && isValid(name) ){
            edtName.setError(getString(R.string.strErrNameAds));
            return false;
        }else if(price.isEmpty() && price != "0" ){
            edtPrice.setError("");
            return false;
        }else if(phone.isEmpty() && isvalidaPhone(phone)){
            edtPhone.setError("");
            return false;
        }else if(desc.isEmpty() && isValid(desc)){
            edtDesc.setError("");
            return false;
        }

        return true;
    }



//Implementation method of Listener

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgProduct1){
            choosePicture(1);
        }else if(v.getId() ==  R.id.imgProduct2){
            choosePicture(2);
        } else if (v.getId() == R.id.btnSaveProduct) {
            Validate();
        }

    }

    /**
     *
     *
     *
     */

    private Product configProduct(){
        String productname = edtName.getText().toString();
        String price;
        price = edtPrice.getText().toString();
        String phone = edtPhone.getText().toString();
        String desc = edtDesc.getText().toString();

        Product product = new Product();
        product.setUser_id(usr);
        product.setProduct_name(productname);
        product.setProduct_price(price);
        product.setProduct_phone(phone);
        product.setProduct_description(desc);

        return product;


    }





    private void Validate() {

        product = configProduct();

        if(photoselected.size() != 0){
            if(_validate(product.getProduct_name(),product.getProduct_price().toString(),product.getProduct_phone(),product.getProduct_description())){
                SavingProduct();
            }else{
                Toast.makeText(ProductActivity.this,getString(R.string.strFailedValid),Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(ProductActivity.this,getString(R.string.strSelectImage),Toast.LENGTH_LONG).show();
        }

    }

    private boolean isvalidaPhone(String phone) {
        if(phone.startsWith("8") && phone.length() == 9 ){
            return true;
        }else{
            return false;
        }

    }



    // Chossing pictures
    public void choosePicture(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            Uri imgSelected = data.getData();
            String imgpath = imgSelected.toString();

            if(requestCode == 1){
                imgprod1.setImageURI(imgSelected);

            }else if(requestCode == 2){
                imgprod2.setImageURI(imgSelected);
            }

            photoselected.add(imgpath);

        }

    }
}