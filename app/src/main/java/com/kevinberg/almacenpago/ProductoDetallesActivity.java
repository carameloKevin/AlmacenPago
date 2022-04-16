package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;

public class ProductoDetallesActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCTO_ID = "productoId";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalles);

        //Muestro la toolbar y un boton para volver para arriba
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Busco el boton de comprar y le agrego funcionalidad
        Button buyButton = (Button)  findViewById(R.id.bt_buy);


        int productoId = (Integer) getIntent().getExtras().get(EXTRA_PRODUCTO_ID);
        SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);
        String nombreProducto = "", descripcionProducto= "", precioProducto = "", imagenProducto = "";
        try{
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("PRODUCTO", new String[] {"NOMBREPROD","DESCRIPCION","PRECIO","IMAGE_RESOURCE_ID"}, "_id = ?", new String[] {Integer.toString(productoId)},null,null,null);
            Log.d(TAG, "onCreate: Estoy por entrar al if");
            if(cursor.moveToFirst()){
                 nombreProducto = cursor.getString(0);
                 descripcionProducto = cursor.getString(1);
                 precioProducto = cursor.getString(2);
                 imagenProducto = cursor.getString(3);

                Log.d(TAG, "onCreate: Los datos que entoncre fueron " + nombreProducto);
                //todo descripcion y precio en variables para mostrar

                TextView tvTitulo = (TextView) findViewById(R.id.producto_titulo);
                tvTitulo.setText(nombreProducto);

                TextView tvDescripcion = (TextView) findViewById(R.id.producto_descripcion);
                tvDescripcion.setText(descripcionProducto);

                TextView tvPrecio = (TextView) findViewById(R.id.producto_precio);
                tvPrecio.setText("$" + precioProducto);

                ImageView imageView = (ImageView) findViewById(R.id.producto_imagen);
                //Glide.with(this).load(new File(Uri.parse(imagenProducto).getPath())).into(imageView);
                //imageView.setImageURI(Uri.parse(imagenProducto));
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.iphone));
                imageView.setContentDescription(nombreProducto);
            }
        }catch (SQLiteException e){
            Toast.makeText(this, "La DB no esta funcionando", Toast.LENGTH_SHORT).show();
        }

        String finalNombreProducto = nombreProducto;

        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
        String userEmail = sharedPreferences.getString("email", "FALLO");

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userEmail != "FALLO"){
                    Intent intent = new Intent(view.getContext(), ComprarProductoActivity.class);
                    intent.putExtra(ComprarProductoActivity.EXTRA_PRODUCT_NAME, finalNombreProducto);
                    intent.putExtra(ComprarProductoActivity.EXTRA_ID_PRODUCTO, productoId);
                    intent.putExtra(ComprarProductoActivity.EXTRA_EMAIL_STRING, userEmail);

                    startActivity(intent);
                }else{
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.must_be_logged_in), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}