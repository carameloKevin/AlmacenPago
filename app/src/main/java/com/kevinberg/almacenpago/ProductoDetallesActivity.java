package com.kevinberg.almacenpago;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
        /*
        Aca tiene que ir un cursor obteniendo los datos de una base de datos
        todo base de datos
         */
        //---todo Hardcode---
        String nombreProducto = "Celular";
        int imagenProducto = R.drawable.iphoine;
        //---todo Hardcode---
        TextView textView = (TextView) findViewById(R.id.producto_texto);
        textView.setText(nombreProducto);

        ImageView imageView = (ImageView) findViewById(R.id.producto_imagen);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, imagenProducto));
        imageView.setContentDescription(nombreProducto);
    }
}