package com.example.moham.magicdrafter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.moham.magicdrafter.Model.Card;
import com.example.moham.magicdrafter.Model.Deck;
import com.example.moham.magicdrafter.Model.DeckAdapter;

import java.util.ArrayList;

/* DraftActivity - Mohammed Patla - 1/2/2018
"The user, upon moving to this activity, will see his/hers list of already completed decks"

   This activity uses mechanisms from the SealedActivity to view and sort cards being drafted.
    (Inherited through SimulatorActivity) However, it shows users a list view with all there previous created decks.
    They can open there Packs to modify it as well as change it if the deck has not yet been completed.

Last Modified: 1/3/2018
 */
public class MyDeck extends Activity  {

    // Recommended cards in a drafted/sealed deck.
    private static final String SEALED_DECK_NUM = "/40";

    //UX Elements
    ListView lst_decks;

    //Array of Data
    ArrayList<Deck> decks = Deck.getDeck();
    // Card pools that are opened and selected.
    ArrayList<Card> openedCardPool;
    ArrayList<Card> selectedCardPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deck);

        lst_decks = findViewById(R.id.lst_deck);

        initialize();
        //loadMyDecks(findViewById(R.layout.activity_my_deck));

    }

    protected void initialize() {

        // Check for Intent with possibly loaded card pools in it from another Activity.
        // If card pools are found, do NOT generateNewCards(). Use what is saved.
        Intent intent = getIntent();
        if(intent != null)
        {
            if(intent.getBooleanExtra("cardPoolIntent", false))
            {
                // Store card pools loaded in from elsewhere.
                Bundle bundle = intent.getExtras();
                openedCardPool = bundle.getParcelableArrayList("openedCardPool");
                selectedCardPool = bundle.getParcelableArrayList("selectedCardPool");

                // Update card counter button according to passed in selected card pool (deck).
                //btnCardsInDeck.setText(selectedCardPool.size() + SEALED_DECK_NUM);
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

    public void loadMyDecks(View view)
    {
        DeckAdapter adapter = new DeckAdapter(this,R.layout.list_decks,decks);
        lst_decks.setAdapter(adapter);

    }
}
