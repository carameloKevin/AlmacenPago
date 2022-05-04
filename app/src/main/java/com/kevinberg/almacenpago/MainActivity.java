package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.deleteDatabase("almacenPago"); //Linea para borrar la BD cuando cambio la id de las imagenes
        //agrego la toolbar arriba del toodo
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //--Drawer--
        //Seleccion el drawer y le digo que ponga el simbolo en la toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_abrir_drawer, R.string.nav_cerrar_drawer);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        //Basicamente le agrego las funciones al drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cargarNombreUsuario();
        cargarFragmentoProductos();
    }

    private void cargarFragmentoProductos(){
        //Obtengo una query y lo paso a los arreglos necesarios para el fragmento;
        SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(this);
        String[] tituloProducto = new String[0];
        //double[] precio;
        String[] imagenIds = new String[0];
        int[] idProducto = new int[0];
        double[] precioProducto = new double[0];
        try {
            SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();

            Cursor cursor = db.query("PRODUCTO", new String[]{"_ID, NOMBREPROD", "IMAGE_RESOURCE_ID", "PRECIO"}, null, null, null, null, "_id DESC", "10");

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
        bundle.putIntArray(ProductoFragment.EXTRA_ARRAY_IDS, idProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_TITULOS, tituloProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_IMAGENID, imagenIds);
        productosFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_layout_main, productosFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void cargarNombreUsuario(){
        //Obtengo el header y le cambio el texto donde va a el nombre del usuario
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView textUsuario = (TextView) header.findViewById(R.id.nav_nombreUsuario);
        TextView textEmail = (TextView) header.findViewById(R.id.nav_emailUsuario);
        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);

        textUsuario.setText(sharedPreferences.getString("nombre", "Visitante"));
        textEmail.setText(sharedPreferences.getString("email", "Visitante@Visitante.com"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Obtengo el id del item que selecciono y abro el fragmento/actividad
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
        boolean isLoggedIn = sharedPreferences.getBoolean("LOGIN", false);
        boolean requiresLogin = false;
        //Cambia dependiendo de que se selecciono
        switch (id){
            case R.id.nav_ultimas_compras:
                requiresLogin = true;
                intent = new Intent(this, ElementosCompradosActivity.class);
                break;
            case R.id.nav_usuario:
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.nav_agregar_compra:
                requiresLogin = true;
                intent = new Intent(this, AgregarProductoActivity.class);
                break;
        }
        if(!requiresLogin||(requiresLogin && isLoggedIn)) {
            startActivity(intent);
        }else{
            Toast.makeText(this, getString(R.string.error_is_not_login), Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFragmentoProductos();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cargarNombreUsuario();
    }
}