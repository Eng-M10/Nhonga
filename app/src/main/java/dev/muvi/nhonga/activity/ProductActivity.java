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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
    private List<String> retrivedphotoUrl = new ArrayList<>();
    String usr,ProducID;
    private DatabaseReference dbref;
    private StorageReference storageReference;
    private AlertDialog alertDialog;
    private Product product,selectedProduct;


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


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            selectedProduct = (Product) bundle.getSerializable("product");


            retriveDataProduct(selectedProduct);

        }

    }

    private void retriveDataProduct(Product productid) {
        usr = productid.getUser_id();
        retrivedphotoUrl = productid.getImages();
        DatabaseReference prodref = dbref
                .child("products")
                .child(usr)
                .child(productid.getProduct_id());



        prodref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){

                    Product pro = snapshot.getValue(Product.class);
                    ProducID = productid.getProduct_id();
                    edtName.setText(pro.getProduct_name());
                    edtPhone.setText(""+pro.getProduct_phone());
                    edtPrice.setText(""+pro.getProduct_price());
                    edtDesc.setText(""+pro.getProduct_description());

                    retrivedphotoUrl = pro.getImages();

                    if(!pro.getImages().isEmpty()){

                        if(pro.getImages().size() == 1){
                           Uri urlimg1 = Uri.parse(pro.getImages().get(0));
                           Picasso.get().load(urlimg1).into(imgprod1);

                        }else{
                            Uri urlimg1 = Uri.parse(pro.getImages().get(0));
                            Uri urlimg2 = Uri.parse(pro.getImages().get(1));
                            Picasso.get().load(urlimg1).into(imgprod1);
                            Picasso.get().load(urlimg2).into(imgprod2);
                        }

                    }else {
                        Toast.makeText(ProductActivity.this,"Sem imagens",Toast.LENGTH_LONG).show();
                    }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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

        // Verifica se há novas imagens para upload
        if (photoselected.size() > 0) {
            for (int i = 0; i < photoselected.size(); i++) {
                String img_url = photoselected.get(i);
                int size = photoselected.size();
                saveUploadphotos(img_url, size, i);
            }
        } else {
            // Se não houver novas imagens, apenas atualize os dados do produto
            updateProductData();
        }


    }

    private void updateProductData() {

        product.setImages(retrivedphotoUrl);
        product.salvar();

        alertDialog.dismiss();
        finish();
        Toast.makeText(ProductActivity.this, getString(R.string.strSuccessValid), Toast.LENGTH_LONG).show();



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
            }else{
                updateProductData();
            }






        })).addOnFailureListener(e -> Toast.makeText(ProductActivity.this,getString(R.string.strUploadImageError),Toast.LENGTH_LONG).show());


    }


    private boolean isValid(String parr){
        return parr.length() > 2 ? true : false;
    }

    private boolean isValidPrice(Double parr){
        if(parr <= 0.00){
            return false;
        }else{
            return true;
        }
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

    private Product configProduct(){
        String productname = edtName.getText().toString();
        String price;
        Double Dbprice;
        price = edtPrice.getText().toString();

        if(!price.isEmpty()) {
             Dbprice = Double.parseDouble(price);
        }else {
             Dbprice = 0.00;
        }

        String phone = edtPhone.getText().toString();
        String desc = edtDesc.getText().toString();

        Product product = new Product();
        product.setUser_id(usr);
        product.setProduct_name(productname);

        if(ProducID != null){
            product.setProduct_id(ProducID);
        }

        product.setProduct_price(Dbprice);
        product.setProduct_phone(phone);
        product.setProduct_description(desc);

        return product;


    }





    private void Validate() {

        product = configProduct();

        if(photoselected.size() != 0 || !retrivedphotoUrl.isEmpty() ){

            if(!product.getProduct_name().isEmpty() && isValid(product.getProduct_name())){

                if(!product.getProduct_price().toString().isEmpty() && isValidPrice(product.getProduct_price())){

                    if(!product.getProduct_phone().isEmpty() && isvalidaPhone(product.getProduct_phone())){

                        if(!product.getProduct_description().isEmpty() && isValid(product.getProduct_description())){

                            SavingProduct();


                        }else {
                            edtDesc.setError("Digite uma descrição válida!");
                        }

                    }else{
                        edtPhone.setError("Digite um telefone válida!");

                    }


                }else{
                    edtPrice.setError("Digite um preço válida!");
                }

            }else{
                edtName.setError("Digite um nome válida!");
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