package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public void insertData(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id",id);
        cv.put("name", name);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public ArrayList getEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> eventList = new ArrayList<>();
        String selectAll = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                eventList.add(cursor.getString(0));
                eventList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    public void deleteEvent(String id) {

        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_NAME, "id=?", new String[]{id});
        db.close();
    }
}