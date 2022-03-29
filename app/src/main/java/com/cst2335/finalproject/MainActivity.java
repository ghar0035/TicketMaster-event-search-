package com.cst2335.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSearch = findViewById(R.id.btnSearch);

        // Render toolbar
        renderToolbar();
        // Add search event
        btnSearch.setOnClickListener(click -> {
            searchNearByEvents();
        });

    }

    private void searchNearByEvents() {

        EditText eventTextBox = findViewById(R.id.eventTextBox);
        EditText radiusTextBox = findViewById(R.id.radiusTextBox);
        String result = null;
        try {
            result = new RequestTask()
                    .execute("https://app.ticketmaster.com/discovery/v2/events.json?apikey=82OhVMuQzr9Lf0Jnp9Zcvhqs1HkBQBAC&city=ottawa&radius=100")
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Gson gson = new Gson();
        JsonObject eventObject = gson.fromJson(result, JsonObject.class);
        JsonObject initialResponse = eventObject.get("_embedded").getAsJsonObject();
        JsonArray eventsArray = initialResponse.get("events").getAsJsonArray();
        System.out.println("shahnaz ===> " + eventsArray );
       // System.out.println(eventTextBox.getText() + " " + radiusTextBox.getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // render toolbar menu items
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void renderToolbar() {
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        getSupportActionBar().setTitle("Home - Mehri Gh - v1");
        // Add navigation drawer
        renderDrawerMenu(toolbar);
    }

    private void renderDrawerMenu(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    public boolean onNavigationItemSelected(MenuItem item) {
        String message = null;
        switch(item.getItemId())
        {
            case R.id.home:
                Intent goToHome = new Intent(MainActivity.this , MainActivity.class);
                startActivity(goToHome);
                break;
            case R.id.savedEvents:
                Intent goToSaveEvents = new Intent(MainActivity.this, MainActivity.class);
                startActivity(goToSaveEvents);
                break;
            case R.id.lastSearchedEvent:
                Intent goToLastSearchedItem = new Intent(MainActivity.this,  MainActivity.class);
                startActivity(goToLastSearchedItem);
                finish();
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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


    class Event {
      String name;
      String id;
      String imageUrl;
      public String _embedded;
      public String events;

      Event(String name, String id, String imageUrl){
          this.name = name;
          this.id = id;
          this.imageUrl = imageUrl;
      }
    }
}

