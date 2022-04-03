package com.cst2335.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    private ArrayList<Event> events = new ArrayList<>();
    ImageView placeholderImage;
    ListAdapter eventAdapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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

        getActivity().setTitle("Home - Mehri Gh - v1");
           }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newView = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        placeholderImage = newView.findViewById(R.id.searchPlaceholder);

        Button btnSearch = newView.findViewById(R.id.btnSearch);
        ListView eventListView = newView.findViewById(R.id.eventListView);
        /*To populate the ListView with data,call setAdapter() on the ListView,to associate an adapter with the list*/
        eventListView.setAdapter(eventAdapter = new ListAdapter());

        // Add search event
        btnSearch.setOnClickListener(click -> {
            searchNearByEvents(newView);
            eventListView.smoothScrollToPosition(0);
        });

        eventListView.setOnItemClickListener( (list, view, position , id) -> {

            Details detailFragment = new Details();
            Bundle args = new Bundle();
            args.putString(detailFragment.ID, events.get(position)._id);
            detailFragment.setArguments(args);



            FragmentTransaction ft = getParentFragmentManager().beginTransaction();// begin  FragmentTransaction
            ft.setReorderingAllowed(true);
            ft.replace(R.id.frameLayout, detailFragment, "DETAIL").addToBackStack("HOME");    // add    Fragment
            ft.commit();
        });

        return newView;
    }

    private void searchNearByEvents(View newView) {
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        EditText eventTextBox = newView.findViewById(R.id.eventTextBox);
        EditText radiusTextBox = newView.findViewById(R.id.radiusTextBox);
        String result = null;
        try {
            result = new RequestTask()
                    .execute("https://app.ticketmaster.com/discovery/v2/events.json?apikey=82OhVMuQzr9Lf0Jnp9Zcvhqs1HkBQBAC&city="+ eventTextBox.getText() +"&radius="+ radiusTextBox.getText())
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        events.removeAll(events);

        Gson gson = new Gson();
        JsonObject eventObject = gson.fromJson(result, JsonObject.class);
        JsonObject initialResponse = eventObject.get("_embedded").getAsJsonObject();
        JsonArray eventsArray = initialResponse.get("events").getAsJsonArray();

       Toast.makeText(getActivity(), eventsArray.size() + " events found nearby!", Toast.LENGTH_LONG).show();

        for(int i=0; i < eventsArray.size(); i++) {
            JsonElement item = eventsArray.get(i);
            JsonObject obj = item.getAsJsonObject();

            String name = obj.get("name").getAsString();
            String id = obj.get("id").getAsString();
            String url = obj.get("images").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            Event event = new Event(name, id, url);
            events.add(event);
          //  System.out.println("shahnaz ===> " + i + obj);

        }
        placeholderImage.setVisibility(View.INVISIBLE);
        eventAdapter.notifyDataSetChanged();
        // System.out.println(eventTextBox.getText() + " " + radiusTextBox.getText());
    }
    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String ... uri)
        {
            try {
                URL url = new URL(uri[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();

                } finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e)
            {
                System.out.println("mehri" + e.getMessage());
            }

            return "Done";
        }

        //Type 2
        public void onProgressUpdate(Integer ... args)
        {

        }
        //Type3
        public void onPostExecute(String fromDoInBackground)
        {

        }
    }
    private class ListAdapter extends BaseAdapter {
        public int getCount(){
            return events.size();
        }

        public Event getItem(int position) {
            return events.get(position);
        }

        //3-return the database ID of the element at the given index of position
        public long getItemId(int position) {
            return getItem(position).id;
        }

        public View getView(int position, View old, ViewGroup parent){

            LayoutInflater inflater = getLayoutInflater(); //we need a LayoutInflater object to load an XML layout file
            //load an XML layout file, make a new row
            //newView is now the root object from your XML file, It contains the widgets that are in your layout
            View newView = inflater.inflate(R.layout.row_layout, parent,false);

            LinearLayout rowLayout = newView.findViewById(R.id.layoutContainer);

            ImageView iView = newView.findViewById(R.id.eventImg);
            TextView tView = newView.findViewById(R.id.eventDetails);
            Picasso.get().load(getItem(position).imageUrl).into(iView);
            tView.setText(getItem(position).name);

            return newView;   //return the inflated view
        }
    }

    class Event {
        String name;
        String _id;
        String imageUrl;
        long id;

        Event(String name, String id, String imageUrl){
            this.name = name;
            this._id = id;
            this.imageUrl = imageUrl;
        }
    }
}