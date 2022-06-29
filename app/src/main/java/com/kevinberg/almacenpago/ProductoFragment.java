package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProductoFragment extends Fragment {

    private static final int LARGO_TEST = 2;
    public static final String EXTRA_ARRAY_TITULOS = "arregloTitulos";
    public static final String EXTRA_ARRAY_PRECIOS = "arregloPrecio";
    public static final String EXTRA_ARRAY_IMAGENID = "arregloImagenId";
    public static final String EXTRA_ARRAY_IDS = "arregloIdsProductos";

    String[] tituloProducto;
    double[] precioProducto;
    String[] imagenIds;
    int[] productoIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Mostramos un monton de productos en un fragmento
        //Este fragmento toma los datos de cada producto y los muestra en cartas. Tiene que funcionar con los elementos que le digan de arriba
        RecyclerView productoRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_producto, container, false);

        //Si me mandaron los datos creo la lista, si no se lo saltea y devuelve el RecyclerView vacio
        if(getArguments() != null) {

            tituloProducto = getArguments().getStringArray(EXTRA_ARRAY_TITULOS);
            imagenIds = getArguments().getStringArray(EXTRA_ARRAY_IMAGENID);
            productoIds = getArguments().getIntArray(EXTRA_ARRAY_IDS);
            precioProducto = getArguments().getDoubleArray(EXTRA_ARRAY_PRECIOS);

            ImagenSubAdapter adapter = new ImagenSubAdapter(tituloProducto, imagenIds, this.getContext());
            productoRecycler.setAdapter(adapter);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            productoRecycler.setLayoutManager(layoutManager);


            adapter.setListener(new ImagenSubAdapter.Listener() {
                @Override
                public void onClick(int position) {
                    Log.d(TAG, "onClick: en la pos que clickeaste esta "+ tituloProducto[position]);
                    Intent intent = new Intent(getActivity(), ProductoDetallesActivity.class);
                    intent.putExtra(ProductoDetallesActivity.EXTRA_PRODUCTO_ID, productoIds[position]);
                    getActivity().startActivity(intent);
                }
            });
        }
        return productoRecycler;

    }
}