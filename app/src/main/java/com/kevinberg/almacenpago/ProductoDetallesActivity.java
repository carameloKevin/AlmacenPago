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
    private final String NOT_LOGGED_IN = "FALLO";
    private SharedPreferences sharedPreferences;
    private String nombreProducto ="", descripcionProducto, precioProducto , imagenProducto, emailVendedor ;
    private Integer idProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalles);

        //Datos de si el usuario esta logueado o no
        sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.SHAREDPREFS_DATOS_USUARIO, 0);
        String userEmail = sharedPreferences.getString(MainActivity.SHAREDPREFS_EMAIL_USUARIO, NOT_LOGGED_IN);
        boolean isLoggedIn = sharedPreferences.getBoolean("LOGIN", false);

        //Muestro la toolbar y un boton para volver para arriba
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //A la toolbar que recien cree le agrego el boton para volver para arriba
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Busco los botones y les agrego sus funcionalidades
        Button buyButton = (Button)  findViewById(R.id.bt_buy);
        Button deleteProductButton = (Button) findViewById(R.id.bt_delete_item);
        Button favButton = (Button) findViewById(R.id.bt_fav);

        int productoId = (Integer) getIntent().getExtras().get(EXTRA_PRODUCTO_ID);
        SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);

        try{
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("PRODUCTO", new String[] {"_ID, NOMBREPROD","DESCRIPCION","PRECIO","IMAGE_RESOURCE_ID, EMAIL"}, "_id = ?", new String[] {Integer.toString(productoId)},null,null,null);

            //Si existe el producto obtengo todo
            if(cursor.moveToFirst()){
                idProducto = cursor.getInt(0);
                nombreProducto = cursor.getString(1);
                descripcionProducto = cursor.getString(2);
                precioProducto = cursor.getString(3);
                imagenProducto = cursor.getString(4);
                emailVendedor = cursor.getString(5);

                //Le asigno el valor a cada elemento
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
                if(!emailVendedor.equals(userEmail)) {
                    deleteProductButton.setVisibility(View.GONE);
                }
            }
            cursor.close();
            db.close();
        }catch (SQLiteException e){
            Toast.makeText(this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
        }

        //Nombre de dueno en textview
        TextView nombreDueno = (TextView) findViewById(R.id.tv_dueno_producto);
        nombreDueno.setText(emailVendedor);

        String finalNombreProducto = nombreProducto;

        //Agrego funcion al Delete
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity act = (Activity) view.getContext();
                SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(act);
                try{
                    SQLiteDatabase db = almacenPagoDatabaseHelper.getWritableDatabase();
                    db.delete("PRODUCTO", "_ID="+idProducto,null);
                    db.close();
                    act.finish();
                }catch (SQLiteException e) {
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Funciones del boton comprar
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userEmail.equals(NOT_LOGGED_IN) ){
                    Activity act = (Activity) view.getContext();
                    Intent intent = new Intent(act, ComprarProductoActivity.class);
                    intent.putExtra(ComprarProductoActivity.EXTRA_PRODUCT_NAME, finalNombreProducto);
                    intent.putExtra(ComprarProductoActivity.EXTRA_ID_PRODUCTO, productoId);
                    intent.putExtra(ComprarProductoActivity.EXTRA_EMAIL_STRING, userEmail);

                    startActivity(intent);
                    act.finish();

                }else{
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.must_be_logged_in), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Funcion del boton Favorito
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userEmail.equals(NOT_LOGGED_IN) ){
                    Activity act = (Activity) view.getContext();
                    AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(act);
                    try{
                        SQLiteDatabase db = almacenPagoDatabaseHelper.getWritableDatabase();
                        almacenPagoDatabaseHelper.insertFavorito(db,idProducto ,userEmail); //Tecnicamente podria tener problemas por meter un productovacio, pero como llego a eso?
                        db.close();
                    }catch (SQLiteException e){
                        Toast.makeText(ProductoDetallesActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.added_fav), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.must_be_logged_in), Toast.LENGTH_SHORT).show();
                }
            }
        });

        nombreDueno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), UsuarioDetallesActivity.class);
                    TextView v = (TextView) view;
                    intent.putExtra(UsuarioDetallesActivity.EMAIL_USUARIO, v.getText());
                    startActivity(intent);
            }
        });

    }
}