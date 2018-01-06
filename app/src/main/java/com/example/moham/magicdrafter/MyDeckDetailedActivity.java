package com.example.moham.magicdrafter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyDeckDetailedActivity extends Activity {

    Button btnSave;
    EditText edtName;
    EditText edtdeckType;
    EditText edtdeckDescription;

    //My Activity Variables
    String name ;
    String deckType ;
    String deckDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deck_detailed);

        btnSave = findViewById(R.id.btn_save);
        edtName = findViewById(R.id.edtxt_name);
        edtdeckType = findViewById(R.id.edtxt_deckType);
        edtdeckDescription = findViewById(R.id.edtxt_deckDescription);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        deckType = i.getStringExtra("deckType");
        deckDesc = i.getStringExtra("deckDesc");

        edtName.setText(name);
        edtdeckType.setText(deckType);
        edtdeckDescription.setText(deckDesc);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyDeckActivity.class);
                if(edtName.getText().toString().isEmpty())
                {
                    intent.putExtra("name",name);
                }
                else{
                    intent.putExtra("name",edtName.getText().toString());
                }
                intent.putExtra("deckType",edtdeckType.getText().toString());
                intent.putExtra("deckDesc",edtdeckDescription.getText().toString());

                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save card info.
        outState.putString("name", name);
        outState.putString("deckType", deckType);
        outState.putString("deckDesc", deckDesc);

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Load card info.
        name = savedInstanceState.getString("name");
        deckType = savedInstanceState.getString("deckType");
        deckDesc = savedInstanceState.getString("deckDesc");

    }
}
