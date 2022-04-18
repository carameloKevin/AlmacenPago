package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class ComprarProductoActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL_STRING = "email";
    public static final String EXTRA_PRODUCT_NAME = "producto";
    public static final String EXTRA_ID_PRODUCTO = "id_producto";
    //private final Drawable EXTRA_IMAGE_RESOURCE = getDrawable(R.drawable.iphone);
    private  Drawable IMAGEN_PROCUTO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_producto);
        Bundle extras = getIntent().getExtras();

        //Lee los campos de la tarjeta y confirma la compra. Lo deberia agregar a una lista de los productos que compro un usurio, asi que nueva tabal\
        TextView etTituloProducto = (TextView) findViewById(R.id.tv_item_en_compra);
        TextView etEmailUsuario = (TextView) findViewById(R.id.tv_email_usuario);
        EditText numeroTarjeta = (EditText) findViewById(R.id.et_numero_tarjeta);
        EditText claveTarjeta = (EditText) findViewById(R.id.et_clave_tarjeta);
        Button buyButton = (Button) findViewById(R.id.bt_finish_buy);
        IMAGEN_PROCUTO = getDrawable(R.drawable.iphone);

        etTituloProducto.setText(getString(R.string.usted_esta_comprando)); //savedInstanceState.getString(EXTRA_PRODUCT_NAME)
        etEmailUsuario.setText(extras.getString(EXTRA_EMAIL_STRING));

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numeroTarjeta.getText().toString().equals("") && claveTarjeta.getText().toString().equals("")){
                    Toast.makeText(ComprarProductoActivity.this, getString(R.string.missing_info), Toast.LENGTH_SHORT).show();
                }else {
                    AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(ComprarProductoActivity.this);
                    SQLiteDatabase db = null;
                    try {
                        //El usuario puede comprar el mismo producto mas de una vez, lo que tendria que agregar mas adelante es la fecha y hora para distinguirlos, pero por ahora con id esta bien
                        Log.d(TAG, "onClick: Intentando insertar una Compra");
                        db = almacenPagoDatabaseHelper.getWritableDatabase();
                        almacenPagoDatabaseHelper.insertCompra(db, extras.getString(EXTRA_EMAIL_STRING), extras.getInt(EXTRA_ID_PRODUCTO));
                        Toast.makeText(ComprarProductoActivity.this, getString(R.string.compra_exitosa), Toast.LENGTH_SHORT).show();
                    } catch (SQLiteException e) {
                        Toast.makeText(ComprarProductoActivity.this, getString(R.string.error_sql), Toast.LENGTH_SHORT).show();
                    } finally {
                        db.close();
                    }
                }
            }
        });
    }
}