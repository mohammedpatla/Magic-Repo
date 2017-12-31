package com.example.moham.magicdrafter.Model.CardComparators;

import com.example.moham.magicdrafter.Model.Card;

import java.util.Comparator;

/**
 * Magic Drafter - CostSort.java
 * Created by Brigham Moll.
 * Created on 12/31/2017.
 * Last Revised on 12/31/2017.
 * Description: This simple comparator class sorts a list of Cards according to their mana cost.
 */

// Sorts cards according to mana cost in an ArrayList sort method.
// Feeding the comparator to sort() will allow cards to be sorted according to custom parameters.
public class CostSort implements Comparator<Card>
{
    @Override
    public int compare(Card cardOne, Card cardTwo)
    {
        return cardOne.getCost() - cardTwo.getCost();
    }
}