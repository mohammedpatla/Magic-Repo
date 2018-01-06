package com.example.moham.magicdrafter.Model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by moham on 2018-01-03.
 */

public class Deck {

    //Deck number
    private int deckid;

    //Deck Name
    private String deckName;

    //Deck number of cards
    private int nocards;

    //Deck type
    private String decktype;

    //Deck Discription
    private String deckdesc;

    //arraylist of Card Set in Deck
    ArrayList<Card> selectedCardPool = new ArrayList<>();

    ArrayList<Card> openedCardPool = new ArrayList<>();

    public Deck() {
        deckid=0;
        deckName="Default";
        nocards=0;
        decktype="";
        deckdesc="";
    }

    public Deck(int deckid, ArrayList<Card> selectedCardPool, ArrayList<Card> openedCardPool) {
        this.deckid = deckid;
        this.selectedCardPool=selectedCardPool;
        this.openedCardPool =openedCardPool;
        nocards=selectedCardPool.size();
        decktype="";
        deckName="NewDeck"+Integer.toString(deckid);
        deckdesc="";
    }
    public Deck(int deckid,String deckName, ArrayList<Card> selectedCardPool,ArrayList<Card> openedCardPool) {
        this.deckid = deckid;
        this.deckName=deckName;
        this.selectedCardPool=selectedCardPool;
        this.openedCardPool =openedCardPool;
        nocards=selectedCardPool.size();
        decktype="";
        deckdesc="";
    }

    public Deck(int deckid, String deckName, int nocards, String decktype, String deckdesc, ArrayList<Card> selectedCardPool, ArrayList<Card> openedCardPool) {
        this.deckid = deckid;
        this.deckName = deckName;
        this.nocards = nocards;
        this.decktype = decktype;
        this.deckdesc = deckdesc;
        this.selectedCardPool = selectedCardPool;
        this.openedCardPool = openedCardPool;
    }

    public int getDeckid() {
        return deckid;
    }

    public String getDeckName() {
        return deckName;
    }

    public int getNocards() {
        return nocards;
    }

    public String getDecktype() {
        return decktype;
    }

    public String getDeckdesc() {
        return deckdesc;
    }

    public ArrayList<Card> getSelectedCardPool() {
        return selectedCardPool;
    }

    public ArrayList<Card> getOpenedCardPool() {
        return openedCardPool;
    }

    public void setDeckid(int deckid) {
        this.deckid = deckid;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public void setNocards(int nocards) {
        this.nocards = nocards;
    }

    public void setDecktype(String decktype) {
        this.decktype = decktype;
    }

    public void setDeckdesc(String deckdesc) {
        this.deckdesc = deckdesc;
    }

    public void setSelectedCardPool(ArrayList<Card> selectedCardPool) {
        this.selectedCardPool = selectedCardPool;
    }

    public void setOpenedCardPool(ArrayList<Card> openedCardPool) {
        this.openedCardPool = openedCardPool;
    }

    public static ArrayList<Deck> getDeck()
    {
        ArrayList<Deck> myDecks = new ArrayList<>();
        //add data ot myDecks

        return myDecks;
    }

    /*
    * Solution refrenced from
    * https://stackoverflow.com/questions/4841952/convert-arraylistmycustomclass-to-jsonarray?answertab=votes#tab-top
    * */
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("deckId", deckid);
            obj.put("deckName", deckName);
            obj.put("nocards", nocards);
            obj.put("decktype", decktype);
            obj.put("deckdesc", deckdesc);

            //getting each card to be added to the deck
            JSONArray jsonArray = new JSONArray();
            for (int i=0; i < selectedCardPool.size(); i++) {
                jsonArray.put(selectedCardPool.get(i).getJSONObject());
            }
            obj.put("selectedCardPool",jsonArray);

            JSONArray jsonArray2 = new JSONArray();
            for (int i=0; i < openedCardPool.size(); i++) {
                jsonArray2.put(openedCardPool.get(i).getJSONObject());
            }
            obj.put("openedCardPool",jsonArray2);

        } catch (JSONException e) {
            Log.e(TAG, "getJSONObject:DefaultListItem.toString JSONException: ",e);
        }
        return obj;
    }

}
