package com.kevinberg.almacenpago;



import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        //Agrego tabla de usuarios y un par de usuarios default
        sqLiteDatabase.execSQL("CREATE TABLE USUARIO(email TEXT PRIMARY KEY," +
                "password TEXT," +
                "nombre TEXT," +
                "apellido TEXT);");

        //Agrego tabla de Productos y un par de datos default
        sqLiteDatabase.execSQL("CREATE TABLE PRODUCTO(_idProducto INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombreProd TEXT," +
                "descripcion TEXT," +
                "precio DOUBLE," +
                "image_resource_id INTEGER," +
                "emailVendedor TEXT," +
                "stock INTEGER," +
                "aLaVenta INTEGER," +
                "FOREIGN KEY(emailVendedor) REFERENCES USUARIO(email));");

        //Agergo tabla de que compro cada usuario
        sqLiteDatabase.execSQL("CREATE TABLE COMPRA(fecha DATE PRIMARY KEY," +
                "idProducto INTEGER," +
                "emailUsuario TEXT," +
                "stock INTEGER," +
                "FOREIGN KEY(emailUsuario) REFERENCES USUARIO(email)," +
                "FOREIGN KEY(idProducto) REFERENCES PRODUCTO(_idProducto));");

        //Agergo tabla de favoritos de cada usuario
        sqLiteDatabase.execSQL("CREATE TABLE FAVORITO(idProducto INTEGER," +
                "emailUsuario TEXT," +
                "FOREIGN KEY (idProducto) references PRODUCTO(_idProducto)," +
                "FOREIGN KEY (emailUsuario) references USUARIO(email));");


        /*
        / Datos cargados cuando se crea la BD. Todoo super hardcodeado
        / Si queres volver a cargarlos tenes que eliminar la BD
        / Tecnicamente se deberia hacer con el UPDATE, pero es mucho trabajo para nada
         */

        insertUsuario(sqLiteDatabase, "bergkevin1996@gmail.com", "1234", "Kevin", "Berg");
        insertUsuario(sqLiteDatabase, "test@test.com", "1111", "YoSoy", "ElTest");
        insertUsuario(sqLiteDatabase, "axelberg@gmail.com", "1111", "Axel", "Berg");


        Resources res = context.getResources();
        int resID = R.drawable.pringles;
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));

        insertProducto(sqLiteDatabase, "Papas Fritas", "Tubo de papas fritas 500grm", uri.toString(), 100,23, "bergkevin1996@gmail.com",true);

        resID = R.drawable.samsung;
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));
        insertProducto(sqLiteDatabase, "Celular Samsung", "Es un celular Samsung de 4\" con Mp3 y otras cosas", uri.toString(), 10123,1, "bergkevin1996@gmail.com", true);

        resID = R.drawable.applewatch;
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));
        insertProducto(sqLiteDatabase, "Smartwatch", "Reloj inteligente con todos los chiches", uri.toString(), 53213,10, "axelberg@gmail.com", true);

        resID = R.drawable.banana;
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));
        insertProducto(sqLiteDatabase, "Banana", "Banana. Que mas necesitas?", uri.toString(), 20,150, "bergkevin1996@gmail.com", false);

        resID = R.drawable.echo;
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));
        insertProducto(sqLiteDatabase, "Amazon Echo", "Te escucha en todo momento, incluso mientras dormis. Una joyita del futuro distopico en el que vamos a vivir", uri.toString(), 65436.32,7, "test@test.com", true);

        resID = R.drawable.gameboy;
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));
        insertProducto(sqLiteDatabase, "Gameboy Color Amarillo", "Un gameboy, un clasico de los fichines. Es como un Tetris, pero mejor porque tiene mas juegos aparte del tetris! Y a color!", uri.toString(), 30020.23,5, "axelberg@gmail.com", false);

        resID = R.drawable.glasses;
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resID)+"/"+ res.getResourceTypeName(resID) + "/" + res.getResourceEntryName(resID));
        insertProducto(sqLiteDatabase, "Lentes de Sol", "Son lentes de sol caros para hacerte el lindo por la playa. \n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi facilisis sem egestas urna finibus, ornare vulputate lacus ullamcorper. " +
                "Nulla lectus felis, cursus sit amet pellentesque sed, sodales sed turpis. " +
                "Mauris aliquam nibh sit amet malesuada viverra. Phasellus dignissim nibh non " +
                "luctus ornare. Ut eleifend et massa nec egestas. Duis bibendum diam quis justo" +
                "agittis tristique. Sed sed nulla dui. Maecenas " +
                "sit amet sodales augue. ", uri.toString(), 1020.23,6, "test@test.com",true);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //No lo uso porque soy flojo y rompo toodo
    }

    public void insertProducto(SQLiteDatabase db, String nombreProducto, String descripcion, String uri, double precio, int stock, String usuarioId, boolean aLaVenta){
        int booleanVenta = 0;
        if(aLaVenta){
            booleanVenta = 1;
        }
        ContentValues productoValues = new ContentValues();
        productoValues.put("nombreProd", nombreProducto);
        productoValues.put("descripcion", descripcion);
        productoValues.put("image_resource_id", uri);
        productoValues.put("precio", precio);
        productoValues.put("emailVendedor", usuarioId);
        productoValues.put("aLaVenta", booleanVenta);
        productoValues.put("stock", stock);


        db.insert("PRODUCTO", null, productoValues);
    }

    public  void insertUsuario(SQLiteDatabase db, String email, String password, String nombre, String apellido){
        ContentValues usuarioValues = new ContentValues();
        usuarioValues.put("email", email);
        usuarioValues.put("password", password);
        usuarioValues.put("nombre", nombre);
        usuarioValues.put("apellido", apellido);

        db.insert("USUARIO", null, usuarioValues);
    }


    public void insertCompra(SQLiteDatabase db, Date dateInput, String email, Integer id, int stock){
        ContentValues compraValue = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = sdf.format(dateInput);
        compraValue.put("fecha", date);
        compraValue.put("emailUsuario", email);
        compraValue.put("idProducto", id);
        compraValue.put("stock", stock);
        db.insert("COMPRA", null, compraValue);

    }

    public void insertFavorito(SQLiteDatabase db, Integer idProd, String email){
        ContentValues favValue = new ContentValues();
        favValue.put("emailUsuario", email);
        favValue.put("idproducto", idProd);
        db.insert("FAVORITO", null, favValue);
        Log.d(TAG, this.getClass().toString()+ " insertFavorito: Agregado a favoritos");
    }

}
