package com.jaac08.prueba_fase1.ClassDB;

public class EstructuraBD {
    public static final String TABLA_POST = "POST";

    public static final String CREAR_TABLA_POST = "CREATE TABLE POST ( " +
            "  ID INTEGER primary key," +
            "  USERID INTEGER," +
            "  TITLE TEXT," +
            "  BODY TEXT," +
            "  READ INTEGER," +
            "  FAVORITE INTEGER)";
}
