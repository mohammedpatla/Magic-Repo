package com.example.moham.magicdrafter.Model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Magic Drafter - DraftAi.java
 * Created by Brigham Moll.
 * Created on 1/1/2018.
 * Last Revised on 1/4/2018.
 * Description: This class represents an instance of an AI, used in the DraftSimulator. By default,
 * there are seven AIs, but the settings screen will allow customization of this.
 * Each AI, during a draft, picks a single card from their own packs, like the player.
 * These packs after having a card selected get passed among the AIs and the player...
 * ... Until none remain. Then a new pack is opened. This is done 3 times.
 * The AI class will contain logic as far as how AIs make their card choices.
 * Different AI difficulties may be implemented in the future.
 */

public class DraftAi implements Parcelable
{
    // Constants for card rarities.
    private static final char MYTHIC = 'M';
    private static final char RARE = 'R';
    private static final char UNCOMMON = 'U';

    // Constant for creatures, for totalCreatures.
    private static final char CREATURE = 'C';

    // Max number of preferred colors an AI can have.
    private static final int MAX_PREFERRED_COLORS = 2;

    // These are NOT colors for preferred colors. Disregard them.
    private static final char COLORLESS = 'C';
    private static final char MULTICOLOR = 'M';

    // Number of creatures that an AI aims to have.
    private static final int RECOMMENDED_CREATURES = 17;

    // This number is used to show that no preferred cards were found in the given list.
    private static final int NONE_PREFERRED = -2;

    // The AI's own openedCardPool and selectedCardPool.
    // These are lists of cards they've opened, and ones they have selected.
    // Later, the user may be able to view these selectedPools. (Future)
    private ArrayList<Card> openedCardPool;
    private ArrayList<Card> selectedCardPool;

    // For preferences, store up to two preferred colors of the AI, and their total number of collected creatures.
    private ArrayList<Character> preferredColors;
    private int totalCreatures;

    // Random object used to make choices in the AI.
    private Random randomSelector;

    public DraftAi()
    {
        // Initialize randomizer.
        randomSelector = new Random();

        // Initialize card pools for this AI.
        openedCardPool = new ArrayList<>();
        selectedCardPool = new ArrayList<>();

        // Initialize preference array for colors and counter for colors, and totalCreatures counter.
        preferredColors = new ArrayList<>();
        totalCreatures = 0;
    }

    // Create an AI out of a Parcel. (Used when loading saved instance states.)
    private DraftAi(Parcel in)
    {
        // Initialize a randomizer.
        randomSelector = new Random();

        // Initialize card pools for this AI based on Parcel.
        Bundle cardBundle = in.readBundle(ClassLoader.getSystemClassLoader());
        openedCardPool = cardBundle.getParcelableArrayList("openedCardPool");
        selectedCardPool = cardBundle.getParcelableArrayList("selectedCardPool");
        preferredColors = new ArrayList<>();
        ArrayList<String> prefColors = cardBundle.getStringArrayList("preferredColors");
        for (int iColor = 0; iColor < prefColors.size(); iColor++)
        {
            preferredColors.add(prefColors.get(iColor).charAt(0));
        }
        // Get totalCreatures.
        totalCreatures = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Called by runtime when writing to Parcel. Puts an AI into a Parcel.
        // Order is important.
        // Put both card pools and preferred colors in a bundle, then write the bundle.
        Bundle cardBundle = new Bundle();
        cardBundle.putParcelableArrayList("openedCardPool", openedCardPool);
        cardBundle.putParcelableArrayList("selectedCardPool", selectedCardPool);
        ArrayList<String> prefColors = new ArrayList<>();
        for(int iColor = 0; iColor < preferredColors.size(); iColor++)
        {
            prefColors.add(preferredColors.get(iColor).toString());
        }
        cardBundle.putStringArrayList("preferredColors", prefColors);
        dest.writeBundle(cardBundle);
        // Write totalCreatures.
        dest.writeInt(totalCreatures);
    }

