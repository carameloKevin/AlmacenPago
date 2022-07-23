package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class FavoritosActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String NOT_LOGGED_IN = "FALLO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        //toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fav);
        setSupportActionBar(toolbar);


        //Para tener los datos del usuario logueado
        sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.SHAREDPREFS_DATOS_USUARIO, 0);
        String userEmail = sharedPreferences.getString(MainActivity.SHAREDPREFS_EMAIL_USUARIO, NOT_LOGGED_IN);
        //idem a cargarFragmentProducto de MainActivity pero adaptado

        //Obtengo una query y lo paso a los arreglos necesarios para el fragmento;
        SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(this);
        String[] tituloProducto = new String[0];
        String[] imagenIds = new String[0];
        int[] idProducto = new int[0];
        double[] precioProducto = new double[0];
        try {
            SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();
            //obtengo la lista de los favoritos del usuario logueado
            Cursor cursor = db.rawQuery("SELECT idProducto, nombreProd, precio, image_resource_id  " +
                    "FROM PRODUCTO, FAVORITO  " +
                    "WHERE FAVORITO.emailUsuario=? AND PRODUCTO._idProducto=FAVORITO.idProducto" ,
                    new String[] {userEmail});
            Log.d(TAG, "onCreate: Llegue "+ cursor.getCount());
            if (cursor.moveToFirst()) {
                Log.d(TAG, "FavoritosActivity - onCreate: Hay un elemento en el cursor.");

                int largoCursor = cursor.getCount();
                tituloProducto = new String[largoCursor];
                imagenIds = new String[largoCursor];
                idProducto = new int[largoCursor];
                precioProducto = new double[largoCursor];

                int pos = 0;
                do {
                    idProducto[pos] = cursor.getInt(0);
                    tituloProducto[pos] = cursor.getString(1);
                    imagenIds[pos] = cursor.getString(3);
                    precioProducto[pos] = Double.parseDouble(cursor.getString(2));

                    pos++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
        }

        //Agrego un ProductoFragment al frame layout. Cuando creo el fragment le envio tambien el bundle con los datos
        Fragment productosFragment = new ProductoFragment();

        //bundle con los datos con los que trabajar
        Bundle bundle = new Bundle();
        bundle.putDoubleArray(ProductoFragment.EXTRA_ARRAY_PRECIOS, precioProducto);
        bundle.putIntArray(ProductoFragment.EXTRA_ARRAY_IDS, idProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_TITULOS, tituloProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_IMAGENID, imagenIds);
        productosFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_favoritos_usuario, productosFragment);  //Uso replace en vez de add para que no se vean las imagenes repetidas en el fondo
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }



}