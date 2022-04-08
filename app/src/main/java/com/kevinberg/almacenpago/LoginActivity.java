package com.kevinberg.almacenpago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private static final String TAG = "Somethin";
    private boolean isLoggedIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
        isLoggedIn = sharedPreferences.getBoolean("LOGIN", false);

        Fragment fragmetACargar = null;
        if(!isLoggedIn) {
            fragmetACargar = new LoginFragment();
        }else{
            //todo agregar bundle con el nombre del usuario
            fragmetACargar = new LogoutFragment();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragmetACargar);
        ft.commit();
    }

    @Override
    public void setLoginStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LOGIN", true);
        editor.commit();
        Toast.makeText(this, "Usuario Login exitoso", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void setLogoutStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("LOGIN");
        editor.commit();
        Toast.makeText(this, "Usuario Logout exitoso", Toast.LENGTH_SHORT).show();
        finish();
    }



}