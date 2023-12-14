package dev.muvi.nhonga.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import dev.muvi.nhonga.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openAuth();
            }
        },3000);
    }

    private void openAuth(){
        Intent i = new Intent(SplashActivity.this, AuthActivity.class);
        startActivity(i);
        finish();
    }
}