package com.kevinberg.almacenpago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;



public class LoginActivity extends AppCompatActivity implements LogoutFragment.LogoutListener, LoginTabFragment.LoginListener {

    private boolean isLoggedIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
        isLoggedIn = sharedPreferences.getBoolean("LOGIN", false);

        //Dependiendo si esta logueado o no muestro un fragmento o otro
        Fragment fragmetACargar = null;
        if(!isLoggedIn) {
            fragmetACargar = new LoginFragment();
        }else{
            fragmetACargar = new LogoutFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragmetACargar);
        ft.commit();
    }

    @Override
    public void setLoginStatus() {
        //Esto existe para poder hacer Finish() y cerrar la actividad al terminar
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LOGIN", true);
        editor.commit();
        Toast.makeText(this, getString(R.string.login_exitoso), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void setLogoutStatus() {
        //Esto existe para poder hacer Finish() y cerrar la actividad al terminar
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("LOGIN");
        editor.commit();
        Toast.makeText(this, getString(R.string.logut_exitoso), Toast.LENGTH_SHORT).show();
        finish();
    }



}