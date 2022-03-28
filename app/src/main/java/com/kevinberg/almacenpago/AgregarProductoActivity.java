package com.kevinberg.almacenpago;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AgregarProductoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EditText etNombreProducto, etDescripcionProducto, etPrecioProducto, etUsuario, etImagen;
        Button btInput;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        //todo agregar imagen
        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcionProducto = findViewById(R.id.etDescripcionProducto);
        etPrecioProducto = findViewById(R.id.etPrecio);
        etUsuario = findViewById(R.id.etUsuario);
        btInput = findViewById(R.id.btLoad);

        btInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre, descripcion, email;
                double precio;
                nombre = etNombreProducto.getText().toString();
                descripcion = etDescripcionProducto.getText().toString();
                email = etUsuario.getText().toString();
                precio = Integer.getInteger(etPrecioProducto.getText().toString());

                if(!nombre.isEmpty() && !descripcion.isEmpty() && !email.isEmpty() && precio !=0){
                    AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(AgregarProductoActivity.this);
                    try{
                        //verifico que no haya un producto con el mismo nombre y el mismo usuario. No es de los mejores chequeos, pero bueno
                        SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
                        //todo medio al dope hice el ExisteUsuario, quizas lo borre
                        if(AlmacenPagoDatabaseHelper.existeUsuario(db, email)){
                            Cursor cursor = db.query("PRODUCTO", new String[]{"_ID", "NOMBREPROD", "EMAIL"}, "NOMBREPROD=? AND EMAIL=?",new String[]{nombre, email},null,null,null);
                            if(cursor.moveToFirst()){
                                Toast.makeText(AgregarProductoActivity.this, "Ya existe el producto creado por este usuario!", Toast.LENGTH_SHORT).show();
                            }else{
                                almacenPagoDatabaseHelper.insertProducto(db,nombre, descripcion, R.drawable.iphone, precio, email);
                                Toast.makeText(AgregarProductoActivity.this, "Producto Agregado con exito", Toast.LENGTH_SHORT).show();
                            }
                            cursor.close();
                            db.close();
                        }
                    }catch (SQLiteException e){
                        Toast.makeText(AgregarProductoActivity.this, "Hubo un problema con la BD", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AgregarProductoActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}