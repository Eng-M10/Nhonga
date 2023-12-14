package dev.muvi.nhonga.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import dev.muvi.nhonga.R;
import dev.muvi.nhonga.helper.FirebaseConfig;
import dev.muvi.nhonga.helper.UserFirebase;

public class AuthActivity extends AppCompatActivity {

    private Button btnCont;
    private EditText edtEmail, edtPassword;
    private Switch switchlogin,switchentity;
    private LinearLayout usertype;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_auth);

        initComponents();
        auth = FirebaseConfig.getReferenceAuth();
        isLoggedin();
        switchlogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //Ads
                    usertype.setVisibility(View.VISIBLE);
                }else{
                    //users
                    usertype.setVisibility(View.GONE);
                }
            }
        });
        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if(validate(email, password)){

                    if(switchlogin.isChecked()){
                        //sign up
                        auth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){

                                Toast.makeText(AuthActivity.this, "Cadastro Realizado com Sucesso!",Toast.LENGTH_SHORT).show();
                                String typo = getUserType();
                                UserFirebase.updateUserType(typo);
                                openActivity(typo);
                            }else{
                                String excep = "";

                                try {
                                    throw task.getException();
                                }catch (FirebaseAuthWeakPasswordException e){
                                    excep = "Digite uma senha forte!!";
                                }catch(FirebaseAuthInvalidCredentialsException e){
                                    excep = "Por favor, digite um e-mail válido!";
                                }catch (FirebaseAuthUserCollisionException e){
                                    excep = "Conta existente";

                                } catch (Exception e) {
                                    excep = "Ao cadastrar user: "+e.getMessage();
                                }

                                Toast.makeText(AuthActivity.this,"Erro: "+ excep, Toast.LENGTH_LONG).show();
                            }
                        });

                    }else{
                        //login
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                                task -> {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AuthActivity.this,"Logado com Sucesso!", Toast.LENGTH_LONG).show();
                                        String ustypo = task.getResult().getUser().getDisplayName();
                                        openActivity(ustypo);
                                    }else {
                                        Toast.makeText(AuthActivity.this,"Falha ao fazer login. Tente novamente!", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );

                    }

                }
            }
        });
    }

    private void isLoggedin() {
        FirebaseUser current = auth.getCurrentUser();
        if(current != null){
            openActivity(String.valueOf(current.getDisplayName()));
        }
    }

    private void initComponents(){
        btnCont = findViewById(R.id.btnAuth);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        switchlogin = findViewById(R.id.switchlogin);
        switchentity = findViewById(R.id.switchentity);
        usertype = findViewById(R.id.layoutentity);
    }

    private Boolean validate(String email, String password){

        if(email.isEmpty()){
            edtEmail.setError(getString(R.string.strErrorEmail));
            return false;
        }else if(password.isEmpty()){
            edtPassword.setError(getString(R.string.strErrorPassword));
            return false;
        }

        return true;
    }
    private void openActivity(String type){
        if(type != null && type.equals("A")){
            startActivity(new Intent(getApplicationContext(), AdvertiserActivity.class ));
        }else {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    private String getUserType(){
        return switchentity.isChecked() ? "A" : "U";
    }
}