package com.kevinberg.almacenpago;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ProductoDetallesActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCTO_ID = "productoId";
    SharedPreferences sharedPreferences;
    String nombreProducto ="", descripcionProducto, precioProducto , imagenProducto, emailVendedor ;
    Integer idProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalles);

        //Datos de si el usuario esta logueado o no
        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
        String userEmail = sharedPreferences.getString("email", "FALLO");
        boolean isLoggedIn = sharedPreferences.getBoolean("LOGIN", false);

        //Muestro la toolbar y un boton para volver para arriba
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Busco el boton de comprar y le agrego funcionalidad
        Button buyButton = (Button)  findViewById(R.id.bt_buy);
        Button deleteProductButton = (Button) findViewById(R.id.bt_delete_item);

        int productoId = (Integer) getIntent().getExtras().get(EXTRA_PRODUCTO_ID);
        SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);

        try{
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("PRODUCTO", new String[] {"_ID, NOMBREPROD","DESCRIPCION","PRECIO","IMAGE_RESOURCE_ID, EMAIL"}, "_id = ?", new String[] {Integer.toString(productoId)},null,null,null);

            if(cursor.moveToFirst()){

                idProducto = cursor.getInt(0);
                nombreProducto = cursor.getString(1);
                descripcionProducto = cursor.getString(2);
                precioProducto = cursor.getString(3);
                imagenProducto = cursor.getString(4);
                emailVendedor = cursor.getString(5);

                TextView tvTitulo = (TextView) findViewById(R.id.producto_titulo);
                tvTitulo.setText(nombreProducto);

                TextView tvDescripcion = (TextView) findViewById(R.id.producto_descripcion);
                tvDescripcion.setText(descripcionProducto);

                TextView tvPrecio = (TextView) findViewById(R.id.producto_precio);
                String aux = "$" + precioProducto;
                tvPrecio.setText(aux);

                ImageView imageView = (ImageView) findViewById(R.id.producto_imagen);
                imageView.setImageURI(Uri.parse(imagenProducto));

                imageView.setContentDescription(nombreProducto);

                //No muestro el Unbuy si el usuario no esta logeado o no publico este producto
                if(!isLoggedIn && !emailVendedor.equals(userEmail)) {
                    deleteProductButton.setVisibility(View.GONE);
                }
            }
            cursor.close();
            db.close();
        }catch (SQLiteException e){
            Toast.makeText(this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
        }

        String finalNombreProducto = nombreProducto;

        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity act = (Activity) view.getContext();
                SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(act);
                try{
                    SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
                    db.delete("PRODUCTO", "_ID="+idProducto,null);
                    db.close();
                    act.finish();
                }catch (SQLiteException e) {
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userEmail.equals("FALLO") ){
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