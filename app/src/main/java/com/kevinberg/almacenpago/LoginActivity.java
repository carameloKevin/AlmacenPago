package com.kevinberg.almacenpago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;



public class LoginActivity extends AppCompatActivity implements LogoutFragment.LogoutListener, LoginTabFragment.LoginListener {

    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!isLoggedIn) {
            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(this);
            ViewPager2 pager = (ViewPager2) findViewById(R.id.viewpager);
            pager.setAdapter(pagerAdapter);

            TabLayout tabLayout = findViewById(R.id.tab_layout);
            new TabLayoutMediator(tabLayout, pager, ((tab, position) -> tab.setText(pagerAdapter.getPageTitle(position)))).attach();
        }else{
            setContentView(R.layout.fragment_logout);
        }
    }

    @Override
    public void updateLoginStatus(boolean value) {
        isLoggedIn = false;
        finish();
        startActivity(getIntent());
    }

    @Override
    public void setLoginStatus(boolean value) {
        isLoggedIn = true;
        finish();
        startActivity(getIntent());
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