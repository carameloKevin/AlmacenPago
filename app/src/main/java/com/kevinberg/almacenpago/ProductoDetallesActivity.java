package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductoDetallesActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCTO_ID = "productoId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalles);

        //Muestro la toolbar y un boton para volver para arriba
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int productoId = (Integer) getIntent().getExtras().get(EXTRA_PRODUCTO_ID);
        SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);
        try{
            Log.d(TAG, "onCreate: Estoy por abrir el "+ productoId);
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("PRODUCTO", new String[] {"NOMBREPROD","DESCRIPCION","PRECIO","IMAGE_RESOURCE_ID"}, "_id = ?", new String[] {Integer.toString(productoId)},null,null,null);
            Log.d(TAG, "onCreate: Estoy por entrar al if");
            if(cursor.moveToFirst()){
                Log.d(TAG, "onCreate: ENTREE");
                String nombreProducto = cursor.getString(0);
                int imagenProducto = cursor.getInt(3);

                Log.d(TAG, "onCreate: Los datos que entoncre fueron " + nombreProducto);
                //todo descripcion y precio en variables para mostrar

                TextView textView = (TextView) findViewById(R.id.producto_texto);
                textView.setText(nombreProducto);

                ImageView imageView = (ImageView) findViewById(R.id.producto_imagen);
                imageView.setImageResource(imagenProducto);
                //imageView.setImageDrawable(ContextCompat.getDrawable(this, imagenProducto));
                imageView.setContentDescription(nombreProducto);
            }
        }catch (SQLiteException e){
            Toast.makeText(this, "La DB no esta funcionando", Toast.LENGTH_SHORT).show();
        }

    }
}