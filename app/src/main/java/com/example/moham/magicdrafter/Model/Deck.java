package com.example.moham.magicdrafter.Model;

import java.util.ArrayList;

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

    public Deck(int deckid, ArrayList<Card> selectedCardPool,ArrayList<Card> openedCardPool) {
        this.deckid = deckid;
        this.selectedCardPool=selectedCardPool;
        this.openedCardPool =openedCardPool;
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

    public ArrayList<Card> getSelectedCardPool() {
        return selectedCardPool;
    }

    public ArrayList<Card> getOpenedCardPool() {
        return openedCardPool;
    }

    public static ArrayList<Deck> getDeck()
    {
        ArrayList<Deck> myDecks = new ArrayList<>();
        //add data ot myDecks

        return myDecks;
    }

}
