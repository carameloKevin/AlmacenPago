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

import org.w3c.dom.Text;

public class ElementosCompradosActivity extends ListActivity {

    private SharedPreferences sharedPreferences;
    private String[] titulosProducto = new String[0];;
    private double[] precioProducto = new double[0];
    Integer[] imagenProducto = new Integer[0]; // <-- Al dope hasta que consiga hacer andar las imagenes
    int[] idProducto = new int[0];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elementos_comprados);

        /* Esto no puede ser porque use un ListActivity. Tengo que cambiar la herencia;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSuppportActionBar(toolbar);
        */
        TextView textView = new TextView(this);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText("Items Comprados");

        //Email de usuario logeado
        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
        String userEmail = sharedPreferences.getString("email", "wrongEmail");

        //Acceso a Base de datos
        AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);

        try {
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();

            //Primero obtengo todos los ids productos que compro
            //Cursor cursor = db.rawQuery("SELECT IDPRODUCTO FROM USUARIO INNER JOIN COMPRA ON EMAILUSUARIO=EMAIL WHERE EMAIL=?" , new String[] {userEmail});
            Cursor cursor = db.rawQuery("SELECT PRODUCTO._id, NOMBREPROD, PRECIO FROM USUARIO, COMPRA, PRODUCTO  WHERE USUARIO.EMAIL=? AND USUARIO.EMAIL=COMPRA.EMAILUSUARIO AND PRODUCTO._id=COMPRA.IDPRODUCTO" , new String[] {userEmail});
            if(cursor.moveToFirst()) {
                int largoCursor = cursor.getCount();
                int pos = 0;
                idProducto = new int[largoCursor];
                titulosProducto = new String[largoCursor];
                precioProducto = new double[largoCursor];
                imagenProducto = new Integer[largoCursor];
                do{
                    //Obtengo todos los ids de los productos comprados por el usuario este
                    idProducto[pos]      = cursor.getInt(0);
                    titulosProducto[pos] = cursor.getString(1);
                    precioProducto[pos] = cursor.getDouble(2);
                    imagenProducto[pos] = R.drawable.iphone;    //todo hardcode imagenes
                }while(cursor.moveToNext());
            }
        }catch (SQLiteException e){
            Toast.makeText(this, "Error en la BD al intentar recueprar la lista de compras del usuario", Toast.LENGTH_SHORT).show();
        }

        ListView listView = (ListView) findViewById(android.R.id.list);
        CompradosListAdapater adapter = new CompradosListAdapater(this,idProducto, titulosProducto, precioProducto, imagenProducto);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, ProductoDetallesActivity.class);
        intent.putExtra(ProductoDetallesActivity.EXTRA_PRODUCTO_ID, idProducto[position]);
        startActivity(intent);
    }
}