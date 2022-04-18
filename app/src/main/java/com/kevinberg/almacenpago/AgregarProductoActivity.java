package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.PathUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AgregarProductoActivity extends AppCompatActivity {

    private ActivityResultLauncher<String[]> seleccionarImagen;
    private File imageFile = null;
    private boolean existeImagen;
    private Uri imageUri;
    private String fullPathUri = "";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EditText etNombreProducto, etDescripcionProducto, etPrecioProducto, etUsuario;
        ImageView ivImagen;
        Button btInput, btInputImage;
        existeImagen = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        //todo agregar imagen
        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcionProducto = findViewById(R.id.etDescripcionProducto);
        etPrecioProducto = findViewById(R.id.etPrecio);
        ivImagen = findViewById(R.id.iv_imagen_producto);
        btInput = findViewById(R.id.btLoad);
        btInputImage = findViewById(R.id.button_add_image);



        seleccionarImagen = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
         new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    //getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    ivImagen.setImageURI(result);
                    Log.d(TAG, "el path de la imagen es: " + result.getPath());
                    imageUri = result;
                    existeImagen = true;

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

                //Para obtener el email
                sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);

                String nombre, descripcion, email;
                String uri = "";
                double precio;
                nombre = etNombreProducto.getText().toString();
                descripcion = etDescripcionProducto.getText().toString();
                email = sharedPreferences.getString("EMAIL", "wrongEmail"); //No deberia haber podido llegar hasta aca si no esta logueado;
                precio = Double.parseDouble(etPrecioProducto.getText().toString());
                if(imageUri != null){
                    uri = imageUri.toString();
                }

                if(!nombre.isEmpty() && !descripcion.isEmpty() && precio !=0){
                    AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(AgregarProductoActivity.this);
                    try{
                        //verifico que no haya un producto con el mismo nombre y el mismo usuario. No es de los mejores chequeos, pero bueno
                        SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
                            Cursor cursor = db.query("PRODUCTO", new String[]{"_ID", "NOMBREPROD", "EMAIL"}, "NOMBREPROD=? AND EMAIL=?",new String[]{nombre, email},null,null,null);
                            if(cursor.moveToFirst()){
                                Toast.makeText(AgregarProductoActivity.this, "Ya existe un mismo producto creado por este usuario!", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(TAG, "AgregarProductoActivity onClick: Intentando insertar producto");
                                db.close();
                                db = almacenPagoDatabaseHelper.getWritableDatabase();
                                almacenPagoDatabaseHelper.insertProducto(db,nombre, descripcion, uri, precio, email);
                                Toast.makeText(AgregarProductoActivity.this, "Producto Agregado con exito", Toast.LENGTH_SHORT).show();
                            }
                            cursor.close();
                            db.close();
                    }catch (SQLiteException e){
                        Toast.makeText(AgregarProductoActivity.this, "Hubo un problema con la BD", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AgregarProductoActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*
    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    */
}