package com.kevinberg.almacenpago;


import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLData;


public class ProductoDetallesActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 200;
    public static final String EXTRA_PRODUCTO_ID = "productoId";
    private final String NOT_LOGGED_IN = "FALLO";
    private SharedPreferences sharedPreferences;
    private String nombreProducto = "", descripcionProducto, precioProducto, imagenProducto, emailVendedor;
    private EditText etCantProd, etStockToAdd;
    private Integer idProducto, stock, aLaVenta =0, cantCompra = -1;


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
        Button buyButton = (Button) findViewById(R.id.bt_buy);
        Button deleteProductButton = (Button) findViewById(R.id.bt_delete_item);
        Button favButton = (Button) findViewById(R.id.bt_fav);
        Button addStockButton = (Button) findViewById(R.id.bt_add_stock);

        //Nombre de dueno en textview
        TextView nombreDueno = (TextView) findViewById(R.id.tv_dueno_producto);
        nombreDueno.setText("");

        idProducto = (Integer) getIntent().getExtras().get(EXTRA_PRODUCTO_ID);
        SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);


        try {
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("PRODUCTO", new String[]{"_idProducto, nombreProd", "descripcion", "precio", "image_resource_id, emailVendedor, stock, aLaVenta"}, "_idProducto = ?", new String[]{Integer.toString(idProducto)}, null, null, null);


            //Si existe el producto obtengo todoo
            if (cursor.moveToFirst()) {
                idProducto = cursor.getInt(0);
                nombreProducto = cursor.getString(1);
                descripcionProducto = cursor.getString(2);
                precioProducto = cursor.getString(3);
                imagenProducto = cursor.getString(4);
                emailVendedor = cursor.getString(5);
                stock = cursor.getInt(6);
                aLaVenta = cursor.getInt(7);

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

                etCantProd = (EditText) findViewById(R.id.etStockDisponible);
                etCantProd.setHint("" + stock);

                TextView tvSellerArea = findViewById(R.id.tv_area_seller);
                etStockToAdd = findViewById(R.id.etAgregarStock);
                etStockToAdd.setHint(getString(R.string.add_stock));

                nombreDueno.setText(emailVendedor);


                /*No muestro el area del vendedor si el usuario (no esta logeado o no publico este producto) y
                 * No puedo usar una LinearLayout para agrupar esto. Hay una cosa
                 * llamada GROUPS, pero no me funciono bien y se me rompio el layout
                 * Tengo que probar hacer una ContraintLayout dentro de esta
                 */
                if (!emailVendedor.equals(userEmail)) {
                    deleteProductButton.setVisibility(View.GONE);
                    addStockButton.setVisibility(View.GONE);
                    tvSellerArea.setVisibility(View.GONE);
                    etStockToAdd.setVisibility(View.GONE);
                }

                // Si el producto no tiene mas stock o se cancelo la venta se quitan algunos botones
                if(aLaVenta == 0 || !isLoggedIn){
                    //buyButton.setVisibility(View.GONE);
                    buyButton.setAlpha(.5f);
                    buyButton.setEnabled(false);
                    etCantProd.setVisibility(View.GONE);
                }
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
        }

        //Si ya entro una vez al loop entonces le quito el boton de quien es el dueno para evitar esos loops
        if(sharedPreferences.getString(MainActivity.SHAREDPREFS_LOOP, "FALSE").equalsIgnoreCase("TRUE")){
            nombreDueno.setVisibility(View.GONE);
        }

        String finalNombreProducto = nombreProducto;

        //Agrego funcion al Delete
        if(aLaVenta == 0){
            //Si ya lo habia sacado de la venta u otro, se le cambia la funcion al boton
            deleteProductButton.setText(getString(R.string.resell));
        }
            deleteProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int auxALaVentaNuevo = 0;   //Tiene que ser lo opuesto a aLaVenta. No es boolean
                    Activity act = (Activity) view.getContext();
                    if(aLaVenta == 0){

                        auxALaVentaNuevo = 1;
                    }

                    SQLiteOpenHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(act);
                    try {
                        SQLiteDatabase db = almacenPagoDatabaseHelper.getWritableDatabase();

                        //db.delete("PRODUCTO", "_ID="+idProducto,null);    //Esto eliminaba fisicamente

                        ContentValues productoValues = new ContentValues();
                        productoValues.put("aLaVenta", auxALaVentaNuevo);
                        db.update("PRODUCTO", productoValues, "_idProducto = ?", new String[]{Integer.toString(idProducto)});
                        db.close();
                        act.finish();
                    } catch (SQLiteException e) {
                        Toast.makeText(ProductoDetallesActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        //Funciones del boton comprar
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCantProd.getText().toString().equals("")) {
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.input_value), Toast.LENGTH_SHORT).show();
                } else {
                    cantCompra = Integer.valueOf(etCantProd.getText().toString());
                    if (!userEmail.equals(NOT_LOGGED_IN) && cantCompra <= stock) {

                        Activity act = (Activity) view.getContext();
                        Intent intent = new Intent(act, ComprarProductoActivity.class);
                        intent.putExtra(ComprarProductoActivity.EXTRA_PRODUCT_NAME, finalNombreProducto);
                        intent.putExtra(ComprarProductoActivity.EXTRA_ID_PRODUCTO, idProducto);
                        intent.putExtra(ComprarProductoActivity.EXTRA_EMAIL_STRING, userEmail);
                        intent.putExtra(ComprarProductoActivity.EXTRA_CANT_PRODUCTO, cantCompra);

                        startActivityForResult(intent, REQUEST_CODE);

                    } else {
                        if(cantCompra > stock){
                            Toast.makeText(ProductoDetallesActivity.this, getString(R.string.no_stock), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ProductoDetallesActivity.this, getString(R.string.must_be_logged_in), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //Funcion del boton Favorito
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userEmail.equals(NOT_LOGGED_IN)) {
                    Activity act = (Activity) view.getContext();
                    AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(act);
                    try {
                        SQLiteDatabase db = almacenPagoDatabaseHelper.getWritableDatabase();
                        almacenPagoDatabaseHelper.insertFavorito(db, idProducto, userEmail); //Tecnicamente podria tener problemas por meter un productovacio, pero como llego a eso?
                        db.close();
                    } catch (SQLiteException e) {
                        Toast.makeText(ProductoDetallesActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.added_fav), Toast.LENGTH_SHORT).show();
                } else {
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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MainActivity.SHAREDPREFS_LOOP, "TRUE");
                editor.commit();

                startActivity(intent);
            }
        });

        addStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etStockToAdd.getText().toString().equals("")) {
                    Toast.makeText(ProductoDetallesActivity.this, getString(R.string.input_value), Toast.LENGTH_SHORT).show();
                } else {
                    Integer stockAdd = Integer.valueOf(etStockToAdd.getText().toString());
                    if (!userEmail.equals(NOT_LOGGED_IN) && stockAdd > 0) {
                        AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(view.getContext());
                        SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();
                        try {
                            Log.d(TAG, "onClick: "+ idProducto);
                            stockAdd += stock;
                            ContentValues updateCv = new ContentValues();
                            updateCv.put("stock", stockAdd);
                            updateCv.put("aLaVenta", 1);
                            db.update("PRODUCTO", updateCv, "_idProducto = ?", new String[]{idProducto.toString()});
                            db.close();
                            recreate(); //Recreo la actividad para poder actualizar el valor de Stock mostrado
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                            Toast.makeText(ProductoDetallesActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                        } finally {
                            db.close();
                        }

                    } else {
                        Toast.makeText(ProductoDetallesActivity.this, getString(R.string.must_be_logged_in), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //Estoy al tanto de que devuevle ComprarProducto para ver si le resto el stock o no
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);
                SQLiteDatabase db = almacenPagoDatabaseHelper.getWritableDatabase();
                ContentValues updateCv = new ContentValues();
                updateCv.put("stock", stock - cantCompra);
                if(stock-cantCompra == 0){
                    //si no queda stock lo saco de la venta
                    updateCv.put("aLaVenta", 0);
                }
                db.update("PRODUCTO", updateCv, "_idProducto=?", new String[]{idProducto.toString()});
                db.close();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //le aviso que puede volver a ver los nombres
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.SHAREDPREFS_LOOP, "FALSE");
        editor.commit();
    }
}