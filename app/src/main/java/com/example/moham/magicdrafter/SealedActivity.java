package com.example.moham.magicdrafter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;

import com.example.moham.magicdrafter.Model.Card;
import com.example.moham.magicdrafter.Model.CardAdapter;
import com.example.moham.magicdrafter.Model.CardDatabase;
import com.example.moham.magicdrafter.Model.CardPackGenerator;

import java.util.ArrayList;

/* SealedActivity - Brigham Moll - 12/24/2017
"The sealed simulator consists of opening six booster packs of ‘Magic: the Gathering’ cards and building a deck using only the cards that
were randomly selected. Users will be able to switch between what cards are in their pool of cards and what cards are in their current deck,
and will be able to sort cards as they build their typically, forty card deck. ‘Basic Land’ cards will be able to be added to the deck via a
button along the top of the screen. (These are cards that normally can be added to a deck whenever one wishes.) When finished, they will be
able to save the deck to view later on the load previous decks screen."
Last Modified: 12/26/2017
 */

public class SealedActivity extends Activity
{
    // Views of Activity.
    GridView grdCardView;
    Button btnColorSort;
    Button btnCostSort;
    Button btnTypeSort;
    Button btnAddBasics;
    Button btnCardsInDeck;

    // Number of packs in the sealed format.
    public static final int SEALED_PACKS = 6;

    // Card pools that are opened and selected.
    ArrayList<Card> openedCardPool;
    ArrayList<Card> selectedCardPool;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sealed);

        // Initialize views.
        grdCardView = findViewById(R.id.grd_card_view);
        btnColorSort = findViewById(R.id.btn_color_sort);
        btnCostSort = findViewById(R.id.btn_cost_sort);
        btnTypeSort = findViewById(R.id.btn_type_sort);
        btnAddBasics = findViewById(R.id.btn_add_basics);
        btnCardsInDeck = findViewById(R.id.btn_cards_in_deck);

        // Database of cards and their appropriate images... This is all built-in and pre-created in the assets folder.
        // Retrieve information from database and create the card pool to draw cards from.
        CardDatabase cardDb = new CardDatabase(this);
        // Store card pool here.
        ArrayList<Card> cardPool;

        cardPool = cardDb.getCardPool();

        // Make a card pack generator. This will use the cardPool passed in to make the packs that will be opened.
        CardPackGenerator packGenerator = new CardPackGenerator(cardPool);

        // Since this is a Sealed simulator, open 6 (SEALED_PACKS) packs.
        openedCardPool = packGenerator.generatePacks(SEALED_PACKS);

        // Use this openedCardPool to generate items in the GridView for each card.
        grdCardView.setAdapter(new CardAdapter(this, openedCardPool));

        // Initialize a selectedCardPool that is empty, for the user to move cards into when desired.
        selectedCardPool = new ArrayList<>();
    }
}
