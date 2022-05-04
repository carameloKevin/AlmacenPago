package com.kevinberg.almacenpago;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Fragmento que contiene el viewer y una tab. Solia estar en LoginActivity, pero como tengo que mostrar otro fragmento si el usuario esta logueado, se movio toodo aca.
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        LoginFragment.SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(this.getActivity());
        ViewPager2 pager = (ViewPager2) view.findViewById(R.id.viewpager);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, pager, ((tab, position) -> tab.setText(pagerAdapter.getPageTitle(position)))).attach();
        return view;
    }

    //Clase privada para poder usar la el viewPager e ir cambiando entre los dos
    private class SectionsPagerAdapter extends FragmentStateAdapter {
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