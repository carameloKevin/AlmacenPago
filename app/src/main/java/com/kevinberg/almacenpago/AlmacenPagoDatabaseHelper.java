package com.kevinberg.almacenpago;



import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class AlmacenPagoDatabaseHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "almacenPago";
    private static final int DB_VERSION = 1;
    //Especificamente para cargar datos de drawable
    private static Context context;

    AlmacenPagoDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //para cargar imagenes de drawable
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Agrego tabla de Productos y un par de datos default
        sqLiteDatabase.execSQL("CREATE TABLE PRODUCTO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NOMBREPROD TEXT," +
                "DESCRIPCION TEXT," +
                "PRECIO DOUBLE," +
                "IMAGE_RESOURCE_ID INTEGER," +
                "EMAIL TEXT);");

        //Agrego tabla de usuarios y un par de usuarios default
        sqLiteDatabase.execSQL("CREATE TABLE USUARIO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "EMAIL TEXT," +
                "PASSWORD TEXT," +
                "NOMBRE TEXT," +
                "APELLIDO TEXT);");

        //Agergo tabla de que compro cada usuario
        sqLiteDatabase.execSQL("CREATE TABLE COMPRA(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "EMAILUSUARIO TEXT," +
                "IDPRODUCTO INTEGER);");

        /*
        // Datos cargados cuando se crea la BD
         */

        insertUsuario(sqLiteDatabase, "bergkevin1996@gmail.com", "1234", "Kevin", "Berg");
        insertUsuario(sqLiteDatabase, "test@test.com", "1111", "YoSoy", "ElTest");
        Resources res = context.getResources();
        int resID = R.drawable.pringles;
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));

        insertProducto(sqLiteDatabase, "Papas Fritas", "Tubo de papas fritas 500grm", uri.toString(), 100, "bergkevin1996@gmail.com");

        resID = R.drawable.samsung;
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));
        insertProducto(sqLiteDatabase, "Celular Samsung", "Es un celular Samsung de 4\" con Mp3 y otras cosas", uri.toString(), 100, "bergkevin1996@gmail.com");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //No lo uso porque soy flojo y rompo todo
    }

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

}
