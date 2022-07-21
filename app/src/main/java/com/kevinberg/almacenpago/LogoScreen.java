package com.kevinberg.almacenpago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class LogoScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Espera unos segundos y pasa a la mainactivity, absolutamente nada mas
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_screen);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);


    }
}