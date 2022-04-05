
package com.cst2335.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;


public class Details extends Fragment {
    Event e;
    TextView title , date, price ;
    Button saveBtn , viewBtn ;
    ImageView headImg;
    ProgressBar spinner;

    public static final String ID = "ID";

    private String id;

    public Details() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getString(ID);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        getActivity().setTitle("detail-Afsaneh Khabbazibasmenj-VB");
        View newView = inflater.inflate(R.layout.fragment_details, container, false);

        // move oncreate
        spinner = newView.findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        title = (TextView)newView.findViewById(R.id.title);
        headImg = (ImageView) newView.findViewById(R.id.imageView3);
        date= (TextView)newView.findViewById(R.id.date);
        saveBtn = (Button)newView.findViewById(R.id.button3);
        price = newView.findViewById(R.id.price);


        // call http get request
        String result = null;
        if(id != null) {
            String url = "https://app.ticketmaster.com/discovery/v2/events/" + id + ".json?apikey=s5ymFAVDKGupqsgEGeBHHycyM4sP1Plr";

            try {
                result = new RequestTask()
                        .execute(url)
                        .get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        if(result == null) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences( "SEARCH_DETAIL" , Context.MODE_PRIVATE);
            String lastSearchedEvent = prefs.getString("lastSearchedEvent", "");
            result = lastSearchedEvent;
        }

        Gson gson = new Gson();
        JsonObject eventObject = gson.fromJson(result, JsonObject.class);
        String name = eventObject.get("name").getAsString();
        String linkUrl = eventObject.get("url").getAsString();
        JsonObject priceRange = eventObject.get("priceRanges").getAsJsonArray().get(0).getAsJsonObject();

        String currency = priceRange.get("currency").getAsString();
        String minPrice  = priceRange.get("min").getAsString();
        String maxPrice = priceRange.get("max").getAsString();

        String formattedPrice = "From " + minPrice + " " + currency + " To: " + maxPrice;
        price.setText(formattedPrice);

        String lastSearchedEvent = eventObject.toString();
        //  String dateString = eventObject.get("start").getAsString();

        //  date.setText(dateString);
        title.setText(name);

        String imgUrl = eventObject.get("images").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();

        Picasso.get().load(imgUrl).into(headImg);


        viewBtn = (Button)newView.findViewById(R.id.button2);
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                getActivity().startActivity(intent);
            }
        });

        saveLastestEvent(lastSearchedEvent);


        return newView;
    }

    private void saveLastestEvent(String lastSearchedEvent) {
        SharedPreferences prefs = getActivity().getSharedPreferences( "SEARCH_DETAIL" , Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("lastSearchedEvent", lastSearchedEvent);

        edit.commit();
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


        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
        }

        //Type 2
        public void onProgressUpdate(Integer ... args)
        {
            spinner.setVisibility(View.VISIBLE);
        }
        //Type3
        public void onPostExecute(String fromDoInBackground)
        {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    spinner.setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    public class Event {
        private   String ind ;
        private String name;
        private String url;
        private String img_link;
        private String date;

        public Event() {

        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public String getImg_link() {
            return img_link;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setImg_link(String img_link) {
            this.img_link = img_link;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return ind;
        }

        public void setId(String ind) {
            this.ind = ind;
        }
    }

}