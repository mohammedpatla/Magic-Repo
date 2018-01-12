package com.example.moham.magicdrafter.Model.CardComparators;

import com.example.moham.magicdrafter.Model.Card;

import java.util.Comparator;

/**
 * Magic Drafter - ColorSort.java
 * Created by Brigham Moll.
 * Created on 12/31/2017.
 * Last Revised on 1/11/2018.
 * Description: This simple comparator class sorts a list of Cards according to their color/collector's number.
 */

// Sorts cards according to color in an ArrayList sort method.
// Feeding the comparator to sort() will allow cards to be sorted according to custom parameters.
public class ColorSort implements Comparator<Card>
{
    @Override
    public int compare(Card cardOne, Card cardTwo)
    {
        return cardOne.getColor() - cardTwo.getColor();
    }
}