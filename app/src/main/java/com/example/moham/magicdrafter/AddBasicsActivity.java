package com.example.moham.magicdrafter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.moham.magicdrafter.Model.Card;

import java.util.ArrayList;

/* AddBasicsActivity - Brigham Moll - 12/31/2017
In this activity, navigated to from the SealedActivity, the user will be able to select a number of land cards to add to their deck in the
types of Plains, Islands, Swamps, Mountains, or Forests. Using NumberPickers, they can select this amount. After selecting amounts,
using the Add Basic Lands button, they will return to the SealedActivity, using Intents to continue their deck building with the added land cards.
Last Modified: 1/1/2018
 */

public class AddBasicsActivity extends Activity
{
    // Views in this activity.
    NumberPicker pickPlains;
    NumberPicker pickIsland;
    NumberPicker pickSwamp;
    NumberPicker pickMountain;
    NumberPicker pickForest;
    Button btnAddBasics;

    // The maximum of a land type one can add. Used in NumberPickers for maximum.
    public static final int MAX_LAND_ADD = 35;

    // Max cards a user can have in their pools of cards combined. (Too many would cause lag, or a crash of the application.)
    public static final int MAX_CARDS_IN_POOLS = 120;

    // Constant for sealed activity. Used with activity navigation.
    public static final int SEALED_ACTIVITY = 3;

    // Store passed in Card lists from SealedActivity. These will be passed back later with lands added.
    ArrayList<Card> openedCardPool;
    ArrayList<Card> selectedCardPool;

    // Basic Land type constants. Used to make different basic land objects.
    // Also puts basics at the end when sorted by color.
    public static final int PLAINS = 501;
    public static final int ISLAND = 502;
    public static final int SWAMP = 503;
    public static final int MOUNTAIN = 504;
    public static final int FOREST = 505;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_basics);

        // Get references to views.
        pickPlains = findViewById(R.id.pick_plains);
        pickIsland = findViewById(R.id.pick_island);
        pickSwamp = findViewById(R.id.pick_swamp);
        pickMountain = findViewById(R.id.pick_mountain);
        pickForest = findViewById(R.id.pick_forest);
        btnAddBasics = findViewById(R.id.btn_add_basic_lands);

        // Set picker maximums.
        pickPlains.setMaxValue(MAX_LAND_ADD);
        pickIsland.setMaxValue(MAX_LAND_ADD);
        pickSwamp.setMaxValue(MAX_LAND_ADD);
        pickMountain.setMaxValue(MAX_LAND_ADD);
        pickForest.setMaxValue(MAX_LAND_ADD);

        // Set listener for button for when user is finished making selections.
        btnAddBasics.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Add lands to selectedCardPool.
                boolean successfulAdd = addBasicLands();
                if(successfulAdd)
                {
                    // Return to SealedActivity with card pools in Intent.
                    returnToDeck();
                }
            }
        });

        // Retrieve card lists from SealedActivity.
        Intent intent = getIntent();
        if(intent != null)
        {
            // Check boolean to see if card pools are in this Intent.
            if(intent.getBooleanExtra("cardPoolIntent", false))
            {
                // Store card pools.
                Bundle bundle = intent.getExtras();
                openedCardPool = bundle.getParcelableArrayList("openedCardPool");
                selectedCardPool = bundle.getParcelableArrayList("selectedCardPool");
            }
        }
    }

    // This method will add the basic land cards that were requested to the current deck.
    private boolean addBasicLands()
    {
        // Create appropriate Card objects for each of the basic lands being added.
        // Get values from NumberPickers.
        int plainsToBeAdded = pickPlains.getValue();
        int islandToBeAdded = pickIsland.getValue();
        int swampToBeAdded = pickSwamp.getValue();
        int mountainToBeAdded = pickMountain.getValue();
        int forestToBeAdded = pickForest.getValue();

        // Make sure user is not adding too many lands! Otherwise app could crash, etc.
        int totalCardsInPools = openedCardPool.size() + selectedCardPool.size();
        int totalLandsToBeAdded = plainsToBeAdded+islandToBeAdded+swampToBeAdded+mountainToBeAdded+forestToBeAdded;

        if((totalCardsInPools + totalLandsToBeAdded) <= MAX_CARDS_IN_POOLS) {

            // Generate and add lands of each type.
            // Add plains cards.
            for (int iPlains = 0; iPlains < plainsToBeAdded; iPlains++) {
                Card newLandCard = new Card(PLAINS);
                selectedCardPool.add(newLandCard);
            }
            // Add island cards.
            for (int iIsland = 0; iIsland < islandToBeAdded; iIsland++) {
                Card newLandCard = new Card(ISLAND);
                selectedCardPool.add(newLandCard);
            }
            // Add swamp cards.
            for (int iSwamp = 0; iSwamp < swampToBeAdded; iSwamp++) {
                Card newLandCard = new Card(SWAMP);
                selectedCardPool.add(newLandCard);
            }
            // Add mountain cards.
            for (int iMountain = 0; iMountain < mountainToBeAdded; iMountain++) {
                Card newLandCard = new Card(MOUNTAIN);
                selectedCardPool.add(newLandCard);
            }
            // Add forest cards.
            for (int iForest = 0; iForest < forestToBeAdded; iForest++) {
                Card newLandCard = new Card(FOREST);
                selectedCardPool.add(newLandCard);
            }

            // Return true, so that the app will go back to the deck builder successfully. There are not too many lands.
            return true;
        }
        else
        {
            // There are too many lands being added. Do not navigate away from this Activity yet.
            // Warn the user in a Toast about what they have done. Then, return false to prevent transition to another activity.
            Toast.makeText(this, R.string.too_many_lands, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Returns to SealedActivity. Lands have already been added so only card pools need to be transferred back.
    private void returnToDeck()
    {
        // Create Intent for SealedActivity with numbers of lands stored in it from NumberPickers.
        Intent intent = new Intent(getApplicationContext(), SealedActivity.class);

        // Must pass lists of cards so that the app remembers them when moving to new activities.
        // SealedActivity must know these card pools so that it will display them instead of generating new card pools.
        // Create a bundle, since there is two lists of cards being passed in, not a single card.
        Bundle bundle = new Bundle();
        // Store card lists.
        bundle.putParcelableArrayList("openedCardPool", openedCardPool);
        bundle.putParcelableArrayList("selectedCardPool", selectedCardPool);
        // Put bundle of card lists into intent.
        intent.putExtras(bundle);

        // Put a boolean into the intent. This will tell SealedActivity that a valid card pool is in this Intent.
        intent.putExtra("cardPoolIntent", true);

        // Navigate back to SealedActivity using the Intent.
        startActivityForResult(intent, SEALED_ACTIVITY);
    }
}
