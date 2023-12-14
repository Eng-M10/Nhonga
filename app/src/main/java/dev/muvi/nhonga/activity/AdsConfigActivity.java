package dev.muvi.nhonga.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.muvi.nhonga.R;
import dev.muvi.nhonga.helper.FirebaseConfig;
import dev.muvi.nhonga.helper.UserFirebase;
import dev.muvi.nhonga.model.Advertiser;
import dmax.dialog.SpotsDialog;

public class AdsConfigActivity extends AppCompatActivity {

    EditText edtAdsName, edtAddress;
    Spinner spinner;
    Button btnSave;

    CircleImageView img;
    private static final int SELECT_GALLERY = 200;
    private DatabaseReference dbref;
    private  StorageReference storageReference;
    private String usr;
    private String urlimg = "";
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_ads_config);
        initComponents();
        storageReference = FirebaseConfig.getReferenceStorage();
        dbref = FirebaseConfig.getReferenceFirebase();
        usr = UserFirebase.getUserID();
        addSpinner();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.strSett));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        );

                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECT_GALLERY);
                }
            }
        });

        retriveAdsData();

        btnSave.setOnClickListener(v -> {
            Validate();

        });

    }
    private void retriveAdsData(){
        DatabaseReference adsref = dbref
                .child("advertiser")
                .child(usr);
        adsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    Advertiser ads = snapshot.getValue(Advertiser.class);
                    edtAdsName.setText(ads.getName());
                    edtAddress.setText(ads.getAddress());
                    int position = Integer.parseInt(ads.getNrCategory());

                    //Recuperar dado do Spinner
                    String[] spinnerDataArray = getResources().getStringArray(R.array.categorias);
                    List<String> spinnerData = Arrays.asList(spinnerDataArray);

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                            AdsConfigActivity.this,
                            android.R.layout.simple_spinner_item,
                            spinnerData
                    );

                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    // Obtenha a posição armazenada no Firebase
                    int posicaoSelecionadaFirebase = position; // Substitua pelo campo real no seu objeto

                    // Verifique se a posição é válida antes de selecionar
                    if (posicaoSelecionadaFirebase >= 0 && posicaoSelecionadaFirebase < spinnerData.size()) {
                        spinner.setSelection(posicaoSelecionadaFirebase);
                    }

                    //Recuperar Imagem de Perfil
                    urlimg = ads.getImg_url();
                    if( urlimg != ""){
                        Picasso.get()
                                .load(urlimg)
                                .into(img);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap image = null;

            try {
                switch (requestCode){
                    case SELECT_GALLERY:
                        Uri localImage = data.getData();
                        image = MediaStore.Images
                                .Media.getBitmap(
                                        getContentResolver(),
                                        localImage
                                );
                        break;
                }
                if(image != null){
                    alertDialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage(R.string.strSaving)
                            .setCancelable(false)
                            .build();

                    alertDialog.show();



                    img.setImageBitmap(image);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 70,baos);
                    byte[] dadosImage = baos.toByteArray();

                    final StorageReference imgRef = storageReference
                            .child("images")
                            .child("advertiser")
                            .child(usr + "png");

                    UploadTask uploadTask = imgRef.putBytes(dadosImage);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdsConfigActivity.this,getString(R.string.strUploadImageError) +"> "+e.getMessage() ,Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                               @Override
                               public void onComplete(@NonNull Task<Uri> task) {
                                 Uri url = task.getResult();
                                 urlimg = url.toString();

                                 alertDialog.dismiss();
                               }
                           });
                            Toast.makeText(AdsConfigActivity.this,getString(R.string.strUploadSuccess),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }catch (Exception e){
                    e.printStackTrace();
            }
        }

    }

    private void initComponents() {
        this.spinner = findViewById(R.id.spnCat);
        this.edtAdsName = findViewById(R.id.textUserName);
        this.edtAddress = findViewById(R.id.textUserPhone);
        this.img = findViewById(R.id.profile_image);
        this.btnSave = findViewById(R.id.btnSaveUserConfig);
    }


    private void addSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categorias,
                android.R.layout.simple_spinner_dropdown_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica o adapter ao spinner
        spinner.setAdapter(adapter);


    }




    public void Validate() {
        String name = edtAdsName.getText().toString();
        String cat = spinner.getSelectedItem().toString();
        String nrcat =Integer.toString(spinner.getSelectedItemPosition());
        String addr = edtAddress.getText().toString();

        if(!name.isEmpty() && isvalid(name)){

            if(!addr.isEmpty() && isvalid(addr)){


                Advertiser ads = new Advertiser();
                ads.setUser_id(usr);
                ads.setName(name);
                ads.setImg_url(urlimg);
                ads.setCategory(cat);
                ads.setNrCategory(nrcat);
                ads.setAddress(addr);
                ads.salvar();
                finish();
                Toast.makeText(AdsConfigActivity.this,getString(R.string.strSuccessValid),Toast.LENGTH_LONG).show();

            }else{
                edtAddress.setError(getString(R.string.strErrAddrAds));
            }


        }else{
            edtAdsName.setError(getString(R.string.strErrNameAds));
        }

    }

    private boolean isvalid(String par){
        return par.length() > 2 ? true : false;

    }


}
