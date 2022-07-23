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

import org.w3c.dom.Text;

public class CompradosListAdapater  extends ArrayAdapter {
    private final String TAG = "CompradosListAdapater";

    private String[] nombreProducto, imageId, fechaCompras;
    private double[] precioProducto;
    private int[] idProducto;
    private  Activity context;

    public CompradosListAdapater(Activity context,int[] idProducto, String[] nombreProducto, double[] precioProducto, String[] imageId, String[] fechaProducto){
        super(context, R.layout.row_item, nombreProducto);
        this.context = context;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.imageId = imageId;
        this.fechaCompras = fechaProducto;
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
        TextView textViewFechaCompra =      row.findViewById(R.id.fechaCompra);
        ImageView imagenProducto =          row.findViewById(R.id.imageViewProducto);

        textViewNombreProducto.setText(nombreProducto[position]);
        textViewPrecioProducto.setText("$"+String.valueOf(precioProducto[position]));
        textViewFechaCompra.setText(fechaCompras[position]);    //getContext().getString(R.string.compra_fecha)+ " " +

        imagenProducto.setImageURI(Uri.parse(imageId[position]));
        return row;
    }
}
