package dev.muvi.nhonga.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;
import dev.muvi.nhonga.R;
import dev.muvi.nhonga.helper.FirebaseConfig;
import dev.muvi.nhonga.helper.UserFirebase;
import dev.muvi.nhonga.model.User;
import dmax.dialog.SpotsDialog;

public class UserConfigActivity extends AppCompatActivity {

    private EditText edtUserName, edtUserPhone , edtUserAddress;
    private CircleImageView imgUserProfile;
    private String userID;
    private DatabaseReference dbref;
    private  StorageReference storageReference;
    private Button btnSave;
    private static final int SELECT_GALLERY = 200;
    private String urlimg = "";
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_user_config);

        //
        intComponents();
        userID = UserFirebase.getUserID();
        dbref = FirebaseConfig.getReferenceFirebase();
        storageReference = FirebaseConfig.getReferenceStorage();
        //
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.strSett));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        retriveUserData();

        imgUserProfile.setOnClickListener(new View.OnClickListener() {
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

        btnSave.setOnClickListener(v -> {

            String name = edtUserName.getText().toString();
            String phone = edtUserPhone.getText().toString();
            String address = edtUserAddress.getText().toString();



            if(!name.isEmpty() && isValid(name)){

                if (!phone.isEmpty() && isvalidaPhone(phone)){

                    if(!address.isEmpty() && isValid(address)){

                        saveData(name,phone,address);
                        finish();

                    Toast.makeText(UserConfigActivity.this,
                    getString(R.string.strSuccessValid),Toast.LENGTH_LONG).show();

                    }else{
                        edtUserAddress.setError("Digite um Endereço Válido!");
                    }



                }else{
                    edtUserPhone.setError("Digite um Telefone Válido!");
                }


            }else{
                edtUserName.setError("Digite um Nome Válido!");
            }


        });



    }

    private void saveData(String name, String phone, String address) {
        User user = new User();
        user.setUserid(userID);
        user.setName(name);
        user.setPhone(phone);
        user.setAddress(address);
        user.setProfileimageurl(urlimg);
        user.salvar();
    }

    private boolean isvalidaPhone(String phone) {
        if(phone.startsWith("8") && phone.length() == 9 ){
            return true;
        }else{
            return false;
        }

    }

    private boolean isValid(String parr){
       return parr.length() > 2 ? true : false;
    }

    private void intComponents() {

        edtUserName = findViewById(R.id.textUserName);
        edtUserPhone = findViewById(R.id.textUserPhone);
        edtUserAddress = findViewById(R.id.textUserAddress);

        imgUserProfile = findViewById(R.id.user_profile);

        btnSave = findViewById(R.id.btnSaveUserConfig);
    }

    private void retriveUserData() {

        DatabaseReference userRef = dbref
                .child("user")
                .child(userID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    User user = snapshot.getValue(User.class);


                    urlimg = user.getProfileimageurl();
                    if( urlimg != ""){
                        Picasso.get()
                                .load(urlimg)
                                .into(imgUserProfile);
                    }

                    edtUserName.setText(user.getName());
                    edtUserPhone.setText(user.getPhone());
                    edtUserAddress.setText(user.getAddress());
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

                    imgUserProfile.setImageBitmap(image);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 70,baos);
                    byte[] dadosImage = baos.toByteArray();

                    final StorageReference imgRef = storageReference
                            .child("images")
                            .child("users")
                            .child(userID + "png");

                    UploadTask uploadTask = imgRef.putBytes(dadosImage);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserConfigActivity.this,getString(R.string.strUploadImageError) +"> "+e.getMessage() ,Toast.LENGTH_LONG).show();
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
                            Toast.makeText(UserConfigActivity.this,getString(R.string.strUploadSuccess),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}