package com.kevinberg.almacenpago;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class UsuarioDetallesFragment extends Fragment {

    public static final String NOMBRE_USUARIO = "nombreUsuario";
    public static final String APELLIDO_USUARIO = "apellidoUsuario";
    public static final String EMAIL_USUARIO    = "emailUsuario";
    private static final String FALLO           = "FALLO";

    public UsuarioDetallesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_detalles, container, false);

        Bundle extras = getArguments();
        String userName = extras.getString(NOMBRE_USUARIO, FALLO);
        String userLastName = extras.getString(APELLIDO_USUARIO, FALLO);
        String userEmail = extras.getString(EMAIL_USUARIO, FALLO);

        TextView nombreUsuarioET = view.findViewById(R.id.tv_username);
        TextView emailUsuarioET = view.findViewById(R.id.tv_userEmail);

        nombreUsuarioET.setText(userName + " " + userLastName);
        emailUsuarioET.setText(userEmail);

        return view;
    }
}