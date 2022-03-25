package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlmacenPagoDatabaseHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "almacenPago";
    private static final int DB_VERSION = 1;

    AlmacenPagoDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE PRODUCTO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NOMBREPROD STRING," +
                "DESCRIPCION TEXT," +
                "PRECIO DOUBLE," +
                "IMAGE_RESOURCE_ID INTEGER," +
                "DUENOID STRING);");

        Log.d(TAG, "onCreate: AGREGANDO LOS DATOS");
        insertProducto(sqLiteDatabase, "Iphone", "Es un celu", R.drawable.iphone, 100, "Kevin01");
        insertProducto(sqLiteDatabase, "Samsung", "Este es un samsung", R.drawable.samsung, 99, "Axel01");
        Log.d(TAG, "onCreate: DATOS AGREGADOS");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Todavia no cambie la DB
    }

    private static void insertProducto(SQLiteDatabase db, String nombreProducto, String descripcion, int resourceId, double precio, String usuarioId){
        ContentValues productoValues = new ContentValues();
        productoValues.put("NOMBREPROD", nombreProducto);
        productoValues.put("DESCRIPCION", descripcion);
        productoValues.put("IMAGE_RESOURCE_ID", resourceId);
        Log.d(TAG, "insertProducto: IMAGEN " + resourceId + " iphone "+ R.drawable.iphone);
        productoValues.put("PRECIO", precio);
        productoValues.put("DUENOID", usuarioId);
        db.insert("PRODUCTO", null, productoValues);
        }

}
