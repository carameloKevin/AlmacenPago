package com.kevinberg.almacenpago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class ElementosCompradosActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String[] titulosProducto = new String[0];
    private double[] precioProducto = new double[0];
    private String[] imagenProducto = new String[0];
    private String[] fechaCompra = new String[0];
    private int[] idProducto = new int[0];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elementos_comprados);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = new TextView(this);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText(this.getString(R.string.bought_products));

        //Email de usuario logeado
        sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.SHAREDPREFS_DATOS_USUARIO, 0);
        String userEmail = sharedPreferences.getString(MainActivity.SHAREDPREFS_EMAIL_USUARIO, "wrongEmail");

        //Acceso a Base de datos
        AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);

        try {
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();

            //Primero obtengo todos los ids productos que compro
            Cursor cursor = db.rawQuery(
                    "SELECT PRODUCTO._idProducto, nombreProd, precio, image_resource_id, fecha" +
                    "  FROM USUARIO, COMPRA, PRODUCTO  " +
                    "WHERE USUARIO.email=? AND USUARIO.email=COMPRA.emailUsuario AND PRODUCTO._idProducto=COMPRA.idProducto" ,
                    new String[] {userEmail});
            if(cursor.moveToFirst()) {
                int largoCursor = cursor.getCount();
                int pos = 0;
                idProducto = new int[largoCursor];
                titulosProducto = new String[largoCursor];
                precioProducto = new double[largoCursor];
                imagenProducto = new String[largoCursor];
                fechaCompra = new String[largoCursor];

                do{
                    //Obtengo todos los ids de los productos comprados por el usuario este
                    idProducto[pos]      = cursor.getInt(0);
                    titulosProducto[pos] = cursor.getString(1);
                    precioProducto[pos] = cursor.getDouble(2);
                    imagenProducto[pos] = cursor.getString(3);



                    fechaCompra[pos] = cursor.getString(4);
                    pos++;
                }while(cursor.moveToNext());
                cursor.close();
                db.close();
            }
        }catch (SQLiteException e){
            Toast.makeText(this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
        }


        //ListView es muy parecido al RecyclerView, buscas el list view, definis el adatper, y le asignas el adater a la listView
        ListView listView = (ListView) findViewById(android.R.id.list);
        //no se puede usar ImagenSubAdapter porque es otra clase de Adapter
        CompradosListAdapater adapter = new CompradosListAdapater(this,idProducto,
                titulosProducto, precioProducto, imagenProducto, fechaCompra);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(view.getContext(), ProductoDetallesActivity.class);
                intent.putExtra(ProductoDetallesActivity.EXTRA_PRODUCTO_ID, idProducto[position]);
                startActivity(intent);
            }
        });

    }
}