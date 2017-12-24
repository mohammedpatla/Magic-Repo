package com.example.moham.magicdrafter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    // TEMPORARY button!
    Button btnTempGoToSealed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TEMPORARY listener
        btnTempGoToSealed = findViewById(R.id.btn_temp_sealed);
        btnTempGoToSealed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempgotosealed();
            }
        });
    }

    // This method is TEMPORARY! For testing purposes, it exists to go to the sealed activity.
    public void tempgotosealed()
    {
        Intent intent = new Intent(getApplicationContext(), SealedActivity.class);

        startActivity(intent);
    }
}
