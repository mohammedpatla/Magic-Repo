package com.example.moham.magicdrafter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

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
Last Modified: 1/4/2018
 */

public class SealedActivity extends SimulatorActivity
{
    // Recommended cards in a drafted/sealed deck.
    private static final String SEALED_DECK_NUM = "/40";

    // Add basic lands activity constant.
    public static final int ADD_BASICS_ACTIVITY = 2;

    // Number of packs in the sealed format.
    public static final int SEALED_PACKS = 6;

    // Views of Activity.
    Button btnAddBasics;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize views.
        btnAddBasics = findViewById(R.id.btn_add_basics);
        btnSave = findViewById(R.id.btn_save);

        // Set a listener for the GridView so that when a card is tapped on, it is removed from the current list being shown and added to the other.
        // This could be from the pool(opened) to the deck(selected), or vice versa.
        grdCardView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Only do the following if the user isn't currently zoomed in on a card.
                if(!cardExpanded)
                {
                    // A card was selected. Add it to the list not being shown currently, and remove it from the one shown.
                    if (openedCardsPoolShown)
                    {
                        selectedCardPool.add(openedCardPool.get(position));
                        openedCardPool.remove(position);
                    } else
                        {
                        // Only add card to openedCardPool if it is not a basic land. (ID over LAND_ID_START.)
                        if (selectedCardPool.get(position).getId() < LAND_ID_START)
                        {
                            openedCardPool.add(selectedCardPool.get(position));
                        }
                        selectedCardPool.remove(position);
                    }

                    // Then, refresh the GridView with shown list of cards.
                    // Use notifyDataSetChanged() to refresh so that the list won't scroll back to the top. Also saves resources. (Over generating the adapter again.)
                    // Also update button text on btnCardsInDeck to reflect number of cards selected.
                    ((CardAdapter) grdCardView.getAdapter()).notifyDataSetChanged();
                    btnCardsInDeck.setText(selectedCardPool.size() + SEALED_DECK_NUM);
                }
            }
        });

        // Set a listener for adding basic lands to the current deck. This will open the AddBasicsActivity activity. There, users will choose the lands they want added before returning via intents.
        btnAddBasics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBasics();
            }
        });

        // Create a listener for the save button. After being pressed, a dialog will come up to make sure the user would like to save.
        // Then, they will be sent to the appropriate activity. Either a user can save their progress, or their finished deck.
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SealedActivity.this, "TEMP TOAST: SAVING", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Most of restore is dealt with by the SimulatorActivity inherited part.
        // Just need to refresh selection counter.
        btnCardsInDeck.setText(String.valueOf(selectedCardPool.size()) + SEALED_DECK_NUM);
    }

    // This method is called if new cards need to be generated. If cards are passed in via Intent, this is not needed.
    private void generateNewCards()
    {
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

        // Initialize a selectedCardPool that is empty, for the user to move cards into when desired.
        selectedCardPool = new ArrayList<>();
    }

    // Open basic land adding activity, where user selects lands to add to their deck. Using intents, data will be transferred between activities regarding current cards in the opened and selected pools.
    private void addBasics()
    {
        Intent intent = new Intent(getApplicationContext(), AddBasicsActivity.class);
        // Must pass lists of cards so that the app remembers them when moving to new activities.
        // If not remembered, SealedActivity will generate a new card pool...
        // Create a bundle, since there is two lists of cards being passed in, not a single card.
        Bundle bundle = new Bundle();
        // Store card lists.
        bundle.putParcelableArrayList("openedCardPool", openedCardPool);
        bundle.putParcelableArrayList("selectedCardPool", selectedCardPool);
        // Put bundle of card lists into intent.
        intent.putExtras(bundle);

        // Put a boolean into the intent. This will tell AddBasicsActivity that the Intent is from SealedActivity.
        intent.putExtra("cardPoolIntent", true);

        // Start activity with the intent.
        startActivityForResult(intent, ADD_BASICS_ACTIVITY);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.activity_sealed;
    }

    @Override
    protected Context getSimContext()
    {
        return SealedActivity.this;
    }

    @Override
    protected void initialCardGeneration() {
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
                btnCardsInDeck.setText(selectedCardPool.size() + SEALED_DECK_NUM);
            }
            else
            {
                // Generate new cards according to Sealed rules, using the database. (6 boosters.)
                generateNewCards();
            }
        }
        else
        {
            // Generate new cards according to Sealed rules, using the database. (6 boosters.)
            generateNewCards();
        }
    }
}
