package com.example.moham.magicdrafter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.moham.magicdrafter.Model.Card;
import com.example.moham.magicdrafter.Model.Deck;
import com.example.moham.magicdrafter.Model.DeckAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/* DraftActivity - Mohammed Patla - 1/2/2018
"The user, upon moving to this activity, will see his/hers list of already completed decks"

   This activity uses mechanisms from the SealedActivity which passes card data between the two.
    it shows users a list view with all there previous created decks.
    They can open there Packs to modify it(Calls SealedActivity) as well as change it if the deck has not yet been completed.

    It Has its own Deck Array List which holds all the decks created by the user. It displays them in the list view.
    It also reads and writes Data to a custom JSON file.

Last Modified: 1/3/2018
 */

public class MyDeckActivity extends Activity  {

    // Recommended cards in a drafted/sealed deck.
    private static final String SEALED_DECK_NUM = "/40";
    private static final int SECOND_ACTIVITY =2;
    private static final int FIRST_ACTIVITY =3;


    //UX Elements
    ListView lst_decks;

    //Array of Data
    ArrayList<Deck> decks = new ArrayList<Deck>();
    // Card pools that are opened and selected.
    ArrayList<Card> openedCardPool = new ArrayList<>();
    ArrayList<Card> selectedCardPool= new ArrayList<>();

    //Read from File
    String fileName = "mydecks.json";

    //My  Global Refrences for detailed Activity on Results Intent
    Deck selectedeck;
    int positionOfItem;
    String name,deckType,deckDesc;

    //Total Decks allowed
    int numberOfDecks;
    //SharedPrefrence myPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deck);

