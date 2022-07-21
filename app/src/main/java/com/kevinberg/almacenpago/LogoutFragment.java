package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class LogoutFragment extends Fragment {

    //Interfaz para interactuar con la actividad Padre
    public interface LogoutListener {
        void setLogoutStatus();
    }

    private LogoutListener listener;
    private SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Un fragment que muestra quien esta logueado y un botton de logout
        sharedPreferences = this.getContext().getApplicationContext().getSharedPreferences(MainActivity.SHAREDPREFS_DATOS_USUARIO, 0);

        String nombreUsuario = sharedPreferences.getString(MainActivity.SHAREDPREFS_NOMBRE_USUARIO, "Usuario");//savedInstanceState.getString(USER_NAME);
        String emailUsuario = sharedPreferences.getString("email", "null");

        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        TextView isLoggedIn = view.findViewById(R.id.tv_isLoggedIn);
        Button btLogout = view.findViewById(R.id.bt_logout);

        isLoggedIn.setText(nombreUsuario + " " +getResources().getString(R.string.is_logged_in));
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.remove("email");
                editor.remove("nombre");
                editor.commit();
                listener.setLogoutStatus();
            }
        });

        cargarFragmentoProductos(emailUsuario);

        return view;
    }

    private void cargarFragmentoProductos(String emailUsuario){
        //Copiado y pegado de MainActivity, codigo CASI repetido
        //Obtengo una query y lo paso a los arreglos necesarios para el fragmento;
        SQLiteOpenHelper almacenPagoDBHelper = new AlmacenPagoDatabaseHelper(this.getContext());
        String[] tituloProducto = new String[0];
        String[] imagenIds = new String[0];
        int[] idProducto = new int[0];
        double[] precioProducto = new double[0];
        try {
            SQLiteDatabase db = almacenPagoDBHelper.getReadableDatabase();


            //Unico gran cambiio
            Cursor cursor = db.query("PRODUCTO", new String[]{"_idProducto, nombreProd", "image_resource_id", "precio"}, "emailVendedor = ?", new String[] {emailUsuario}, null, null, "_idProducto DESC", "10");

            if (cursor.moveToFirst()) {
                Log.d(TAG, "LogoutFragment - cargarFragmentoProducto: Hay un elemento en el cursor. Leyendolo");

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

                    Log.d(TAG, idProducto[pos] + " " + tituloProducto[pos] + " " + precioProducto[pos]);

                    pos++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this.getContext(), getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
        }

        //Agrego un ProductoFragment al frame layout. Cuando creo el fragment le envio tambien el bundle con los datos
        Fragment productosFragment = new ProductoFragment();

        //bundle con los datos con los que trabajar
        Bundle bundle = new Bundle();
        bundle.putIntArray(ProductoFragment.EXTRA_ARRAY_IDS, idProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_TITULOS, tituloProducto);
        bundle.putStringArray(ProductoFragment.EXTRA_ARRAY_IMAGENID, imagenIds);
        bundle.putDoubleArray(ProductoFragment.EXTRA_ARRAY_PRECIOS, precioProducto);
        productosFragment.setArguments(bundle);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.productos_usuario, productosFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (LogoutListener) context;
    }
}