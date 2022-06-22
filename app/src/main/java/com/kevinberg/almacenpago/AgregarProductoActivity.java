package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;


public class AgregarProductoActivity extends AppCompatActivity {

    private ActivityResultLauncher<String[]> seleccionarImagen;
    private File imageFile = null;
    private Uri imageUri;
    private String fullPathUri = "";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EditText etNombreProducto, etDescripcionProducto, etPrecioProducto;
        ImageView ivImagen;
        Button btInput, btInputImage;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcionProducto = findViewById(R.id.etDescripcionProducto);
        etPrecioProducto = findViewById(R.id.etPrecio);
        ivImagen = findViewById(R.id.iv_imagen_producto);
        btInput = findViewById(R.id.btLoad);
        btInputImage = findViewById(R.id.button_add_image);

        //Esto genera la actividad que abre una nueva pantalla donde se selecciona la imagen
        seleccionarImagen = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
         new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    ivImagen.setImageURI(result);
                    Log.d(TAG, "el path de la imagen es: " + result.getPath());
                    imageUri = result;
                }
            });


        btInputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen.launch(new String[] {"image/*"});
            }
        });


        btInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre, descripcion, email;
                String uri = "";
                double precio;

                //Para obtener el email
                sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);

                //Obtengo los datos de los EditText que ingreso el usuario
                nombre = etNombreProducto.getText().toString();
                descripcion = etDescripcionProducto.getText().toString();
                email = sharedPreferences.getString("email", "wrongEmail"); //No deberia haber podido llegar hasta aca si no esta logueado
                precio = Double.parseDouble(etPrecioProducto.getText().toString());

                if(imageUri != null){
                    //Esto se tiene que cargar en la registerForActivityResult, aca solo lo formateo para guardar en BD
                    uri = imageUri.toString();
                }

                if(!nombre.isEmpty() && !descripcion.isEmpty() && precio >0 && !uri.isEmpty()){
                    //Creo un nuevo helper para que me ayude a cargar y obtener los datos
                    AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(AgregarProductoActivity.this);
                    try{
                        //verifico que no haya un producto con el mismo nombre y el mismo usuario. No es de los mejores chequeos, pero sirve para la pequena escala que manejo
                        SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
                            Cursor cursor = db.query("PRODUCTO", new String[]{"_ID", "NOMBREPROD", "EMAIL"}, "NOMBREPROD=? AND EMAIL=?",new String[]{nombre, email},null,null,null);
                            if(cursor.moveToFirst()){
                                Toast.makeText(AgregarProductoActivity.this, getString(R.string.ya_existe_producto), Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(TAG, "AgregarProductoActivity onClick: Intentando insertar producto");
                                db.close();
                                db = almacenPagoDatabaseHelper.getWritableDatabase();
                                almacenPagoDatabaseHelper.insertProducto(db,nombre, descripcion, uri, precio, email);
                                Toast.makeText(AgregarProductoActivity.this, getString(R.string.producto_agregado_exito), Toast.LENGTH_SHORT).show();
                            }
                            cursor.close();
                            db.close();
                    }catch (SQLiteException e){
                        Toast.makeText(AgregarProductoActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AgregarProductoActivity.this, getString(R.string.campos_incompletos), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}