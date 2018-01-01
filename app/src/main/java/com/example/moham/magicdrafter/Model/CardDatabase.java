package com.example.moham.magicdrafter.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Magic Drafter - CardDatabase.java
 * Created by Brigham Moll.
 * Created on 12/25/2017.
 * Last Revised on 12/26/2017.
 * Description: This class is used to access data from the database in order to generate card packs randomly
 * from the Magic: The Gathering set being used. This is done with the help of the extended CardDataBaseHelper.
 */

public class CardDatabase
{
    // Instances for accessing  pre-built card database.
    private SQLiteDatabase database;
    private CardDataBaseHelper openHelper;

    // Constants for the table.
    // The table for the set called "Ixalan".
    private static final String IXALAN_CARD_TABLE = "IxalanSet";

    // Fields of card info.
    private static final int ID_COLUMN = 0;
    private static final int COLOR_COLUMN = 1;
    private static final int COST_COLUMN = 2;
    private static final int TYPE_COLUMN = 3;
    private static final int RARITY_COLUMN = 4;
    private static final int FLIP_COLUMN = 5;

    // Creates the custom OpenHelper class, made to access the database from the assets folder.
    public CardDatabase(Context context)
    {
        // Initialize the extended OpenHelper.
        openHelper = new CardDataBaseHelper(context);
        // Generate database, copy values into it from pre-built database.
        openHelper.generateAppDatabase();
        openHelper.openDatabase();
    }

    // Using getCardPool(), retrieve an array of all possible cards in the set. Later, generated cards will be copied from this pool of cards.
    public ArrayList<Card> getCardPool()
    {
        // Create the ArrayList that will hold the cards in the set.
        ArrayList<Card> cardsPool = new ArrayList<>();

        // Get readable database for reading off the card information.
        database = openHelper.getReadableDatabase();

        // Get all records from the cards pool table. Ordered by the _id. (Collector's number)
        Cursor result = database.query(IXALAN_CARD_TABLE, null, null, null, null, null, "_id");

        // Loop through cursor to get all Cards into the ArrayList.
        while(result.moveToNext())
        {
            // Get the values from the Cursor.
            int id = result.getInt(ID_COLUMN);
            // All the 'string' entries in this table are 1 character, so convert them to char to save space.
            // .charAt(0) takes the character that is first in the String.
            char color = result.getString(COLOR_COLUMN).charAt(0);
            int cost = result.getInt(COST_COLUMN);
            char type = result.getString(TYPE_COLUMN).charAt(0);
            char rarity = result.getString(RARITY_COLUMN).charAt(0);
            boolean flip = result.getInt(FLIP_COLUMN) != 0;

            // With all information gathered for this card entry, make the card and add it to the Array.
            cardsPool.add(new Card(id, cost, rarity, type, color, flip));
        }

        // Close database in CardDataBaseHelper.
        openHelper.close();

        // Close cursor and database.
        result.close();
        database.close();

        // Return the card pool.
        return cardsPool;
    }
}
