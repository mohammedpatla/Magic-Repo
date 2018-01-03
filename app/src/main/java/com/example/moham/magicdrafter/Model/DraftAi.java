package com.example.moham.magicdrafter.Model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Magic Drafter - DraftAi.java
 * Created by Brigham Moll.
 * Created on 1/1/2018.
 * Last Revised on 1/1/2018.
 * Description: This class represents an instance of an AI, used in the DraftSimulator. By default,
 * there are seven AIs, but the settings screen will allow customization of this.
 * Each AI, during a draft, picks a single card from their own packs, like the player.
 * These packs after having a card selected get passed among the AIs and the player...
 * ... Until none remain. Then a new pack is opened. This is done 3 times.
 * The AI class will contain logic as far as how AIs make their card choices.
 * Different AI difficulties may be implemented in the future.
 */

public class DraftAi
{
    // The AI's own openedCardPool and selectedCardPool.
    // These are lists of cards they've opened, and ones they have selected.
    // Later, the user may be able to view these selectedPools. (Future)
    private ArrayList<Card> openedCardPool;
    private ArrayList<Card> selectedCardPool;

    // Random object used to make choices in the AI.
    private Random randomSelector;

    public DraftAi()
    {
        // Initialize randomizer.
        randomSelector = new Random();

        // Initialize card pools for this AI.
        openedCardPool = new ArrayList<>();
        selectedCardPool = new ArrayList<>();
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

    // Used to make an AI select a card from their openedCardPool and add it to their selectedCardPool.
    public void selectACard()
    {
        // Check if there are any cards in this pack.
        if (openedCardPool.size() != 0)
        {
            int iSelectedCard = -1;
            // Choose creatures more often if under 17 of them in the current selectedCardPool.
            // Is there a mythic in this card pool? Take it, if it is there.
            // (Or randomly choose one if the rare chance arises that there is more than one.)

            // Is there a rare in this card pool? If there's only one, take it. Otherwise, randomly select.

            // Is there an uncommon in this card pool, that matches the chosen colors of this AI?
            // If so, select it, or a random one of that criteria.

            // Is there a common in this pool that matches the chosen colors of this AI? Choose one.

            // TEST! Choose a random card for now.
            iSelectedCard = randomSelector.nextInt(openedCardPool.size());

            // Add to selected pool, remove from opened pool.
            selectedCardPool.add(openedCardPool.get(iSelectedCard));
            openedCardPool.remove(iSelectedCard);
        }
    }
}
