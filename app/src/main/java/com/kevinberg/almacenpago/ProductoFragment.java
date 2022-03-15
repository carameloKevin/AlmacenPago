package com.kevinberg.almacenpago;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProductoFragment extends Fragment {

    static final int LARGO_TEST = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Mostramos un monton de productos en un fragmento
        //Este fragmento toma los datos de cada producto y los muestra en cartas
        RecyclerView productoRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_producto, container, false);
        /*
        Aca tiene que ir un cursor que apunta a la base de datos y obtiene todos estos datos
         */
        String[] nombreProducto = new String[LARGO_TEST];
        int[] imagenProducto = new int[LARGO_TEST];
        //---HardCodeado Test
        nombreProducto[0] = "iphone";
        imagenProducto[0] = R.drawable.iphoine;
        nombreProducto[1] = "samsung";
        imagenProducto[1] = R.drawable.samsung_phone;
        //--Fin Hardcode

        ImagenSubAdapter adapter = new ImagenSubAdapter(nombreProducto, imagenProducto);
        productoRecycler.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        productoRecycler.setLayoutManager(layoutManager);

        adapter.setListener(new ImagenSubAdapter.Listener(){
            @Override
            public void onClick(int position){
                Intent intent = new Intent(getActivity(), ProductoDetallesActivity.class);
                intent.putExtra(ProductoDetallesActivity.EXTRA_PRODUCTO_ID, position);
                getActivity().startActivity(intent);
            }
        });
        return productoRecycler;

    }
}