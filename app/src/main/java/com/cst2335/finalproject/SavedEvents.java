
/* Course: 22W-CST2335-011
 * Professor: Abul Qasim
 * Author: Evan Lin
 * File name: SavedEvents.java
 * Date: 2022-04-08
 * Final Project
 */
package com.cst2335.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SavedEvents extends Fragment {
    /**
     * ArrayList as a container for events
     */
    private ArrayList<Event> events = new ArrayList<>();

    /**
     * Instance of the ListAdapter class
     */
    ListAdapter eventAdapter;

    /**
     * Instance of the ListView class
     */
    ListView eventListView;

    /**
     * Instance of the MyOpenHelper class
     */
    MyOpenHelper myOpener;


    /**
     * Instance of the SQLiteDatabase class
     */
    SQLiteDatabase db;

    /**
     * default constructor
     */
    public SavedEvents() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    /**
     * this class is used to inflate the UI
     *
     * @param inflater : a LayoutInflater object to load an XML layout file
     * @param container  : acts as an invisible container in which other Views and Layouts are placed
     * @param savedInstanceState : a reference to a Bundle object that is passed into the onCreateView method
     * @return newView : is the root object from your XML file, It contains the widgets that are in your layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Evan - Saved Events");

        View newView = inflater.inflate(R.layout.fragment_saved_events, container, false);

        // Initialize list view
        eventListView = newView.findViewById(R.id.eventListView3);
        eventListView.setAdapter(eventAdapter = new ListAdapter());


        // Querying the database
        myOpener =  new MyOpenHelper(this.getActivity());
        db = myOpener.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + MyOpenHelper.TABLE_NAME + ";", null);
        int idIndex =  cursor.getColumnIndex(MyOpenHelper.COL_ID);
        int dataIndex = cursor.getColumnIndex(MyOpenHelper.COL_DATA);

        while(cursor.moveToNext()) {
           String dbId = cursor.getString(idIndex);
           String dbData = cursor.getString(dataIndex);

           Gson gson = new Gson();
           JsonObject eventObject = gson.fromJson(dbData, JsonObject.class);
           String name = eventObject.get("name").getAsString();

           JsonObject priceRange = eventObject.get("priceRanges").getAsJsonArray().get(0).getAsJsonObject();

           String currency = priceRange.get("currency").getAsString();
           String minPrice  = priceRange.get("min").getAsString();
           String maxPrice = priceRange.get("max").getAsString();

           String formattedPrice = "From " + minPrice + " - " + maxPrice + " " + currency;

           String dateStartObject = eventObject.get("sales").getAsJsonObject().get("public").getAsJsonObject().get("startDateTime").getAsString();

           String imgUrl = eventObject.get("images").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();

           Event currentEvent = new Event(dbId, name, formattedPrice, imgUrl, dateStartObject);
           events.add(currentEvent);
       }

        return newView;
    }


    /**
     * ListAdapter interface implemented with the 4 functions to use with a customized list
     */
    private class ListAdapter extends BaseAdapter {
        public int getCount() {
            return events.size();
        }

        public Event getItem(int position) {
            return events.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).id;
        }

        public View getView(int position, View newView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.event_listview_layout, parent, false);

            // Putting name and price in TextView
            TextView tView = newView.findViewById(R.id.eventName);
            tView.setText(getItem(position).name + "\n" + getItem(position).price);

            // Putting image in ImageView
            ImageView iView = newView.findViewById(R.id.eventImg);
            Picasso.get().load(getItem(position).img_link).into(iView);

            // Delete button with alert dialogue
            Button deleteBtn = newView.findViewById(R.id.delete_btn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure you want to delete this event?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int id) {
                                    db.delete(MyOpenHelper.TABLE_NAME, MyOpenHelper.COL_ID + "= ?" , new String[] { getItem(position)._id } );
                                    events.remove(position);
                                    eventAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(),"Event has been deleted!", Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.setNegativeButton(
                            "no",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            return newView;   //return the inflated view
        }
    }

    /**
     * model event class for ArrayList
     */
    static class Event {
        String _id ;
        String name;
        String price;
        String img_link;
        String date;
        long id;

        Event(String _id, String name , String price, String img_link, String date) {
            this._id = _id;
            this.name = name;
            this.price = price;
            this.img_link = img_link;
            this.date = date;
        }

    }

}