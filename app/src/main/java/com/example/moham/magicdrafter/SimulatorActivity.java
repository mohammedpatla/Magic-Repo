package com.example.moham.magicdrafter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.moham.magicdrafter.Model.Card;
import com.example.moham.magicdrafter.Model.CardAdapter;
import com.example.moham.magicdrafter.Model.CardComparators.ColorSort;
import com.example.moham.magicdrafter.Model.CardComparators.CostSort;
import com.example.moham.magicdrafter.Model.CardComparators.TypeSort;

import java.util.ArrayList;
import java.util.Collections;

/*
    SimulatorActivity - Brigham Moll - 1/1/2018
    This abstract Activity class will be inherited from by SealedActivity and DraftActivity.
    The two activities share a lot of code for viewing and adding/removing cards from pools.
    Shared code will be placed here so that differences are clearly in their respective classes.
    Modified: 1/11/2018
 */

public abstract class SimulatorActivity extends Activity
{
    // Constants for sorting shown cards.
    public static final int COLOR_SORT = 1;
    public static final int COST_SORT = 2;
    public static final int TYPE_SORT = 3;

    // Constant for knowing if a card is a basic land. (ID over 500.)
    public static final int LAND_ID_START = 500;

    // Used for file name finding when getting high resolution card images for Image dialog.
    public static final String IXALAN_HIGH_RES_CARD_IMAGES = "ixahigh";
    public static final String RIX_HIGH_RES_CARD_IMAGES = "rixhigh";
    public static final String DOM_HIGH_RES_CARD_IMAGES = "domhigh";

    // Used for file name finding when getting the flip side of an expanded card.
    public static final String FLIP_SIDE = "_2";

    // Constants for the card set tables. To be passed into CardDatabase.
    // Also, the constants are used for set selection.
    // The table for the set called "Ixalan".
    public static final String IXALAN_CARD_TABLE = "IxalanSet";
    // The table for the set called "Rivals of Ixalan".
    public static final String RIX_CARD_TABLE = "RIXSet";
    // The table for the set called "Dominaria".
    public static final String DOM_CARD_TABLE = "DOMSet";

    // Views of Activity.
    GridView grdCardView;
    Button btnColorSort;
    Button btnCostSort;
    Button btnTypeSort;
    Button btnCardsInDeck;

    // Card pools that are opened and selected.
    ArrayList<Card> openedCardPool;
    ArrayList<Card> selectedCardPool;

    // Store a boolean showing whether the opened cards pool is being shown currently or not.
    boolean openedCardsPoolShown;

    // Store the current card sorting method being used by int.
    int sortingMethod;

    // Indicates whether a card is currently being viewed up close. Used to stop unintentional adding/removing of cards while viewing one.
    boolean cardExpanded;

    // Store reference to expanded card image dialog here. By doing this, it can be dismissed when clicked on, or the image can be changed for flip cards.
    Dialog cardExpandedViewDialog;

    // The ImageView within the expanded image dialog for a viewed card.
    ImageView cardDialogImage;

    // The card currently expanded for closer view on-screen.
    Card expandedCard;

    // Whether the card that has been expanded is currently flipped over or not.
    boolean expandedCardFlipped;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // Initialize views.
        grdCardView = findViewById(R.id.grd_card_view);
        btnColorSort = findViewById(R.id.btn_color_sort);
        btnCostSort = findViewById(R.id.btn_cost_sort);
        btnTypeSort = findViewById(R.id.btn_type_sort);
        btnCardsInDeck = findViewById(R.id.btn_cards_in_deck);

        // Initialize cardExpanded to false, as no card is viewed up close yet.
        cardExpanded = false;
        // Set expandedCard to null because no card is viewed up close yet.
        expandedCard = null;
        // Set expandedCardFlipped to false, as no card has been viewed, let alone flipped.
        expandedCardFlipped = false;

        // Shows card pool at first that is out of deck by default. Set openedCardsPoolShown to true.
        openedCardsPoolShown = true;

        // Set sortingMethod to 0. This represents that the cards are sorted how they were opened from the packs or obtained.
        sortingMethod = 0;

        // Complete some kind of card generation process. Depends upon the type of simulator.
        initialCardGeneration();

        // Use this openedCardPool to generate items in the GridView for each card.
        grdCardView.setAdapter(new CardAdapter(this, openedCardPool));

        // If user holds down finger on card, it will launch a dialog with the high resolution image of that card.
        // (Possibly in the future, sense gestures, use proper zooming animation or popup activity...)
        grdCardView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Launch dialog here with image corresponding to this Card.
                cardExpandedViewDialog = new Dialog(getSimContext());

                // Show dialog without any title, of course.
                cardExpandedViewDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                // Use layout to show picture of card instead of typical dialog with button, etc.
                View convertView = getLayoutInflater().inflate(R.layout.card_dialog_zoomed, null);
                cardExpandedViewDialog.setContentView(convertView);

                // Change image of dialog to the appropriate one for this card.
                // Also store reference to ImageView for changing image on flip cards.
                cardDialogImage = convertView.findViewById(R.id.img_dialog);

                // Find pool of card by checking which one is currently displayed, and use position to find which card, then finally use getId to get the ID of the card.
                // With the ID, the cardImageFileName can be determined.
                Card dialogCard;
                if(openedCardsPoolShown)
                {
                    dialogCard = openedCardPool.get(position);
                }
                else
                {
                    dialogCard = selectedCardPool.get(position);
                }
                String cardImageFileName = getCardSet(dialogCard) + dialogCard.getId();

