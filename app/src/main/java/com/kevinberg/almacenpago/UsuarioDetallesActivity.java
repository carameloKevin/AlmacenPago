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
import android.widget.EditText;
import android.widget.Toast;

public class UsuarioDetallesActivity extends AppCompatActivity {

    public static final String EMAIL_USUARIO = "emailUsuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_detalles);

        String nombreUsuario   = "FALLO";
        String apellidoUsuario = "FALLO";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UsuarioDetallesFragment usuarioDetallesFragment = new UsuarioDetallesFragment();    //Se pasa por un bundle los datos en el sql de abajo
        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            String emailUsuario = extras.getString(EMAIL_USUARIO);

            //Obtener todos los datos del Usuario
            AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);
            try {
                SQLiteDatabase db = almacenPagoDatabaseHelper.getReadableDatabase();

                Cursor cursor = db.query("USUARIO", new String[]{"email, nombre", "apellido"}, "email = ?", new String[] {emailUsuario}, null, null, null, null);
                Log.d(TAG, "onCreate: USUARIODETALLES " + emailUsuario + " Cursos "+ cursor.getCount());
                if (cursor.moveToFirst()) {
                    Log.d(TAG, "UsuarioDetallActiviy - onCreate: Hay un elemento en el cursor. Leyendolo");
                        nombreUsuario = cursor.getString(1);
                        apellidoUsuario = cursor.getString(2);

                        Bundle bundle = new Bundle();
                        bundle.putString(UsuarioDetallesFragment.NOMBRE_USUARIO, nombreUsuario);
                        bundle.putString(UsuarioDetallesFragment.APELLIDO_USUARIO, apellidoUsuario);
                        bundle.putString(UsuarioDetallesFragment.EMAIL_USUARIO, emailUsuario);

                        usuarioDetallesFragment.setArguments(bundle);
                }
                cursor.close();
                db.close();
            } catch (SQLiteException e) {
                Toast.makeText(this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.frame_detalles_usuario, usuarioDetallesFragment);
            ft.commit();

            cargarFragmentoProductosUsuario(emailUsuario);


        }
    }

    private void cargarFragmentoProductosUsuario(String emailUsuario){
        //Obtengo una query y lo paso a los arreglos necesarios para el fragmento;
        SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(this);
        String[] tituloProducto = new String[0];
        //double[] precio;
        String[] imagenIds = new String[0];
        int[] idProducto = new int[0];
        double[] precioProducto = new double[0];
        try {
            SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();

            Cursor cursor = db.query("PRODUCTO", new String[]{"_idProducto, nombreProd", "image_resource_id", "precio"}, "emailVendedor = ?", new String[]{emailUsuario}, null, null, "_idProducto DESC", "10");

            if (cursor.moveToFirst()) {
                Log.d(TAG, "MainActivity - onCreate: Hay un elemento en el cursor. Leyendolo");

                int largoCursor = cursor.getCount();
                tituloProducto = new String[largoCursor];
                imagenIds = new String[largoCursor];
                idProducto = new int[largoCursor];
                precioProducto = new double[largoCursor];


                int pos = 0;
                do {
                    idProducto[pos] = cursor.getInt(0);
                    tituloProducto[pos] = cursor.getString(1);
                    imagenIds[pos] = cursor.getString(2);
                    precioProducto[pos] = Double.parseDouble(cursor.getString(3));

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
        ft.replace(R.id.frame_productos_publicados, productosFragment);  //Uso replace en vez de add para que no se vean las imagenes repetidas en el fondo
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

}