    @Override
    public int describeContents()
    {
        // Leave as default.
        return 0;
    }

    // Used to give an AI a new pack of cards.
    public void setOpenedCardPool(ArrayList<Card> openedCardPool)
    {
        this.openedCardPool = openedCardPool;
    }

    // Used for passing AI cards away.
    public ArrayList<Card> getOpenedCardPool()
    {
        return openedCardPool;
    }

    // Used for Parcelable in order to save AIs on Activity being compromised.
    public static final Creator<DraftAi> CREATOR = new Creator<DraftAi>()
    {
        @Override
        public DraftAi createFromParcel(Parcel in)
        {
            return new DraftAi(in);
        }

        @Override
        public DraftAi[] newArray(int size)
        {
            return new DraftAi[size];
        }
    };

    // Used to make an AI select a card from their openedCardPool and add it to their selectedCardPool.
    public void selectACard()
    {
        // Check if there are any cards in this pack.
        if (openedCardPool.size() != 0)
        {
            // This holds the index of the card that the AI has chosen to draft.
            int iSelectedCard = -1;

            // Choose creatures more often if under 17 of them in the current selectedCardPool.
            // Is there a mythic in this card pool? Take it, if it is there.
            // (If there is more than one, choose the first one.)
            iSelectedCard = selectMythicOrRare(MYTHIC);

            // Is there a rare in this card pool? If there's only one, take it. Otherwise, if there is more than one, choose the first one.
            // Make sure iSelectedCard has not already been set to a mythic from the pack.
            if (iSelectedCard == -1)
            {
                iSelectedCard = selectMythicOrRare(RARE);
            }

            // Is there an uncommon in this card pool? (Match to chosen colors if possible, or preferred type.)
            // Make sure no mythic or rare has been selected.
            if (iSelectedCard == -1)
            {
                // Go through pool, make a pool of uncommons.
                ArrayList<Card> poolOfUncommons = new ArrayList<>();
                // Make an array of indexes of those uncommons.
                ArrayList<Integer> uncommonIndexes = new ArrayList<>();

                for (int iCard = 0; iCard < openedCardPool.size(); iCard++)
                {
                    if (openedCardPool.get(iCard).getRarity() == UNCOMMON)
                    {
                        // Add any uncommons found, to the pool.
                        poolOfUncommons.add(openedCardPool.get(iCard));
                        // Add their index to the appropriate list as well.
                        uncommonIndexes.add(iCard);
                    }
                }
                // If any uncommons were found, choose one carefully.
                if (poolOfUncommons.size() != 0)
                {
                    iSelectedCard = chooseCardFromPool(poolOfUncommons);
                    // iSelectedCard here is related to the poolOfUncommons. Instead, it must be the index in the full pool.
                    // This index in uncommonIndexes will be the value of the outer index.
                    if (iSelectedCard == NONE_PREFERRED)
                    {
                        // No preferred cards were found. Take a random uncommon.
                        iSelectedCard = randomSelector.nextInt(uncommonIndexes.size());
                    }
                    iSelectedCard = uncommonIndexes.get(iSelectedCard);
                }
            }

            // All remaining cards are commons if iSelectedCard is still -1. (Match to chosen colors if possible, or preferred type.)
            // Make sure nothing has been selected yet.
            if (iSelectedCard == -1)
            {
                iSelectedCard = chooseCardFromPool(openedCardPool);
                if (iSelectedCard == NONE_PREFERRED)
                {
                    // If no preferred commons were found, just pick a random card from all cards.
                    iSelectedCard = randomSelector.nextInt(openedCardPool.size());
                }
            }

            // Add to selected pool, remove from opened pool.
            selectedCardPool.add(openedCardPool.get(iSelectedCard));
            openedCardPool.remove(iSelectedCard);
        }
    }

