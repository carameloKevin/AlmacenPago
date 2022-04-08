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

        if(!isLoggedIn) {
            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(this);
            ViewPager2 pager = (ViewPager2) findViewById(R.id.viewpager);
            pager.setAdapter(pagerAdapter);

            TabLayout tabLayout = findViewById(R.id.tab_layout);
            new TabLayoutMediator(tabLayout, pager, ((tab, position) -> tab.setText(pagerAdapter.getPageTitle(position)))).attach();
        }else{
            Fragment logoutFrag = new LogoutFragment();
            //todo agregar bundle con el nombre del usuario

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_logout, logoutFrag);
            ft.commit();

        }
    }

    @Override
    public void setLoginStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LOGIN", true);
        editor.commit();
        //finish();
        //startActivity(getIntent());
    }

    @Override
    public void setLogoutStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("LOGIN");
        editor.commit();
        Log.d(TAG, "setLogoutStatus: Ejecute el setLogoutStatus " + sharedPreferences.getBoolean("LOGIN", false));
    }


    //Clase privada para poder usar la el viewPager e ir cambiando entre los dos
    private class SectionsPagerAdapter extends FragmentStateAdapter{
        public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity){
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new LoginTabFragment();
                case 1:
                    return new SignUpFragment();
            }
            return null;
        }


        @Override
        public int getItemCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return getResources().getText(R.string.login);
                case 1:
                    return getResources().getText(R.string.signup);
            }
            return null;
        }
    }
}