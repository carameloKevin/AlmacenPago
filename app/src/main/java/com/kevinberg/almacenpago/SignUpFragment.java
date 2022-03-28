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
        EditText etEmail, etPassword, etPasswordVerification;
        TextView tvSuccess;
        Button loginButton;

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        etEmail = view.findViewById(R.id.etEmailAddressSignUp);
        etPassword = view.findViewById(R.id.etPasswordSignUp);
        etPasswordVerification = view.findViewById(R.id.etPassword2SignUp);
        loginButton = view.findViewById(R.id.button_sign_up);
        tvSuccess = view.findViewById(R.id.textViewSuccess);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, passwordVerification;
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                passwordVerification = etPasswordVerification.getText().toString();
                Log.d(TAG, "onClick: "+ email + " " + password + " " + passwordVerification);
                if(!email.isEmpty() && !password.isEmpty() && passwordVerification.equals(password)){
                    SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(getContext());

                    try {
                        SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();
                        Cursor cursor = db.query("USUARIO", new String[]{"_ID", "EMAIL"}, "EMAIL=?",new String[]{email},null,null,null);
                        if(cursor.moveToFirst()){
                            //Si encuentro un usuario en la lista significa que ya existe, dah
                            Toast.makeText(getContext(), "Ya existe un usuario con ese email", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(getContext(), "No existe otro usuario con ese nombre asi que todo bien!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (SQLiteException e){
                        Toast.makeText(getContext(), "Error en la BD al intentar recuperar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Alguno de los campos no esta bien, verifique los datos ingresados", Toast.LENGTH_SHORT).show();
            }
        }});

        return view;
    }
}