package com.kevinberg.almacenpago;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Todavia no cambie la DB
    }

    private static void insertProducto(SQLiteDatabase db, String nombreProducto, String descripcion, int resourceId, double precio, String usuarioId){
        ContentValues productoValues = new ContentValues();
        productoValues.put("NOMBRE", nombreProducto);
        productoValues.put("DESCRIPCION", descripcion);
        productoValues.put("IMAGE_RESOURCE_ID", resourceId);
        productoValues.put("PRECIO", precio);
        productoValues.put("DEUNOID", usuarioId);
        db.insert("PRODUCTO", null, productoValues);
        }

}
