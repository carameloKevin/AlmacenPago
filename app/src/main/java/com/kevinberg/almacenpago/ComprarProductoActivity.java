package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ComprarProductoActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL_STRING = "email";
    public static final String EXTRA_PRODUCT_NAME = "producto";
    public static final String EXTRA_ID_PRODUCTO = "id_producto";
    public static final String EXTRA_CANT_PRODUCTO = "cantProd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_producto);
        Bundle extras = getIntent().getExtras();

        //Lee los campos de la tarjeta y confirma la compra. Lo deberia agregar a una lista de los productos que compro un usurio, asi que nueva tabla
        TextView etTituloProducto = (TextView) findViewById(R.id.tv_item_en_compra);
        TextView etEmailUsuario = (TextView) findViewById(R.id.tv_email_usuario);
        TextView etCantElemento = (TextView) findViewById(R.id.tvAmmount);
        EditText numeroTarjeta = (EditText) findViewById(R.id.et_numero_tarjeta);
        EditText claveTarjeta = (EditText) findViewById(R.id.et_clave_tarjeta);

        Button buyButton = (Button) findViewById(R.id.bt_finish_buy);

        etTituloProducto.setText(extras.getString(EXTRA_PRODUCT_NAME));
        etEmailUsuario.setText(extras.getString(EXTRA_EMAIL_STRING));
        etCantElemento.setText(""+extras.getInt(EXTRA_CANT_PRODUCTO));

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                if(numeroTarjeta.getText().toString().equals("") && claveTarjeta.getText().toString().equals("")){
                    Toast.makeText(ComprarProductoActivity.this, getString(R.string.missing_info), Toast.LENGTH_SHORT).show();
                }else {
                    AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(ComprarProductoActivity.this);
                    SQLiteDatabase db = null;
                    try {
                        //El usuario puede comprar el mismo producto mas de una vez, lo que tendria que agregar mas adelante es la fecha y hora para distinguirlos, pero por ahora con id esta bien
                        Log.d(TAG, "onClick: Intentando insertar una Compra");
                       int idProducto = extras.getInt(EXTRA_ID_PRODUCTO);

                        db =almacenPagoDatabaseHelper.getReadableDatabase();
                        //Solo obtengo el stock para poder actualizarlo al nuevo valor, horrible
                        Cursor cursor = db.query("PRODUCTO",new String[]{"stock"},"_idProducto = ?", new String[]{Integer.toString(idProducto)},null,null,null);
                        cursor.moveToFirst();   //Por alguna razon este cursor se tiene que mover al primer elemento, creo que lo otros no tuve que hacer eso
                        Log.d(TAG, "ComprarPodructo onClick: "+ cursor.getColumnNames().length+ " " + cursor.getString(0));

                        int pos = cursor.getColumnIndex("stock");
                        int stockFinal = cursor.getInt(pos);
                        // - extras.getInt(EXTRA_CANT_PRODUCTO);

                        db = almacenPagoDatabaseHelper.getWritableDatabase();
                        //Agrego la compra a la lista de comprados del usuario
                        almacenPagoDatabaseHelper.insertCompra(db, Calendar.getInstance().getTime(), extras.getString(EXTRA_EMAIL_STRING), extras.getInt(EXTRA_ID_PRODUCTO), extras.getInt(EXTRA_CANT_PRODUCTO));
                        setResult(RESULT_OK);
                        Toast.makeText(ComprarProductoActivity.this, getString(R.string.compra_exitosa), Toast.LENGTH_SHORT).show();
                    } catch (SQLiteException e) {
                        Toast.makeText(ComprarProductoActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    } finally {
                        db.close();
                    }
                }
                Activity act =(Activity) view.getContext();
                act.finish();
            }
        });
    }
}