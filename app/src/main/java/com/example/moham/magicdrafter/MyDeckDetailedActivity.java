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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deck_detailed);

        btnSave = findViewById(R.id.btn_save);
        edtName = findViewById(R.id.edtxt_name);
        edtdeckType = findViewById(R.id.edtxt_deckType);
        edtdeckDescription = findViewById(R.id.edtxt_deckDescription);

        Intent i = getIntent();
        final String name = i.getStringExtra("name");
        final String deckType = i.getStringExtra("deckType");
        final String deckDesc = i.getStringExtra("deckDesc");

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
}