    // Chooses a card from a pool of uncommons or commons based on preferences the AI has.
    private int chooseCardFromPool(ArrayList<Card> pool)
    {
        int returnVal = 0;
        // Make a list of indexes in pool that are preferred cards.
        ArrayList<Integer> preferredCardsIndexes = new ArrayList<>();
        // Find cards of preferred color(s) and put into a list. (Assumed the AI already has a preferred color at this point!)
        ArrayList<Card> preferredCards = new ArrayList<>();
        for(int iCard = 0; iCard < pool.size(); iCard++)
        {
            Card currentCard = pool.get(iCard);
            // If the AI still has no preferred colors or has only 1, use this card to try and give the AI one.
            if(preferredColors.size() < MAX_PREFERRED_COLORS)
            {
                findPreference(currentCard);
            }
            // Go through preferred colors and pool of given cards, and add cards that are of correct colors.
            for(int iColor = 0; iColor < preferredColors.size(); iColor++)
            {
                if(currentCard.getColor() == preferredColors.get(iColor))
                {
                    preferredCards.add(currentCard);
                    // Also save index of this card in a separate list. This number will be returned if the card is selected.
                    preferredCardsIndexes.add(iCard);
                }
            }
        }

        // If AI has below 17 creatures, search for a creature. Take one if one is there.
        // (Just choose a random card if no preferred cards were found above.)
        if (totalCreatures < RECOMMENDED_CREATURES && preferredCards.size() != 0)
        {
            for(int iCard = 0; iCard < preferredCards.size(); iCard++)
            {
                Card currentCard = preferredCards.get(iCard);
                if(currentCard.getType() == CREATURE)
                {
                    // Get appropriate index number from preferredCardsIndexes.
                    returnVal = preferredCardsIndexes.get(iCard);
                }
            }


        }
        else if (preferredCards.size() != 0)
        {
            // No creatures were found, or none were needed, but there are still cards desired here.
            // Take one of them at random.
            returnVal = preferredCardsIndexes.get(randomSelector.nextInt(preferredCardsIndexes.size()));
        }
        // Otherwise, if no creatures in pack or above 16 of them collected, or no preferred cards found...
        // Return that there has been no preferred cards found here.
        else
        {
            returnVal = NONE_PREFERRED;
        }

        // Return card index.
        return returnVal;
    }

    // Selects a mythic or rare. (If there is one) AI doesn't care about preferences in this case.
    private int selectMythicOrRare(char rarity)
    {
        // Return -1 if no card of target rarity was found.
        int returnVal = -1;

        for (int iCard = 0; iCard < openedCardPool.size(); iCard++)
        {
            Card thisCard = openedCardPool.get(iCard);
            // Pick out a rare. (Doesn't matter what type of card it is.)
            if (thisCard.getRarity() == rarity)
            {
                // Call find preference to set a preferred color for the AI if they have 0 or 1 so far.
                findPreference(thisCard);
                // Increment creature counter if this is a creature.
                if(thisCard.getType() == CREATURE)
                {
                    totalCreatures++;
                }
                returnVal = iCard;
            }
        }


        return returnVal;
    }

    // Uses a card to assign a color preference to an AI, if they only have 0 or 1 preference color so far.
    private void findPreference(Card thisCard)
    {
        // Find the color of this card.
        char newPreferenceColor = thisCard.getColor();

        // Check if array is already max size for preferred colors list.
        if(preferredColors.size() < MAX_PREFERRED_COLORS)
        {
            // Also make sure the card is not COLORLESS or MULTICOLORED!
            if(newPreferenceColor != COLORLESS && newPreferenceColor != MULTICOLOR)
            {
                // Check if array of colors has this color.
                for (int iColor = 0; iColor < preferredColors.size(); iColor++)
                {
                    if (preferredColors.get(iColor) == newPreferenceColor)
                    {
                        // Leave method if color is found in list.
                        return;
                    }
                }

                // If color is not found and there is still space, add it to the list.
                preferredColors.add(newPreferenceColor);
            }
        }
    }
}
