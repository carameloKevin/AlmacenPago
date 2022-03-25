package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImagenSubAdapter extends RecyclerView.Adapter<ImagenSubAdapter.ViewHolder> {

    private String[] subtitulos;
    private int[] imagenIds;
    private Listener listener;


    /*
    Creo la interfaz listener para que despues el que vaya
    a usar esta clase le diga a esta clase que tiene que hacer
    cuando tocan una carta
 */
    interface Listener{
        void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //El ViewHolder describe la View y metadata de un item. Te da su ubicacion dentro del recylcerview;

       /*---
       Porque puede acceder a esta variable si es privada? Se accede en onBindViewHolder
        ---*/
        private CardView cardView;

        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public ImagenSubAdapter(String[] subtitulos, int[] imagenIds){
        //Le informo al adapter con que valores trabajar
        this.subtitulos = subtitulos;
        this.imagenIds = imagenIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Se "infla" la carta en el contexto
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subtitulo_imagen, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Asigno los datos que van en la card
        CardView cardView = holder.cardView;

        //Obtengo la ubicacion de XML donde va la imagen y se la asigno
        Log.d(TAG, "onBindViewHolder: Imagenes en ImagenSubAdapter");
        ImageView imageView = (ImageView) cardView.findViewById(R.id.info_image);
        Drawable drawable = ContextCompat.getDrawable(cardView.getContext(), imagenIds[position]);
        imageView.setImageDrawable(drawable);

        //Idem texto
        TextView textView = (TextView) cardView.findViewById(R.id.info_text);
        textView.setText(subtitulos[position]);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onClick(holder.getAbsoluteAdapterPosition());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return subtitulos.length;
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }
}
