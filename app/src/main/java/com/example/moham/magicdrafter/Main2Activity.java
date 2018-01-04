package com.example.moham.magicdrafter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class Main2Activity extends Activity {

    private ListView menuDrawerList;
    private ArrayAdapter<String> menuAdapter;
    private ImageButton menuButton;

    private ActionBarDrawerToggle menuDrawerToggle;
    private DrawerLayout menuDrawerLayout;
    private String menuActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        menuDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();


        menuDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        tempgotosealed();
                        break;
                    case 1:
                        tempgotodraft();
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
                Toast.makeText(Main2Activity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });

        //menuButton = (ImageButton)findViewById(R.id.btn_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //only works when extends AppCompat
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //menuDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //menuActivityTitle = getTitle().toString();




    }


    // Setup Drawer on Click of Action Bar only works with AppCompat
    /*private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            *//** Called when a drawer has settled in a completely open state. *//*
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            *//** Called when a drawer has settled in a completely closed state. *//*
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }*/



    private void addDrawerItems(){
        String[] arrayList = { "Sealed Simulator", "Draft Simulator", "Deck Builder", "Settings"};
        menuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        menuDrawerList.setAdapter(menuAdapter);
    }

    public void tempgotosealed()
    {
        Intent intent = new Intent(getApplicationContext(), SealedActivity.class);

        startActivity(intent);
    }
    // This method is TEMPORARY! For testing purposes, it exists to go to the draft activity.
    public void tempgotodraft()
    {
        Intent intent = new Intent(getApplicationContext(), DraftActivity.class);

        startActivity(intent);
    }
}
