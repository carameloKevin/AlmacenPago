package com.kevinberg.almacenpago;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class CompradosListAdapater  extends ArrayAdapter {
    private final String TAG = "CompradosListAdapater";

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
        Log.d(TAG, "CompradosListAdapter getView: Estoy por mostrar el elemento " + nombreProducto[position]);
        if(convertView == null){
            row = inflater.inflate(R.layout.row_item, null, true);
        }
        TextView textViewNombreProducto =   row.findViewById(R.id.textViewNombreProducto);
        TextView textViewPrecioProducto =   row.findViewById(R.id.textViewPrecioProducto);
        ImageView imagenProducto =          row.findViewById(R.id.imageViewProducto);

        textViewNombreProducto.setText(nombreProducto[position]);
        textViewPrecioProducto.setText(String.valueOf(precioProducto[position]));

        imagenProducto.setImageURI(Uri.parse(imageId[position]));
        return row;
    }
}
