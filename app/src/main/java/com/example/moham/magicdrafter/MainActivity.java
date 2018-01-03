package com.example.moham.magicdrafter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

    // TEMPORARY buttons!
    Button btnTempGoToSealed;
    Button btnTempGoToDraft;
    Button btnTempGotoMenu;

    //Navigation Menu Memebers
    //private ListView nDrawerList;
    //private ArrayAdapter<String> nAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // TEMPORARY listeners
        btnTempGoToSealed = findViewById(R.id.btn_temp_sealed);
        btnTempGoToSealed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempgotosealed();
            }
        });
        btnTempGoToDraft = findViewById(R.id.btn_temp_draft);
        btnTempGoToDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempgotodraft();
            }
        });

        //Temporary calls Navigation Drawr Activity
        btnTempGotoMenu = findViewById(R.id.btn_menu);
        btnTempGotoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temptomenu();
            }
        });

    }

    public void temptomenu()
    {
        Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
        startActivity(i);
    }

    // This method is TEMPORARY! For testing purposes, it exists to go to the sealed activity.
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
