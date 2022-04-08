package com.cst2335.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

/**

 */
public class SavedEvents extends Fragment {

    Snackbar snackbar;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    SQLiteDatabase db;
    MyOpenHelper myOpener;

    /**
     * default constructor
     */
    public SavedEvents() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedEvents.
     */

    public static SavedEvents newInstance(String param1, String param2) {
        SavedEvents fragment = new SavedEvents();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        MyOpenHelper myOpener = new MyOpenHelper(this.getActivity());
        SQLiteDatabase DB = myOpener.getWritableDatabase();
        ArrayList<Event> eventArrayList = myOpener.getEvents();


        ListAdapter adapter = new ListAdapter(this.getActivity(), eventArrayList);
        ListView eventListView = newView.findViewById(R.id.eventListView3);
        eventListView.setAdapter(adapter);

        Button checkLastDelBtn = newView.findViewById(R.id.btnCheckLastDel);


        /**
         * Check last deleted event button
         */
        checkLastDelBtn.setOnClickListener(view -> {
            SharedPreferences prefs = getActivity().getSharedPreferences("LAST EVENT", Context.MODE_PRIVATE);

            String eventID = prefs.getString("eventID", "");
            String eventDATA = prefs.getString("eventDATA", "");

        });

        return newView;
    }


    /**
     * ListAdapter interface implemented with the 4 functions to use with a customized list
     */
    private class ListAdapter extends BaseAdapter {
            private Context context;
            private ArrayList<Event> events;

            public ListAdapter(Context context, ArrayList<Event> events) {
                this.context = context;
                this.events = events;
            }

            public int getCount() {
                return events.size();
            }

            public Object getItem(int position) {
                return events.get(position);
            }

            public long getItemId(int position) {
                return position;
            }

            public View getView(int position, View newView, ViewGroup parent) {

                LayoutInflater inflater = getLayoutInflater();
                newView = inflater.inflate(R.layout.event_listview_layout, parent, false);

                LinearLayout rowLayout = newView.findViewById(R.id.eventContainer);

                Event currentEvent = (Event) getItem(position);

                TextView tView = newView.findViewById(R.id.eventID);
                TextView tView2 = newView.findViewById(R.id.eventDATA);
                Button deleteButton = newView.findViewById(R.id.delete_btn);

                tView.setText(currentEvent.getEventID());
                tView2.setText(currentEvent.getEventData());

                deleteButton.setTag(position);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar = Snackbar.make(view, "The event has been deleted", 3000);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        SharedPreferences prefs = getActivity().getSharedPreferences("LAST EVENT", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = prefs.edit();

                        edit.putString("eventID", currentEvent.eventID.toString());
                        edit.putString("eventDATA", currentEvent.eventData.toString());
                        edit.apply();
                        snackbar.show();
                    }
                });

                return newView;   //return the inflated view
            }
        }

    /**
     * model event class for ArrayList
     */
    static class Event {
        private String eventID;
        private String eventData;
        public Event (String eventID, String eventData) {
            this.eventID = eventID;
            this.eventData = eventData;
        }

        public String getEventID() {
            return this.eventID;
        }

        public String getEventData() {
            return eventData;
        }
    }

}