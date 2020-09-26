package com.jaac08.prueba_fase1.ClassDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ConexionSQliteHelper extends SQLiteOpenHelper {


    public ConexionSQliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EstructuraBD.CREAR_TABLA_POST);
        //db.execSQL(EstructuraBD.CREAR_TABLA_DETALLE);
        //db.execSQL(EstructuraBD.CREAR_TABLA_EMAILSMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DISPOSITIVO");
        //db.execSQL("DROP TABLE IF EXISTS DETALLE");
        //db.execSQL("DROP TABLE IF EXISTS EMAILSMS");
        onCreate(db);
    }
}
