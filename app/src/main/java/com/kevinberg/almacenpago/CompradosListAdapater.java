package com.kevinberg.almacenpago;

import android.app.Activity;
import android.net.Uri;
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
    private int[] idProducto;
    private String[] imageId;
    private  Activity context;

    public CompradosListAdapater(Activity context,int[] idProducto, String[] nombreProducto, double[] precioProducto, String[] imageId){
        super(context, R.layout.row_item, nombreProducto);
        this.context = context;
        this.idProducto = idProducto;
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
        ImageView imagenProducto =          (ImageView) row.findViewById(R.id.imageViewProducto);

        textViewNombreProducto.setText(nombreProducto[position]);
        textViewPrecioProducto.setText(String.valueOf(precioProducto[position]));

        if(imageId[position] != null) {
            imagenProducto.setImageURI(Uri.parse(imageId[position]));
        }else{
            //Si no tiene imagne uso una por defecto
            imagenProducto.setImageDrawable(getContext().getResources().getDrawable(R.drawable.no_image_available));
        }
        return row;
    }
}