                // Set the card's image. Use IXALAN_HIGH_RES_CARD_IMAGES for the higher resolution images.
                cardDialogImage.setImageResource(getResources().getIdentifier(cardImageFileName, "drawable", getPackageName()));

                // Make sure that when the dialog is dismissed, it calls onExpandedCardDialogClick.
                cardExpandedViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        onExpandedCardDialogClick(null);
                    }
                });

                // Actually present the dialog to the user.
                cardExpandedViewDialog.show();

                // Set "cardExpanded" to true. When it is true, no cards can be added or removed from pools.
                cardExpanded = true;
                // Set expandedCard to a reference to the card being displayed so that the app will know which one is displayed currently.
                expandedCard = dialogCard;

                return false;
            }
        });

        // Set a button listener so that when the button at the top right is pressed, the GridView will show either the selected or opened card pool. (Whatever is not currently shown.)
        btnCardsInDeck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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

    // Finds the high res file name for a card's set and returns it.
    public String getCardSet(Card card)
    {
        switch (card.getSet())
        {
            case IXALAN_CARD_TABLE:
                return IXALAN_HIGH_RES_CARD_IMAGES;
            case RIX_CARD_TABLE:
                return RIX_HIGH_RES_CARD_IMAGES;
            case DOM_CARD_TABLE:
                return DOM_HIGH_RES_CARD_IMAGES;
            default:
                // ERROR!
                return IXALAN_HIGH_RES_CARD_IMAGES;
        }
    }

    // Store card info so new cards are not generated when the activity is compromised by say, a rotation of the device.
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save card info.
        outState.putParcelableArrayList("openedCardPool", openedCardPool);
        outState.putParcelableArrayList("selectedCardPool", selectedCardPool);

        // Save whether opened or selected card pool is shown.
        outState.putBoolean("openedCardsPoolShown", openedCardsPoolShown);

        // Save what sorting method was used last.
        outState.putInt("sortingMethod", sortingMethod);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        // Load card info.
        openedCardPool = savedInstanceState.getParcelableArrayList("openedCardPool");
        selectedCardPool = savedInstanceState.getParcelableArrayList("selectedCardPool");

        // Load which pool is shown.
        openedCardsPoolShown = savedInstanceState.getBoolean("openedCardsPoolShown");

        // Load previous sorting method used.
        sortingMethod = savedInstanceState.getInt("sortingMethod");

        // Refresh display.
        CardAdapter adapter = (CardAdapter)grdCardView.getAdapter();
        if(openedCardsPoolShown)
        {
            adapter.changeCardList(openedCardPool);
        }
        else
        {
            adapter.changeCardList(selectedCardPool);
        }
        adapter.notifyDataSetChanged();

    }

    // If the Back Button is pressed, warn user they will lose data and be sent to MainActivity if they continue.
    @Override
    public void onBackPressed()
    {
        // Warn the user if they continue they will be redirected and lose data.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create dialog with settings.
        builder.setTitle(getString(R.string.exitingSim))
                .setIcon(R.mipmap.ic_magic_drafter_round)
                .setMessage(getString(R.string.simWarning))
                .setPositiveButton(getString(R.string.exit), backListener)
                .setNegativeButton(getString(R.string.stay), backListener);

        // Make an instance of the dialog.
        AlertDialog dialog = builder.create();

        // Show dialog.
        dialog.show();
    }

    // Responses to back button dialog.
    DialogInterface.OnClickListener backListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch(which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    // Go to the MainActivity. Do not store any data, user has been warned.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    // Don't do anything. The user has chosen to stay on this activity.
                    break;
                default:
                    break;
            }
        }
    };

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
            default:
                break;
        }

        // Update GridView.
        ((CardAdapter)grdCardView.getAdapter()).notifyDataSetChanged();
    }

    // If the expanded card view dialog is clicked on, this method will be called.
    // If the card is a normal card, it will dismiss the dialog. If it is a flip card, it will flip the card on click. The next click will close the dialog afterwords.
    public void onExpandedCardDialogClick(View view)
    {
        // If this method has been called with a view, try to flip the card, if it is a flip card. This means the user tapped the image of the card, not around the image.
        // Check if card is a flip card.
        if (expandedCard.getFlip() && view != null)
        {
            // If flip card, check if it has been flipped.
            if(!expandedCardFlipped)
            {
                // If not, change the art to the flip side of it.
                String cardImageFileName = getCardSet(expandedCard) + expandedCard.getId() + FLIP_SIDE;
                cardDialogImage.setImageResource(getResources().getIdentifier(cardImageFileName, "drawable", getPackageName()));
                // Set expandedCardFlipped to true.
                expandedCardFlipped = true;
            }
            else
            {
                // Set expandedCardFlipped to false.
                expandedCardFlipped = false;
                // If it has been flipped, dismiss.
                cardExpandedViewDialog.dismiss();
                // Set cardExpanded to false to symbolize that the user is finished viewing a card.
                cardExpanded = false;
            }
        }
        else
        {
            // Dismiss the dialog, the user is done with viewing the card.
            cardExpandedViewDialog.dismiss();
            // Set cardExpanded to false to symbolize that the user is finished viewing a card.
            cardExpanded = false;
        }
    }


    protected abstract int getLayoutResourceId();

    protected abstract Context getSimContext();

    protected abstract void initialCardGeneration();
}
