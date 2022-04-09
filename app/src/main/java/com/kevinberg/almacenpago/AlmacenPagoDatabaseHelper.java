package com.kevinberg.almacenpago;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class AlmacenPagoDatabaseHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "almacenPago";
    private static final int DB_VERSION = 1;

    AlmacenPagoDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Agrego tabla de Productos y un par de datos default
        sqLiteDatabase.execSQL("CREATE TABLE PRODUCTO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NOMBREPROD STRING," +
                "DESCRIPCION STRING," +
                "PRECIO DOUBLE," +
                "IMAGE_RESOURCE_ID INTEGER," +
                "EMAIL STRING);");

        //Agrego tabla de usuarios y un par de usuarios default
        sqLiteDatabase.execSQL("CREATE TABLE USUARIO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "EMAIL STRING," +
                "PASSWORD STRING," +
                "NOMBRE STRING," +
                "APELLIDO STRING);");

        //Agergo tabla de que compro cada usuario
        sqLiteDatabase.execSQL("CREATE TABLE COMPRA(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "EMAILUSUARIO STRING," +
                "IDPRODUCTO INTEGER);");

        insertUsuario(sqLiteDatabase, "bergkevin1996@gmail.com", "1234", "Kevin", "Berg");
        insertUsuario(sqLiteDatabase, "test@test.com", "1111", "YoSoy", "ElTest");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Todavia no cambie la DB
    }

    /*
    No sirve mas porque ahora guardo URI que son string
    private  void insertProducto(SQLiteDatabase db, String nombreProducto, String descripcion, int resourceId, double precio, String usuarioId){
        ContentValues productoValues = new ContentValues();
        productoValues.put("NOMBREPROD", nombreProducto);
        productoValues.put("DESCRIPCION", descripcion);
        productoValues.put("IMAGE_RESOURCE_ID", resourceId);
        productoValues.put("PRECIO", precio);
        productoValues.put("EMAIL", usuarioId);
        db.insert("PRODUCTO", null, productoValues);
        }
*/

    public  void insertProducto(SQLiteDatabase db, String nombreProducto, String descripcion, String uri, double precio, String usuarioId){
        ContentValues productoValues = new ContentValues();
        productoValues.put("NOMBREPROD", nombreProducto);
        productoValues.put("DESCRIPCION", descripcion);
        productoValues.put("IMAGE_RESOURCE_ID", uri);
        productoValues.put("PRECIO", precio);
        productoValues.put("EMAIL", usuarioId);
        db.insert("PRODUCTO", null, productoValues);
    }

    public  void insertUsuario(SQLiteDatabase db, String email, String password, String nombre, String apellido){
        ContentValues usuarioValues = new ContentValues();
        usuarioValues.put("EMAIL", email);
        usuarioValues.put("PASSWORD", password);
        usuarioValues.put("NOMBRE", nombre);
        usuarioValues.put("APELLIDO", apellido);
        db.insert("USUARIO", null, usuarioValues);
    }

    public void insertCompra(SQLiteDatabase db, String email, Integer id){
        ContentValues compraValue = new ContentValues();
        compraValue.put("EMAILUSUARIO", email);
        compraValue.put("IDPRODUCTO", id);
        db.insert("COMPRA", null, compraValue);
    }

    public static boolean existeUsuario(SQLiteDatabase db, String email){
        //Este metodo no sirve de mucho por ahora
        boolean existe = false;
        Cursor cursor = db.query("USUARIO", new String[]{"_ID", "EMAIL"}, "EMAIL=?", new String[]{email}, null, null, null);
        existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }
}
