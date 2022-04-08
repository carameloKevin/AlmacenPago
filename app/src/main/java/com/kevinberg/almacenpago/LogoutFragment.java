package com.kevinberg.almacenpago;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class LogoutFragment extends Fragment {

    public final String USER_NAME = "username";

    public interface LogoutListener {
        void setLogoutStatus();
    }

    private LogoutListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Un fragment que muestra quien esta logueado y un botton de logout

        String nombreUsuario = "Kevin";//savedInstanceState.getString(USER_NAME);
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        TextView isLoggedIn = view.findViewById(R.id.tv_isLoggedIn);
        Button btLogout = view.findViewById(R.id.bt_logout);

        isLoggedIn.setText(nombreUsuario + " " +getResources().getString(R.string.is_logged_in));
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setLogoutStatus();;
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (LogoutListener) context;
    }
}