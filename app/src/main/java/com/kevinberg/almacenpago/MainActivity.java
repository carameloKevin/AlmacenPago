package com.kevinberg.almacenpago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //agrego la toolbar arriba del todo
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //--Drawer--
        //Seleccion el drawer y le digo que ponga el simbolo en la toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_abrir_drawer, R.string.nav_cerrar_drawer);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        //Basicamente le agrego las funciones al drawer
        NavigationView navigationView =  (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                break;
            case R.id.nav_ultimas_compras:
                //fragment = new UltimasComprasFragment();
                break;
            case R.id.nav_usuario:
                //fragment = new UsuarioFragment();
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