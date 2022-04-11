package com.kevinberg.almacenpago;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CompradosListAdapater  extends ArrayAdapter {
    private String[] nombreProducto;
    private double[] precioProducto;
    private Integer[] imageId;
    private  Activity context;

    public CompradosListAdapater(Activity context, String[] nombreProducto, double[] precioProducto, Integer[] imageId){
        super(context, R.layout.row_item, nombreProducto);
        this.context = context;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.imageId = imageId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView == null){
            row = inflater.inflate(R.layout.row_item, null, true);
        }
        TextView textViewNombreProducto =   (TextView) row.findViewById(R.id.textViewNombreProducto);
        TextView textViewPrecioProducto =   (TextView) row.findViewById(R.id.textViewPrecioProducto);
        ImageView imgenProducto =           (ImageView) row.findViewById(R.id.imageViewProducto);

        textViewNombreProducto.setText(nombreProducto[position]);
        textViewPrecioProducto.setText(String.valueOf(precioProducto[position]));
        imgenProducto.setImageResource(R.drawable.iphone);//imageId[position]);
        return row;
    }
}
