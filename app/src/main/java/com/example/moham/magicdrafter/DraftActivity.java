package com.example.moham.magicdrafter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.moham.magicdrafter.Model.Card;
import com.example.moham.magicdrafter.Model.CardAdapter;
import com.example.moham.magicdrafter.Model.CardDatabase;
import com.example.moham.magicdrafter.Model.CardPackGenerator;

import java.util.ArrayList;


/* DraftActivity - Brigham Moll - 1/1/2018
"The user, upon moving to this activity, will open a single pack in a simulator that looks similar
to the sealed simulator screen. However, they will only be able to choose a single card from the
pack’s contents that are shown before the remaining cards are passed to one of the AIs playing with
 the player. The number of AIs can be adjusted later, but defaults to 7. Each of the AIs do the same
  as the player, opening their packs, taking a single card, and passing the packs to the next AI or
  player in a set order. This continues until no cards remain in the initial pack, then the process
   starts again for two more packs. When all packs are completely used up, the user will be sent to
   the sealed simulator’s deck builder with an intent carrying the cards they selected in the draft
   process. They will immediately be able to build a deck using those cards."

   This activity uses mechanisms from the SealedActivity to view and sort cards being drafted.
    (Inherited through SimulatorActivity) However,
   each time a card is selected and put into the selected pool, a new pool of cards is shown. (The cards
   passed from an AI.) Eventually the cards will run out and a new pack will be opened again. This will occur 3 times with
   a total of 3 packs. Afterwords, the pool of selected cards will be stored in an intent and be passed over to
   the SealedActivity for deck building and eventual saving.
Last Modified: 1/1/2018
 */

public class DraftActivity extends SimulatorActivity
{
    // Views of Activity.
    TextView txtPackNum;

    // Total cards to be drafted.
    private static final String TOTAL_TO_BE_DRAFTED = "/42";

    // Number of packs in the sealed format.
    public static final int SEALED_PACKS = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
                    btnCardsInDeck.setText(selectedCardPool.size() + TOTAL_TO_BE_DRAFTED);
                }
            }
        });

        // Initialize views.
        txtPackNum = findViewById(R.id.txt_pack_num);
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

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.activity_draft;
    }

    @Override
    protected Context getSimContext() {
        return DraftActivity.this;
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
                btnCardsInDeck.setText(selectedCardPool.size() + TOTAL_TO_BE_DRAFTED);
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
