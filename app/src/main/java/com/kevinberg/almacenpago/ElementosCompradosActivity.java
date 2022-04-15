package com.kevinberg.almacenpago;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elementos_comprados);

        TextView textView = new TextView(this);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText("Items Comprados");



        //Email de usuario logeado
        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
        String userEmail = sharedPreferences.getString("email", "wrongEmail");

        //Acceso a Base de datos
        AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);

        //Variables auxiliares
        String[] titulosProducto = new String[0];
        double[] precioProducto = new double[0];
        Integer[] imagenProducto = new Integer[0];   // <-- Al dope hasta que consiga hacer andar las imagenes

        try {
            SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();

            //Primero obtengo todos los ids productos que compro
            //Cursor cursor = db.rawQuery("SELECT IDPRODUCTO FROM USUARIO INNER JOIN COMPRA ON EMAILUSUARIO=EMAIL WHERE EMAIL=?" , new String[] {userEmail});
            Cursor cursor = db.rawQuery("SELECT NOMBREPROD, PRECIO FROM USUARIO, COMPRA, PRODUCTO  WHERE USUARIO.EMAIL=? AND USUARIO.EMAIL=COMPRA.EMAILUSUARIO AND PRODUCTO._id=COMPRA.IDPRODUCTO" , new String[] {userEmail});
            if(cursor.moveToFirst()) {
                int largoCursor = cursor.getCount();
                int pos = 0;
                titulosProducto = new String[largoCursor];
                precioProducto = new double[largoCursor];
                imagenProducto = new Integer[largoCursor];
                do{
                    //Obtengo todos los ids de los productos comprados por el usuario este
                    titulosProducto[pos] = cursor.getString(0);
                    precioProducto[pos] = cursor.getDouble(1);
                    imagenProducto[pos] = R.drawable.iphone;    //todo hardcode imagenes
                }while(cursor.moveToNext());
            }
        }catch (SQLiteException e){
            Toast.makeText(this, "Error en la BD al intentar recueprar la lista de compras del usuario", Toast.LENGTH_SHORT).show();
        }

        ListView listView = (ListView) findViewById(android.R.id.list);
        CompradosListAdapater adapter = new CompradosListAdapater(this, titulosProducto, precioProducto, imagenProducto);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Toast.makeText(ElementosCompradosActivity.this, "Esta es la pos "+ position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}