package com.example.moham.magicdrafter.Model;


import java.util.ArrayList;
import java.util.Random;

/**
 * Magic Drafter - CardPackGenerator.java
 * Created by Brigham Moll.
 * Created on 12/25/2017.
 * Last Revised on 12/26/2017.
 * Description: This class will generate card packs using the cardPool retrieved from the database of cards
 * in the set being drafted/opened. It will return these card pack pools to the activity as a single array, so they can be
 * displayed to the user.
 */

public class CardPackGenerator
{
    // Random number generator for choosing random cards for packs.
    private Random randomGen;

    // Constants for card pack generation.
    private static final int COMMON_PER_PACK = 10;
    private static final int UNCOMMON_PER_PACK = 3;

    // Represents 1/8 chance of mythic, 1/4 chance of foil.
    private static final int CHANCE_MYTHIC = 8;
    private static final int CHANCE_FOIL = 4;

    // chars for each rarity.
    private static final char COMMON_RARITY = 'C';
    private static final char UNCOMMON_RARITY = 'U';
    private static final char RARE_RARITY = 'R';
    private static final char MYTHIC_RARITY = 'M';
    private static final char ANY_CARD_RARITY = 'A';

    // Total card pool for drawing foils from.
    private ArrayList<Card> fullPool;

    // Portions of the total set's card pool for each card rarity.
    private ArrayList<Card> commonPool;
    private ArrayList<Card> uncommonPool;
    private ArrayList<Card> rarePool;
    private ArrayList<Card> mythicPool;

    public CardPackGenerator(ArrayList<Card> cardPool)
    {
        // Store full pool for foils, make empty lists for smaller lists.
        fullPool = cardPool;
        commonPool = new ArrayList<>();
        uncommonPool = new ArrayList<>();
        rarePool = new ArrayList<>();
        mythicPool = new ArrayList<>();

        // Initialize and fill each pool of cards with appropriate cards from cardPool.
        for(int iCard = 0; iCard < cardPool.size(); iCard++)
        {
            switch(cardPool.get(iCard).getRarity())
            {
                case 'C': commonPool.add(cardPool.get(iCard));
                          break;
                case 'U': uncommonPool.add(cardPool.get(iCard));
                          break;
                case 'R': rarePool.add(cardPool.get(iCard));
                          break;
                case 'M': mythicPool.add(cardPool.get(iCard));
                          break;
                default:  // ERROR! Code should not reach here.
                          break;
            }
        }

        // Create a Random object for random card selection.
        randomGen = new Random();
    }

    // Generates a certain number of packs and returns their contents.
    public ArrayList<Card> generatePacks(int packsToBeOpened)
    {
        // Initialize a list of cards for the opened cards from the packs to be placed into.
        ArrayList<Card> openedPacks = new ArrayList<>();

        // For each pack, generate a random set of cards and add them to the openedPacks array.
        for(int packsLeft = packsToBeOpened; packsLeft > 0; packsLeft--)
        {
            // Initialize a list of cards for this specific pack. This will be used to make sure no pack contains duplicate cards.
            ArrayList<Card> currentPack = new ArrayList<>();

            // Choose 10 commons for the pack.
            for(int commonsLeft = COMMON_PER_PACK; commonsLeft > 0; commonsLeft--)
            {
                Card generatedCard = createNonDuplicateCard(COMMON_RARITY, currentPack);
                currentPack.add(generatedCard);
                openedPacks.add(generatedCard);
            }
            // Choose 3 uncommons for the pack.
            for(int uncommonsLeft = UNCOMMON_PER_PACK; uncommonsLeft > 0; uncommonsLeft--)
            {
                Card generatedCard = createNonDuplicateCard(UNCOMMON_RARITY, currentPack);
                currentPack.add(generatedCard);
                openedPacks.add(generatedCard);
            }
            // After commons and uncommons, there is no need to check for duplicates. Also foil is allowed to be a duplicate.
            // Check if mythic in this pack...
            // Pick a number from 0 -> CHANCE_MYTHIC - 1. If number is 0, do mythic, otherwise rare.
            int mythicChance = randomGen.nextInt(CHANCE_MYTHIC);
            // If mythic, choose which one.
            if(mythicChance == 0)
            {
                Card generatedCard = generateCard(MYTHIC_RARITY);
                openedPacks.add(generatedCard);
            }
            // If rare, choose which one.
            else
            {
                Card generatedCard = generateCard(RARE_RARITY);
                openedPacks.add(generatedCard);
            }
            // Check foil chance, if foil, choose a random card of any rarity to be the foil and set foil boolean of card.
            // Pick a number from 0 -> CHANCE_FOIL -1. If number is 0, add a foil. Otherwise, don't.
            int foilChance = randomGen.nextInt(CHANCE_FOIL);
            if(foilChance == 0)
            {
                Card generatedCard = generateCard(ANY_CARD_RARITY);
                // Set this card to be foil.
                generatedCard.setFoilToTrue();
                openedPacks.add(generatedCard);
            }
        }

        // Return opened card packs list!
        return openedPacks;
    }

