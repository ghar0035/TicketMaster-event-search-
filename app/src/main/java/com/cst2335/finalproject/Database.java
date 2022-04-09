package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public Database(Context context){
        super(context, "EventDatabase2.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String eventTable = "create table event(id text,name text)";
        db.execSQL(eventTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    public void insertData(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id",id);
        cv.put("name", name);
        db.insert("event", null, cv);
        db.close();
    }


    public ArrayList getEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> eventList = new ArrayList<>();
        String selectAll = "SELECT * FROM event";
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

    public void deleteEvent(String eventName) {

        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete("eventDB", "name=?", new String[]{eventName});
        db.close();
    }


}
