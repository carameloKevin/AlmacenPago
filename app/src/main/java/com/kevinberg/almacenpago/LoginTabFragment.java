package com.kevinberg.almacenpago;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
        void setLoginStatus();
    }

    private LoginListener listener;
    EditText etEmail, etPassword;
    Button loginButton;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);

        etEmail = view.findViewById(R.id.etEmailAddressLogin);
        etPassword = view.findViewById(R.id.etPasswordLogin);
        loginButton = view.findViewById(R.id.button_login);
        sharedPreferences = this.getContext().getApplicationContext().getSharedPreferences(MainActivity.SHAREDPREFS_DATOS_USUARIO, 0);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if(!email.equals("") && !password.equals("")){

                    SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(getContext());
                    try {
                        SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();
                        Cursor cursor = db.query("USUARIO", new String[]{"email", "password"}, "email=? AND password=?",new String[]{email, password},null,null,null);
                        if(cursor.moveToFirst()){
                            //Email y nombre en los datos compartidos. Tambien le digo al padre que setee el estado de login
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(MainActivity.SHAREDPREFS_EMAIL_USUARIO, email);
                            editor.putString(MainActivity.SHAREDPREFS_NOMBRE_USUARIO, cursor.getString(2));
                            editor.commit();
                            listener.setLoginStatus();
                        }
                    }catch (SQLiteException e){
                        Toast.makeText(getContext(), getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), getString(R.string.error_datos), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (LoginListener) context;
    }
}