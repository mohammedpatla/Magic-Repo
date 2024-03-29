package com.example.moham.magicdrafter.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Magic Drafter - CardDatabase.java
 * Created by Brigham Moll.
 * Created on 12/25/2017.
 * Last Revised on 1/1/2018.
 * Description: This class is used to represent a single card that has been drawn from a pack.
 * Information stored in the Card includes cost, color, rarity, collector's number (id), and type.
 * This information is used to generate random card packs for sealed/draft simulators. Information is also used to sort cards in user's view.
 * Also stores whether the card is foil. (Assigned after drawn from pack, if user is lucky.)
 */

public class Card implements Parcelable
{
    // Card information field variables.
    // Collector's number of the card. (Displayed at the bottom of the card.) Primary key of database and used for sorting.
    private int id;
    // Converted mana cost of the card. This refers to the total number of symbols at the top right of the card. Used for sorting.
    private int cost;
    // Rarity of the card. This is denoted by the orange,gold,silver, or black symbol at the right of the card.
    // Used to determine when the card will show up in a generated pack.
    private char rarity;
    // The type of the card, used for sorting.
    private char type;
    // The color of the card, used for sorting.
    private char color;
    // Whether the card is foil or not.
    private boolean foil;
    // Whether the card has a back-side to it or not.
    private boolean flip;
    // What set this card is from.
    private String set;

    // Basic Land type constants for making basic lands.
    private static final char LANDRARITYCOLOR = 'C';
    private static final char LANDTYPE = 'L';

    // Initializes passed in card from the original full set pool.
    public Card (int id, int cost, char rarity, char type, char color, boolean flip, String set)
    {
        this.id = id;
        this.cost = cost;
        this.rarity = rarity;
        this.color = color;
        this.type = type;
        this.flip = flip;
        this.set = set;
        foil = false;
    }

    // Initialize blank card to be cloned out of the set's pool after being opened out of a pack.
    public Card()
    {
        id = 0;
        cost = -1;
        rarity = type = color = ' ';
        foil = false;
        flip = false;
        set = "";

    }

    // Creating a Card via a Parcel. (Used for moving card lists around to different activities.)
    private Card(Parcel in)
    {
        id = in.readInt();
        cost = in.readInt();
        rarity = in.readString().charAt(0);
        type = in.readString().charAt(0);
        color = in.readString().charAt(0);
        foil = in.readByte() != 0;
        flip = in.readByte() != 0;
        set = in.readString();
    }

    // Creating a basic land Card.
    public Card(int basicLandType)
    {
        id = basicLandType;
        cost = 0;
        rarity = color = LANDRARITYCOLOR;
        type = LANDTYPE;
        foil = false;
        flip = false;
        color = 'C';
        set = "IxalanSet";
    }

    // Getters and setters.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public char getRarity() {
        return rarity;
    }

    public void setRarity(char rarity) {
        this.rarity = rarity;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    // As of now, the foil feature is not used. In a future release, may show cards with a glow if they are foil. May also make AIs prioritize foils higher than other cards.
    // Foils are still required in the application as of now to generate accurate packs though. (Hence being partially implemented.)
    /*
    public boolean getFoil()
    {
        return foil;
    }
    */

    public boolean getFlip()
    {
        return flip;
    }

    public void setFlip(boolean flip)
    {
        this.flip = flip;
    }

    // Foil is never unset, so simply sets to true.
    public void setFoilToTrue()
    {
        foil = true;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getSet() {
        return set;
    }

    // Used for Parcelable in order to move Card lists around the app.
    public static final Creator<Card> CREATOR = new Creator<Card>()
    {
        @Override
        public Card createFromParcel(Parcel in)
        {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size)
        {
            return new Card[size];
        }
    };

    @Override
    public int describeContents()
    {
        // Leave as default.
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        // Called by runtime when writing to Parcel. Puts a Card into a Parcel.
        // Order is important.
        dest.writeInt(id);
        dest.writeInt(cost);
        dest.writeString(Character.toString(rarity));
        dest.writeString(Character.toString(type));
        dest.writeString(Character.toString(color));
        dest.writeByte((byte)(foil ? 1: 0));
        dest.writeByte((byte)(flip ? 1: 0));
        dest.writeString(set);
    }


    //Returning JSON OBJECT to create JSON file
    //Refrence from https://stackoverflow.com/questions/4841952/convert-arraylistmycustomclass-to-jsonarray?answertab=votes#tab-top
    //Created By Mohammed Patla 01-4-2018
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("cost", cost);
            obj.put("rarity", rarity);
            obj.put("type", type);
            obj.put("color", color);
            obj.put("foil", foil);
            obj.put("flip", flip);
            obj.put("set", set);

        } catch (JSONException e) {
            Log.e(TAG, "getJSONObject: DefaultListItem.toString JSONException:",e);
        }
        return obj;
    }
}