        //Loads total number Of decks from Prefrences
        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);
        numberOfDecks = myPrefs.getInt("numberOfDecks",0);

        lst_decks = findViewById(R.id.lst_deck);

        readAlldecksFromFile();
        loadMyDecks();
        initialize();
        //loadMyDecks(findViewById(R.layout.activity_my_deck));

        lst_decks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedeck = (Deck) parent.getAdapter().getItem(position);
                name = selectedeck.getDeckName();
                deckType=selectedeck.getDecktype();
                deckDesc=selectedeck.getDeckdesc();
                positionOfItem =position;
                Intent intent = new Intent(getApplicationContext(), MyDeckDetailedActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("deckType",deckType);
                intent.putExtra("deckDesc",deckDesc);

                startActivityForResult(intent,SECOND_ACTIVITY);
                return true;
            }
        });

        lst_decks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedeck = (Deck) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), SealedActivity.class);
                // Must pass lists of cards so that the app remembers them when moving to new activities.
                // Create a bundle, since there is two lists of cards being passed in, not a single card.
                Bundle bundle = new Bundle();
                // Store card lists.
                bundle.putInt("deckId",selectedeck.getDeckid());
                bundle.putParcelableArrayList("openedCardPool", selectedeck.getOpenedCardPool());
                bundle.putParcelableArrayList("selectedCardPool", selectedeck.getSelectedCardPool());
                // Put bundle of card lists into intent.
                intent.putExtras(bundle);

                // Put a boolean into the intent. This will tell MyDeck(Activity) that the Intent is from SealedActivity.
                intent.putExtra("cardPoolIntent", true);

                // Start activity with the intent.
                startActivity(intent);
                //startActivityForResult(intent,FIRST_ACTIVITY);
            }
        });


    }

    // Store card info so new cards are not generated when the activity is compromised by say, a rotation of the device.
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save card info.
        outState.putParcelableArrayList("openedCardPool", openedCardPool);
        outState.putParcelableArrayList("selectedCardPool", selectedCardPool);

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Load card info.
        openedCardPool = savedInstanceState.getParcelableArrayList("openedCardPool");
        selectedCardPool = savedInstanceState.getParcelableArrayList("selectedCardPool");

        loadMyDecks();
    }

    @Override
    protected void onPause() {
        Log.e(TAG,"In onPause()");

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG,"In onStop");
        super.onStop();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"In onResume");

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"In onDestroy");
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SECOND_ACTIVITY){
            if(resultCode==RESULT_OK){
                name = (String) data.getStringExtra("name");
                deckType = (String) data.getStringExtra("deckType");
                deckDesc = (String) data.getStringExtra("deckDesc");;
            }else{
                //Code should not reach here

            }
            selectedeck.setDeckName(name);
            selectedeck.setDecktype(deckType);
            selectedeck.setDeckdesc(deckDesc);

            decks.set(positionOfItem,selectedeck);
            writeToFile();
            readAlldecksFromFile();
            loadMyDecks();
        }
        else if(requestCode== FIRST_ACTIVITY){
            if(resultCode==RESULT_OK){

                //Does not reach here For future Activities

            }
        }
    }

    protected void initialize() {

        // Check for Intent with possibly loaded card pools in it from another Activity.
        Intent intent = getIntent();
        if(intent != null)
        {
            if(intent.getBooleanExtra("cardPoolIntent", false))
            {
                // Store card pools loaded in from elsewhere.
                Bundle bundle = intent.getExtras();
                int deckId;
                boolean thisDeckExist=true;
                deckId = bundle.getInt("deckId");
                if(deckId == 0)
                {
                    thisDeckExist=false;

                    //reinitialize deckID
                    deckId = 1+numberOfDecks++;

                    //Saves Prefrences of total number of Decks
                    SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putInt("numberOfDecks",numberOfDecks).apply();
                }
                openedCardPool = bundle.getParcelableArrayList("openedCardPool");
                selectedCardPool = bundle.getParcelableArrayList("selectedCardPool");

                //create a new Deck and add it to the Arrays of deck
                Deck tempdeck = createDeck(deckId,selectedCardPool,openedCardPool);
                //decks.clear();
                if (!thisDeckExist)
                {
                    if(decks.size() >= 5)
                    {
                        Toast.makeText(this,"Maximum amount of Decks reached",Toast.LENGTH_LONG).show();
                    }
                    else {
                        decks.add(tempdeck);
                    }
                    //numberOfDecks++;
                }
                else {
                    for(int i =0 ; i <decks.size();i++){
                        Deck deck = decks.get(i);
                        if(deck.getDeckid() == tempdeck.getDeckid())
                        {
                            decks.set(i,tempdeck);
                        }
                        else{

                        }
                    }
                }



                writeToFile();
                loadMyDecks();
            }
            else
            {
                //Have empty Arrays with no cards
            }
        }
        else
        {
            //Have empty arrays with no cards in it.
        }
    }

    public  void writeToFile(){
        //get refrence to apps resources
        Resources res = getResources();

        //Create a JSON object
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i < decks.size(); i++) {
            jsonArray.put(decks.get(i).getJSONObject());
        }
        JSONObject obj = new JSONObject();
        try {
            //obj.put("numberOfDecksEverCreated",numberOfDecksEverCreated);
            obj.put("MyDecks",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Write to JSON file
        FileOutputStream fos = null;
        //Not appending to File as we are just saving all decks
        boolean append = false;
        try {
            // get the FileOutputStream
            fos = openFileOutput(fileName, append ? MODE_APPEND : MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            // code if reaches here, System should create a new file automatically
            Log.e(TAG, "initialize: File Not Found Exception",e);
        }

        // Here we are concanitaing alll data into a string
        String block = null;
        try {
            block = obj.getString("MyDecks");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // create a comma-delimited string for the file
        //String toFile = String.format("%s", block);

        // wrap a PrintWriter around FileOutputStream for
        // convenience of println
        PrintWriter writer = new PrintWriter(fos);
        // print the line
        writer.println(block);
        // Close the PrintWriter, which will also close fos
        writer.close();

    }

    public  void readAlldecksFromFile(){
        // declare input stream and files we'll use
        FileInputStream fis;
        // Get the FileInputStream
        try {
            fis = openFileInput(fileName);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "loadMyDecks: File Not Found Exception",e);
            return;
        }
        // Call our readFile method passing in FileInputStream
        Scanner scanner;
        // use a StringBuilder to concatenate text
        StringBuilder builder = new StringBuilder();
        scanner = new Scanner(fis);
        // Keep reading the file as long as there's data
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine()).append("\n");
        }
        //Call JSON builder and get the Decks from FIle
        parseJSON(builder.toString());
        // close the Scanner which will also close fis
        scanner.close();

    }

    //loads the deck adapter to list view
    public void loadMyDecks()
    {
        //Attaches adapter to list view
        if(decks !=null) {
            DeckAdapter adapter = new DeckAdapter(this, R.layout.list_decks, decks);
            lst_decks.setAdapter(adapter);
        }
        else {
            Toast.makeText(this,"Does not display decks as deck is empty",Toast.LENGTH_SHORT).show();
        }

    }

    //creating JSON String to read
    private void parseJSON(String jsonString) {

        openedCardPool.clear();
        selectedCardPool.clear();
        decks.clear();
        try {
            JSONArray root = new JSONArray(jsonString);
            //JSONObject mydecks = root.getJSONObject("MyDecks");

            //JSONArray allsaveddecks = root.getJSONArray("MyDecks");
            for (int i = 0; i < root.length(); i++) {
                JSONObject deckJSON = root.getJSONObject(i);

                JSONArray sealedcardpoolarray = deckJSON.getJSONArray("selectedCardPool");
                for (int j = 0; j < sealedcardpoolarray.length(); j++) {
                    JSONObject cardJSON = sealedcardpoolarray.getJSONObject(j);

                    Card cardset = new Card();
                    cardset.setId(cardJSON.getInt("id"));
                    cardset.setCost(cardJSON.getInt("cost"));
                    cardset.setRarity(cardJSON.getString("rarity").charAt(0));
                    cardset.setType(cardJSON.getString("type").charAt(0));
                    cardset.setColor(cardJSON.getString("color").charAt(0));
                    cardset.setFlip(cardJSON.getBoolean("flip"));
                    if(cardJSON.getBoolean("foil")) {
                        cardset.setFoilToTrue();
                    }
                    selectedCardPool.add(cardset);
                }

                JSONArray openedcardpoolarray = deckJSON.getJSONArray("openedCardPool");
                for (int j = 0; j < openedcardpoolarray.length(); j++) {
                    JSONObject cardJSON = openedcardpoolarray.getJSONObject(j);

                    Card cardset = new Card();
                    cardset.setId(cardJSON.getInt("id"));
                    cardset.setCost(cardJSON.getInt("cost"));
                    cardset.setRarity(cardJSON.getString("rarity").charAt(0));
                    cardset.setType(cardJSON.getString("type").charAt(0));
                    cardset.setColor(cardJSON.getString("color").charAt(0));
                    cardset.setFlip(cardJSON.getBoolean("flip"));
                    if(cardJSON.getBoolean("foil")) {
                        cardset.setFoilToTrue();
                    }
                    openedCardPool.add(cardset);
                }


                Deck deckset = new Deck(Integer.parseInt(deckJSON.getString("deckId")), deckJSON.getString("deckName"), Integer.parseInt(deckJSON.getString("nocards")), deckJSON.getString("decktype"), deckJSON.getString("deckdesc"), selectedCardPool, openedCardPool);

                decks.add(deckset);

            }
        }catch (JSONException e){
            Log.e(TAG, "parseJSON: Parsing JSON error",e );
        }
    }

    public Deck createDeck(int id,ArrayList<Card> openedCardPool,ArrayList<Card> selectedCardPool){

        Deck tempdeck = new Deck(id, openedCardPool, selectedCardPool);

        return tempdeck;
    }

}
