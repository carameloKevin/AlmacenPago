package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EditText etEmail, etPassword, etPasswordVerification, etNombre, etApellido;
        Button loginButton;

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        etEmail = view.findViewById(R.id.etEmailAddressSignUp);
        etNombre = view.findViewById(R.id.etUserName);
        etApellido = view.findViewById(R.id.etUserSurname);
        etPassword = view.findViewById(R.id.etPasswordSignUp);
        etPasswordVerification = view.findViewById(R.id.etPassword2SignUp);
        loginButton = view.findViewById(R.id.button_sign_up);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, passwordVerification, nombre, apellido;
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                passwordVerification = etPasswordVerification.getText().toString().trim();
                nombre = etNombre.getText().toString().trim();
                apellido = etApellido.getText().toString().trim();

                Log.d(TAG, "onClick: "+ email + " " + password + " " + passwordVerification);
                if(!email.isEmpty() && !password.isEmpty() && passwordVerification.equals(password) && !nombre.isEmpty() && !apellido.isEmpty()) {
                    AlmacenPagoDatabaseHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(getContext());
                    SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();
                    try {
                        Cursor cursor = db.query("USUARIO", new String[]{"_ID", "EMAIL"}, "EMAIL=?", new String[]{email}, null, null, null);
                        if (cursor.moveToFirst()) {
                            //Si encuentro un usuario en la lista significa que ya existe, dah
                            Toast.makeText(getContext(), getString(R.string.user_alredy_exists), Toast.LENGTH_SHORT).show();
                        } else {
                            db.close();
                            db = almacenPagoDBHelper.getWritableDatabase();
                            almacenPagoDBHelper.insertUsuario(db, email, password, nombre, apellido);
                            Toast.makeText(getContext(), getString(R.string.user_created_success), Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLiteException e) {
                        Toast.makeText(getContext(), getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    } finally {
                        db.close();
                    }
                }else{
                    Toast.makeText(getContext(), getString(R.string.verify_input), Toast.LENGTH_SHORT).show();
            }
        }});

        return view;
    }
}