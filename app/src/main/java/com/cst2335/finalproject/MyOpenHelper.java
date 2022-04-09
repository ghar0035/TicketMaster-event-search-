package com.cst2335.finalproject;
/**
 * course: 22W-CST 2335-011
 * author afsaneh khabbazibasmenj
 * professor Abul Qasim
 * student number: 040998618
 * file name: Detail.java
 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class MyOpenHelper extends SQLiteOpenHelper {
    public static final String fileName = "EventManagerDatabase";
    public static final int version = 1;
    public static final String TABLE_NAME = "EventTable";
    public static final String COL_ID = "_id";
    public static final String COL_DATA = "data";


    /*
        context – the Activity where the database is being opened.
        databaseName – this is the filename that will contain the data.
        factory – An object to create Cursor objects, normally this is null.
        version – What is the version of your database
    */
    public MyOpenHelper(Context context) {
        super(context, fileName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //the creation statement. Create table MsgTable
        db.execSQL( String.format( "Create table %s ( %s TEXT, %s TEXT);"
                , TABLE_NAME, COL_ID, COL_DATA) );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "Drop table if exists " + TABLE_NAME ); //deletes the current data

        //create a new table:
        this.onCreate(db); //calls function on line 26
    }

}