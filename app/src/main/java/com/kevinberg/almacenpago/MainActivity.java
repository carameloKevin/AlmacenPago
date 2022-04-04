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
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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


        //Obtengo una query y lo paso a los arreglos necesarios para el fragmento;
        //String query = "SELECT NOMBREPROD, IMAGE_RESOURCE_ID, PRECIO FROM PRODUCTO ORDER BY column DESC LIMIT 6";
        //todo: yo se que el limite es 6 y es en este caso asi, asi que lo hardcodeo, pero hay que verlo
        SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(this);
        String[] tituloProducto = new String[0];
        //double[] precio;
        String[] imagenIds = new String[0];
        int[] idProducto = new int[0];
        double[] precioProducto = new double[0];
        try {
            SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();
            Cursor cursor = db.query("PRODUCTO", new String[]{"_ID, NOMBREPROD", "IMAGE_RESOURCE_ID", "PRECIO"}, null, null, null, null, "_id DESC", "4");

            if (cursor.moveToFirst()) {
                //todo yo en este caso se que son X, pero no me gusta
                //Se deja en dos (o igual que el Cursor de arriba) porque imagenSubAdapter falla si el mandas un arreglo con alguno de los elementos null
                int x = 6;
                tituloProducto = new String[x];
                imagenIds = new String[x];
                idProducto = new int[x];
                precioProducto = new double[x];
                int pos = 0;
                do {
                    idProducto[pos] = cursor.getInt(0);
                    tituloProducto[pos] = cursor.getString(1);
                    precioProducto[pos] = Double.parseDouble(cursor.getString(3));
                    imagenIds[pos] = cursor.getString(2);

                    pos++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_SHORT).show();
        }

        //Agrego un ProductoFragment al frame layout. Cuando creo el fragment le envio tambien el bundle con los datos
        Fragment productosFragment = new ProductoFragment();

        //bundle con los datos con los que trabajar
        Bundle bundle = new Bundle();
        bundle.putIntArray(ProductoFragment.EXTRA_ARRAY_IDS, idProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_TITULOS, tituloProducto);
        //bundle.putDoubleArray(.......);
        //bundle.putIntArray(ProductoFragment.EXTRA_ARRAY_IMAGENID, imagenIds);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_IMAGENID, imagenIds);
        productosFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_layout_main, productosFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Obtengo el id del item que selecciono y abro el fragmento/actividad
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        //Cambias dependiendo de que se selecciono
        switch (id){
            case R.id.nav_saldo:
                //fragment = new SaldoFragment();
                //Todo para testear. Esto tendria que estar en la tooblar una vez logueado el usuario
                intent = new Intent(this, AgregarProductoActivity.class);
                break;
            case R.id.nav_ultimas_compras:
                //fragment = new UltimasComprasFragment();
                break;
            case R.id.nav_usuario:
                intent = new Intent(this, LoginActivity.class);
                break;
        }

        //cargo el fragmento o intent
        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //ft.replace(R.id.content_frame, fragmenet)
            //ft.commit();
        }else{
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
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
}