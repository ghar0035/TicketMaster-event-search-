/*
 * Course: 22W-CST2335-013
 * Professor: Frank Emanuel
 * Author: Mehri Gharacheh
 * student# 041005509
 * File name: MainActivity.java
 * Date: 2022-04-08
 * Final Project
 */
package com.cst2335.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.ListAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *this ins our main and first activity which contains the toolbar , the toolbarMenu, the drawerBar and the drawer menu
 * Author: Mehri Gharacheh
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    /**
     * overrides the AppCompatActivity onCreate() menu
     *
     * @param savedInstanceState : is a reference to a Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        renderToolbar(); // Render toolbar
        setDefaultfragment();
    }

    /**
     * sets the default fragment(Home) if it is not exist
     */
    private void setDefaultfragment() {
        Home homeFragment = new Home();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, homeFragment, "HOME")
                .commit();
    }

    /**
     *
     * render toolbar menu items
     *
     * @param menu an object of Menu class
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Create and renders the activity toolbar
     */
    private void renderToolbar() {
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar); //causes Android to call onCreateOptionsMenu(Menu menu)
        toolbar.bringToFront(); //brings toolbar layout on top of other layouts

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.background_dark));
        toolbar.getOverflowIcon().setTint(getResources().getColor(android.R.color.background_dark));
        // Add navigation drawer
        renderDrawerMenu(toolbar);
    }

    /**
     * Create and renders the DrawerMenu
     * @param toolbar
     */
    private void renderDrawerMenu(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     *this method used to handling the event of option menu
     * i.e. which menu action is triggered and what should be the outcome of that action etc
     *
     * @param item toolbar menu items
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SavedEvents savedFragment = new SavedEvents();
        Details detailFragment = new Details();
        Home homeFragment = new Home();
        String message = null;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();// begin  FragmentTransaction
        ft.setReorderingAllowed(true);
        switch (item.getItemId()) {
            case R.id.help:
                List<Fragment> fragments = getSupportFragmentManager().getFragments();

                String tagName = fragments.get(0).getTag();
                if (tagName == "HOME") {
                    // add your code here
                    showHelpDialouge("The User enters the city name and the distance, " +
                            "then when clicks on the 'FIND' button, " +
                            "a list of nearby events will appears, then by click on each event, " +
                            "the user will see some details about the event!");
                } else if(tagName == "SAVED") {
                    showHelpDialouge("Here is a list of all the events saved by the user. " +
                            "The user can check delete saved events by clicking the DEL button.");
                } else if(tagName == "DETAIL") {
                    showHelpDialouge("User clicks on item, they can see the " +
                            "item result here! There is a link will redirect user " +
                            "to the event details. User can also save the event!");
                }
                break;
            case R.id.tool_home:
                ft.replace(R.id.frameLayout, homeFragment, "HOME");    // add    Fragment
                ft.commit();
                break;
            case R.id.tool_saved:
                ft.replace(R.id.frameLayout, savedFragment, "SAVED");    // add    Fragment
                ft.commit();
                break;
            case R.id.tool_lastSearched:
                ft.replace(R.id.frameLayout, detailFragment, "DETAIL");    // add    Fragment
                ft.commit();
                break;
        }
        return true;
    }

    /**
     * this method will create a diolog alert when the help icon is clicked
     *
     * @param helpText  the text to be showed when the help icon is clicked
     */
    public void showHelpDialouge(String helpText) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Help");
        alertBuilder.setMessage(helpText);
        alertBuilder.setCancelable(true);

        alertBuilder.setNegativeButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /**
     *this method used to handling the event of drawer menu
     * i.e. which menu action is triggered and what should be the outcome of that action etc
     *
     * @param item
     * @return
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        SavedEvents savedFragment = new SavedEvents();
        Details detailFragment = new Details();
        Home homeFragment = new Home();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();// begin  FragmentTransaction
        ft.setReorderingAllowed(true);

        String message = null;
        switch(item.getItemId())
        {
            case R.id.home:
                ft.replace(R.id.frameLayout, homeFragment, "HOME");    // add    Fragment
                ft.commit();
                break;
            case R.id.savedEvents:
                ft.replace(R.id.frameLayout, savedFragment, "SAVED");    // add    Fragment
                ft.commit();
                break;
            case R.id.lastSearchedEvent:
                ft.replace(R.id.frameLayout, detailFragment, "DETAIL");    // add    Fragment
                ft.commit();
              //  finish();
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * spesefies what should be the outcome of clicking on the back button
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }
}

