package com.example.moham.magicdrafter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.example.moham.magicdrafter.Model.Card;
import com.example.moham.magicdrafter.Model.CardAdapter;
import com.example.moham.magicdrafter.Model.CardComparators.ColorSort;
import com.example.moham.magicdrafter.Model.CardComparators.CostSort;
import com.example.moham.magicdrafter.Model.CardComparators.TypeSort;
import com.example.moham.magicdrafter.Model.CardDatabase;
import com.example.moham.magicdrafter.Model.CardPackGenerator;

import java.util.ArrayList;
import java.util.Collections;

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

    // Store a boolean showing whether the opened cards pool is being shown currently or not.
    boolean openedCardsPoolShown;

    // Store the current card sorting method being used by int.
    int sortingMethod;

    // Constants for sorting shown cards.
    public static final int COLOR_SORT = 1;
    public static final int COST_SORT = 2;
    public static final int TYPE_SORT = 3;

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

        // Shows card pool at first that is out of deck by default. Set openedCardsPoolShown to true.
        openedCardsPoolShown = true;

        // Set sortingMethod to 0. This represents that the cards are sorted how they were opened from the packs or obtained.
        sortingMethod = 0;

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

        // Set a listener for the GridView so that when a card is tapped on, it is removed from the current list being shown and added to the other.
        // This could be from the pool(opened) to the deck(selected), or vice versa.
        grdCardView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // A card was selected. Add it to the list not being shown currently, and remove it from the one shown.
                if(openedCardsPoolShown)
                {
                    selectedCardPool.add(openedCardPool.get(position));
                    openedCardPool.remove(position);
                }
                else
                {
                    openedCardPool.add(selectedCardPool.get(position));
                    selectedCardPool.remove(position);
                }
                // Then, refresh the GridView with shown list of cards.
                // Use notifyDataSetChanged() to refresh so that the list won't scroll back to the top. Also saves resources. (Over generating the adapter again.)
                // Also update button text on btnCardsInDeck to reflect number of cards selected.
                ((CardAdapter)grdCardView.getAdapter()).notifyDataSetChanged();
                btnCardsInDeck.setText(selectedCardPool.size() + "/40");
            }
        });

        // Set a button listener so that when the button at the top right is pressed, the GridView will show either the selected or opened card pool. (Whatever is not currently shown.)
        btnCardsInDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(openedCardsPoolShown)
                {
                    // Show the opposite pool and change the value of openedCardsPoolShown.
                    grdCardView.setAdapter(new CardAdapter(getApplicationContext(), selectedCardPool));
                    openedCardsPoolShown = false;
                }
                else
                {
                    grdCardView.setAdapter(new CardAdapter(getApplicationContext(), openedCardPool));
                    openedCardsPoolShown = true;
                }
                // Sort cards according to last set sorting option.
                sortCards();
            }
        });

        // Set a listener for sorting cards by color. This can be done by sorting by collector's number.
        btnColorSort.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Set sorting int.
                sortingMethod = COLOR_SORT;
                // Sort cards in list being shown accordingly.
                sortCards();
                // Refresh GridView.
                ((CardAdapter)grdCardView.getAdapter()).notifyDataSetChanged();
            }
        });

        // Set a listener for sorting cards by type. The order can be alphabetically set according to their type's char.
        btnTypeSort.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Set sorting int.
                sortingMethod = TYPE_SORT;
                // Sort cards in list being shown accordingly.
                sortCards();
                // Refresh GridView.
                ((CardAdapter)grdCardView.getAdapter()).notifyDataSetChanged();
            }
        });

        // Set a listener for sorting cards by cost. This is done numerically by their cost property.
        btnCostSort.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Set sorting int.
                sortingMethod = COST_SORT;
                // Sort cards in list being shown accordingly.
                sortCards();
                // Refresh GridView.
                ((CardAdapter)grdCardView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    // Sort the cards in the currently shown pool (Either opened, or selected.) according to the current sortingMethod.
    private void sortCards()
    {
        ArrayList<Card> cardListToSort;

        // Find which card pool is shown.
        if(openedCardsPoolShown)
        {
            cardListToSort = openedCardPool;
        }
        else
        {
            cardListToSort = selectedCardPool;
        }

        // Sort according to sorting method using custom comparators in Card.java
        switch (sortingMethod)
        {
            case COLOR_SORT:
                Collections.sort(cardListToSort, new ColorSort());
                break;
            case TYPE_SORT:
                Collections.sort(cardListToSort, new TypeSort());
                break;
            case COST_SORT:
                Collections.sort(cardListToSort, new CostSort());
                break;
        }

        // Update GridView.
        ((CardAdapter)grdCardView.getAdapter()).notifyDataSetChanged();
    }
}