    // Creates a card from the pool of cards based on a rarity passed in by CardPackGenerator.
    private Card generateCard(char rarity)
    {
        // Declare card to be returned.
        Card generatedCard;
        generatedCard = new Card();
        Card cardToBeGenerated;
        cardToBeGenerated = null;
        // Choose a random index number within the range of the pool of cards based on the rarity passed in.
        int iRandomCard;

        switch(rarity)
        {
            case 'C':   iRandomCard = randomGen.nextInt(commonPool.size());
                        cardToBeGenerated = commonPool.get(iRandomCard);
                        break;
            case 'U':   iRandomCard = randomGen.nextInt(uncommonPool.size());
                        cardToBeGenerated = uncommonPool.get(iRandomCard);
                        break;
            case 'R':   iRandomCard = randomGen.nextInt(rarePool.size());
                        cardToBeGenerated = rarePool.get(iRandomCard);
                        break;
            case 'M':   iRandomCard = randomGen.nextInt(mythicPool.size());
                        cardToBeGenerated = mythicPool.get(iRandomCard);
                        break;
            case 'A':   // If any rarity is fine (foil), draw the card from the full pool of cards.
                        iRandomCard = randomGen.nextInt(fullPool.size());
                        cardToBeGenerated = fullPool.get(iRandomCard);
                        break;
            default:    // ERROR! Code should not reach here.
                        break;
        }

        // Copy values into cloned card that has been generated.
        if(cardToBeGenerated != null)
        {
            generatedCard.setId(cardToBeGenerated.getId());
            generatedCard.setType(cardToBeGenerated.getType());
            generatedCard.setCost(cardToBeGenerated.getCost());
            generatedCard.setRarity(cardToBeGenerated.getRarity());
            generatedCard.setColor(cardToBeGenerated.getColor());
        }
        // Return card that was generated!
        return generatedCard;
    }

    //Pass in current pack contents and the rarity of a card desired to be added to that pack.
    // This will use generateCard with that rarity to make a card, then it will check if it is a
    // duplicate of one in the pack already before returning the card.
    private Card createNonDuplicateCard(char rarity, ArrayList<Card> currentPack)
    {
        boolean duplicate;
        Card generatedCard;
        do {
            duplicate = false;
            generatedCard = generateCard(rarity);
            // Before adding card to pool, make sure it isn't in the pack already.
            for (int iCard = 0; iCard < currentPack.size(); iCard++) {
                if (currentPack.get(iCard).getId() == generatedCard.getId()) {
                    // Go through each card in the current pack. If the card is already there, set duplicate to true.
                    duplicate = true;
                }
            }
            // If not a duplicate, add it to the total card pool. (Return it by breaking the loop.)
            // If there is a duplicate, try generating a new card and checking again. (duplicate = true will continue the loop)
        } while (duplicate);
        return generatedCard;
    }
}
