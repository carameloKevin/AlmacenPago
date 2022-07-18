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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SHAREDPREFS_DATOS_USUARIO = "userdetails";
    public static final String SHAREDPREFS_EMAIL_USUARIO = "email";
    public static final String SHAREDPREFS_NOMBRE_USUARIO = "nombre";
    public static final String SHAREDPREFS_LOGIN    = "LOGIN";
    public static final String SHAREDPREFS_LOOP     = "TRUE";
    private boolean musicOn = false;

    private MediaPlayer player;
    private SharedPreferences sharedPreferences;
    private EditText textoBuscador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.deleteDatabase("almacenPago"); //Linea para borrar la BD cuando cambio la id de las imagenes
        //agrego la toolbar arriba del toodo
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        cargarFragmentoProductos("");
    }


    private void cargarFragmentoProductos(String tituloProd){
        //Si se pasa el tituloProd vacio se obtiene todoo
        //Obtengo una query y lo paso a los arreglos necesarios para el fragmento;
        SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(this);
        String[] tituloProducto = new String[0];
        String[] imagenIds = new String[0];
        int[] idProducto = new int[0];
        double[] precioProducto = new double[0];
        try {
            SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();

            Cursor cursor = db.query("PRODUCTO", new String[]{"_idProducto, nombreProd", "image_resource_id", "precio"}, "nombreProd LIKE ?", new String[]{"%"+ tituloProd + "%"}, null, null, "_idProducto ASC", "10");

            if (cursor.moveToFirst()) {
                Log.d(TAG, "MainActivity - onCreate: Hay elemento/s en el cursor. Leyendolo");

                int largoCursor = cursor.getCount();
                tituloProducto = new String[largoCursor];
                imagenIds = new String[largoCursor];
                idProducto = new int[largoCursor];
                precioProducto = new double[largoCursor];

                //Cargo todos los elementos para pasarlos al adapter
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

        //bundle con los datos con los que trabajar el adapter
        Bundle bundle = new Bundle();
        bundle.putDoubleArray(ProductoFragment.EXTRA_ARRAY_PRECIOS, precioProducto);
        bundle.putIntArray(ProductoFragment.EXTRA_ARRAY_IDS, idProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_TITULOS, tituloProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_IMAGENID, imagenIds);
        productosFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout_main, productosFragment);  //Uso replace en vez de add para que no se vean las imagenes repetidas en el fondo
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void cargarNombreUsuario(){
        //Obtengo el header y le cambio el texto donde va a el nombre del usuario
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView textUsuario = (TextView) header.findViewById(R.id.nav_nombreUsuario);
        TextView textEmail = (TextView) header.findViewById(R.id.nav_emailUsuario);
        sharedPreferences = getApplicationContext().getSharedPreferences(SHAREDPREFS_DATOS_USUARIO, 0);

        textUsuario.setText(sharedPreferences.getString(SHAREDPREFS_NOMBRE_USUARIO, "Invitado"));
        textEmail.setText(sharedPreferences.getString(SHAREDPREFS_EMAIL_USUARIO, ""));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Que hacer cuando se selecciona un elemento del drawer
        //Obtengo el id del item que selecciono y abro el fragmento/actividad
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        //Obtengo si esta el usuario logeado. Supongo que se podria hacer verificando si hay un email y no un "FALLO"
        sharedPreferences = getApplicationContext().getSharedPreferences(SHAREDPREFS_DATOS_USUARIO, 0);
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
            case R.id.nav_favoritos:
                requiresLogin = true;
                intent = new Intent(this, FavoritosActivity.class);
                break;
        }
        //Si no pide login que pase. Si esta logueado entonces siempre puede pasar
        if(!requiresLogin||isLoggedIn) {
            startActivity(intent);
        }else{
            Toast.makeText(this, getString(R.string.error_is_not_login), Toast.LENGTH_SHORT).show();
        }

        //cierro el drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public void onBackPressed() {
        //Si apreto para atras y esta el drawer, cerralo. Si no, hace lo que haga super
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        //Recarga los productos cuando resumas
        super.onResume();
        cargarFragmentoProductos("");
    }

    @Override
    protected void onRestart() {
        //Recarga el usuario si es que tenes a alguien en shared preferences
        super.onRestart();
        cargarNombreUsuario();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Asigno las herramientas a la toolbar
        int id = item.getItemId();
        if(id == R.id.action_search && textoBuscador == null){  //Verifico que no este creado ya
            textoBuscador = new EditText(this);
            textoBuscador.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.WRAP_CONTENT));
            Toolbar layout = findViewById(R.id.toolbar);
            layout.addView(textoBuscador);

            textoBuscador.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    cargarFragmentoProductos(editable.toString());
                }
            });
        }else if(id == R.id.action_sound){
            if(musicOn){
                stopService(new Intent(this, BackgroundSoundService.class));
                musicOn = false;
            }else{
                startService(new Intent(this, BackgroundSoundService.class));
                musicOn = true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        // your app come to background
        stopService(new Intent(this, BackgroundSoundService.class));
    }


}