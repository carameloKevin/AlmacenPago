package com.kevinberg.almacenpago;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginTabFragment extends Fragment {

    public interface LoginListener{
        void setLoginStatus(boolean value);
    }

    private LoginListener listener;
    EditText etEmail, etPassword;
    TextView tvSuccess;
    Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);

        etEmail = view.findViewById(R.id.etEmailAddressLogin);
        etPassword = view.findViewById(R.id.etPasswordLogin);
        loginButton = view.findViewById(R.id.button_login);
        tvSuccess = view.findViewById(R.id.textViewSuccess);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if(email != null && password != null){
                    SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(getContext());

                    try {
                        SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();
                        Cursor cursor = db.query("USUARIO", new String[]{"_ID", "EMAIL", "PASSWORD"}, "EMAIL=? AND PASSWORD=?",new String[]{email, password},null,null,null);
                        if(cursor.moveToFirst()){
                            tvSuccess.setText("Exito!");
                            listener.setLoginStatus(true);
                        }else{
                            tvSuccess.setText("Fallo");
                        }
                    }catch (SQLiteException e){
                        Toast.makeText(getContext(), "Error en la BD al intentar recuperar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

}