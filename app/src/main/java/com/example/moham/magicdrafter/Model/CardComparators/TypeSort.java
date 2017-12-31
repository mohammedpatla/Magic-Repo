package com.example.moham.magicdrafter.Model.CardComparators;

import com.example.moham.magicdrafter.Model.Card;

import java.util.Comparator;

/**
 * Magic Drafter - TypeSort.java
 * Created by Brigham Moll.
 * Created on 12/31/2017.
 * Last Revised on 12/31/2017.
 * Description: This simple comparator class sorts a list of Cards according to their type.
 */

// Sorts cards according to type in an ArrayList sort method.
// Feeding the comparator to sort() will allow cards to be sorted according to custom parameters.
public class TypeSort implements Comparator<Card>
{
    @Override
    public int compare(Card cardOne, Card cardTwo)
    {
        return cardOne.getType() - cardTwo.getType();
    }
}
