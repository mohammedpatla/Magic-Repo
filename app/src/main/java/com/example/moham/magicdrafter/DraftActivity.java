package com.example.moham.magicdrafter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moham.magicdrafter.Model.Card;
import com.example.moham.magicdrafter.Model.CardAdapter;
import com.example.moham.magicdrafter.Model.CardDatabase;
import com.example.moham.magicdrafter.Model.CardPackGenerator;
import com.example.moham.magicdrafter.Model.DraftAi;

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
Last Modified: 1/2/2018
 */

public class DraftActivity extends SimulatorActivity
{
    // Views of Activity.
    TextView txtPackNum;

    // Total cards to be drafted.
    private static final String TOTAL_TO_BE_DRAFTED = "/45";

    // Number of packs in the draft format.
    public static final int DRAFT_FORMAT = 3;
    public static final int DRAFT_PACK = 1;

    // Default number of Ais to make.
    public static final int DEFAULT_NUMBER_OF_AIS = 7;

    // Constant for sealed activity. Used with activity navigation.
    public static final int SEALED_ACTIVITY = 3;

    // The current pack that the user is going through out of 3.
    int packNum;

    // The list of DraftAis, populated at the start of a draft.
    ArrayList<DraftAi> listOfAis;

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
                    // Make sure the user is looking at the cards to be drafted.
                    if (openedCardsPoolShown)
                    {
                        // A card was selected to be drafted! Remove it from the current pack and move it to the user's selectedPool.
                        selectedCardPool.add(openedCardPool.get(position));
                        openedCardPool.remove(position);

                        // Update button text on btnCardsInDeck to reflect number of cards selected.
                        btnCardsInDeck.setText(selectedCardPool.size() + TOTAL_TO_BE_DRAFTED);

                        // Tell the AIs to select their cards.
                        selectAiCards();
                    }
                }
            }
        });

        // Initialize views.
        txtPackNum = findViewById(R.id.txt_pack_num);

        // Initialize packNum at 1.
        packNum = 1;

        // Update display.
        String packNumString = "Pack " + String.valueOf(packNum);
        txtPackNum.setText(packNumString);
    }

    // This method is called if new cards need to be generated. (A new drafting round starts after each pack is completely drafted.)
    private void generateRoundOfPacks()
    {
        // Database of cards and their appropriate images... This is all built-in and pre-created in the assets folder.
        // Retrieve information from database and create the card pool to draw cards from.
        CardDatabase cardDb = new CardDatabase(this);
        // Store card pool here.
        ArrayList<Card> cardPool;

        cardPool = cardDb.getCardPool();

        // Make a card pack generator. This will use the cardPool passed in to make the packs that will be opened.
        CardPackGenerator packGenerator = new CardPackGenerator(cardPool);

        // This is a single pack of the Draft being simulated, so open 1 pack.
        openedCardPool = packGenerator.generatePacks(DRAFT_PACK);

        // Update display and pack number if there is a display.
        if(txtPackNum != null)
        {
            packNum++;
            String packNumString = "Pack " + String.valueOf(packNum);
            txtPackNum.setText(packNumString);
        }

        // Initialize a selectedCardPool that is empty, for the user to move cards into when desired.
        // (Only do this if the pool doesn't exist yet.)
        if(selectedCardPool == null)
        {
            selectedCardPool = new ArrayList<>();
        }

        // Generate a pack for each AI in listOfAis.
        for(int iAi = 0; iAi < listOfAis.size(); iAi++)
        {
            listOfAis.get(iAi).setOpenedCardPool(packGenerator.generatePacks(DRAFT_PACK));
        }
    }

    // This method gets the AIs to each select one card from their pools.
    // Afterwards, it will pass each set of cards to the next player in the list of AIs.
    // (The user will get the set of cards the last AI had.)
    // Then, the GridView will be updated to show the pool to choose from next.
    private void selectAiCards()
    {
        // Get Ais to select their choices.
        for(int iAi = 0; iAi < listOfAis.size(); iAi++)
        {
            listOfAis.get(iAi).selectACard();
        }

        // Pass sets of cards to next appropriate players.
        passCardSets();

        // Check if there is more cards in the pack still.
        if (openedCardPool.size() > 0)
        {
            // If there is, continue the round by updating the GridView.
            CardAdapter adapter = ((CardAdapter) grdCardView.getAdapter());
            adapter.changeCardList(openedCardPool);
            adapter.notifyDataSetChanged();
        }
        else
        {
            // If there are no more cards in this pack, check if the Draft is over.
            if(packNum >= DRAFT_FORMAT)
            {
                // If the draft is over, display a toast to let the user know what is about to occur.
                Toast.makeText(this, "Draft complete. Moving to deck builder mode.", Toast.LENGTH_LONG).show();

                // Now, store the current card information in an Intent and send it to the SealedActivity.
                // There, the user can continue building their deck.
                // The Intent will stop SealedActivity from generating cards.
                moveToDeckBuilding();
            }
            else
            {
                // If it is not over, generate another pack for each player.
                generateRoundOfPacks();

                // Update GridView.
                CardAdapter adapter = ((CardAdapter) grdCardView.getAdapter());
                adapter.changeCardList(openedCardPool);
                adapter.notifyDataSetChanged();
            }
        }

    }

    // Passes cards that have been opened, around the 'table' of players.
    // AI1 gets the player's cards, AI2 gets AI1's cards, ...., AI7 gives their cards to the player...etc.
    private void passCardSets()
    {
        // Temporarily hold a reference to the player's card pool while cards are passed.
        ArrayList<Card> playersSelectedPool = openedCardPool;

        // Move the last AI's cards to the player's pool.
        openedCardPool = listOfAis.get(listOfAis.size()-1).getOpenedCardPool();

        // Use a loop to move the remaining card pools to each AI.
        for(int iAiSendingTo = listOfAis.size()-1; iAiSendingTo > 0; iAiSendingTo--)
        {
            int iAiComingFrom = iAiSendingTo - 1;
            listOfAis.get(iAiSendingTo).setOpenedCardPool(listOfAis.get(iAiComingFrom).getOpenedCardPool());
        }

        // Finally, move the stored card pool to the first AI.
        listOfAis.get(0).setOpenedCardPool(playersSelectedPool);
    }

    // Sends card information to the SealedActivity for deck building after the draft
    // as well as deck saving.
    private void moveToDeckBuilding()
    {
        // Create Intent for SealedActivity with card information stored in it from draft.
        Intent intent = new Intent(getApplicationContext(), SealedActivity.class);

        // Must pass lists of cards so that the app remembers them when moving to new activities.
        // SealedActivity must know these card pools so that it will display them instead of generating new card pools.
        // Create a bundle, since there is two lists of cards being passed in, not a single card.
        Bundle bundle = new Bundle();
        // Store card lists. (Reverse the names of the pools so that selected cards are not all in the deck already.)
        bundle.putParcelableArrayList("selectedCardPool", openedCardPool);
        bundle.putParcelableArrayList("openedCardPool", selectedCardPool);
        // Put bundle of card lists into intent.
        intent.putExtras(bundle);

        // Put a boolean into the intent. This will tell SealedActivity that a valid card pool is in this Intent.
        intent.putExtra("cardPoolIntent", true);

        // Navigate back to SealedActivity using the Intent.
        startActivityForResult(intent, SEALED_ACTIVITY);
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
    protected void initialCardGeneration()
    {
        // Create an amount of AIs equal to the number chosen in the settings menu.
        // Default = 7. Put these into a list.
        listOfAis = new ArrayList<>();
        for(int iAi = 0; iAi < DEFAULT_NUMBER_OF_AIS; iAi++)
        {
            DraftAi newAi = new DraftAi();
            listOfAis.add(newAi);
        }

        // In Draft format, there are 3 packs that get opened.
        // Start the first round of packs.
        generateRoundOfPacks();
    }
}
