package com.example.moham.magicdrafter.Model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Title of Project - Title of Script
 * Created by Brigham Moll.
 * Created on 12/26/2017.
 * Last Revised on 1/2/2018.
 * Description: This class extends BaseAdapter. It is used to display cards from a pool of cards on the screen
 * as GridView items. Each item has a picture of the card that has been drawn from a pack. Cards can be selected
 * for a deck, or removed from one, when the user taps on an item. If they press down and hold on the item,
 * the picture will enlarge so they can view the card up close. While up close, they can hold down again to flip
 * the card, if it has another side. Otherwise they can tap again to make the image 'zoom out'.
 */

public class CardAdapter extends BaseAdapter
{
    // Determines size of items to be displayed in GridView.
    private static final int ITEM_HEIGHT = 320;
    // Width of a card is about 67% of its height.
    private static final int ITEM_WIDTH = (int)(0.67*ITEM_HEIGHT);

    // Use this to find image files in the Ixalan set of low resolution.
    private static final String IXALAN_LOW_RES_IMAGES = "ixa";

    // Context is required to make an ImageView, so store a reference to it when Adapter is made.
    private Context context;

    // A reference to the opened pool of cards that is to be displayed.
    private ArrayList<Card> openedCardPool;

    public CardAdapter(Context context, ArrayList<Card> openedCardPool)
    {
        // Initialize with passed in parameters.
        this.context = context;
        this.openedCardPool = openedCardPool;
    }

    @Override
    public int getCount()
    {
        return openedCardPool.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    // Create an ImageView for each card in getView().
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView cardView;
        if(convertView == null)
        {
            // Set some image attributes. First, scale the image properly to the right size.
            cardView = new ImageView(context);
            cardView.setLayoutParams(new GridView.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT));
            // Set how the image is cropped/scaled to fit in the item.
            cardView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        else
        {
            cardView = (ImageView) convertView;
        }

        // Loading full-size Bitmaps takes up an abundance of memory, leading to OOM.
        // Calculate and use smaller resolution versions of images.


        // Find the card in the list of opened cards that correlates with the specific card being displayed, and use it's id to find its file name in the resources.
        String cardImageFileName = IXALAN_LOW_RES_IMAGES + openedCardPool.get(position).getId();
        // Set the image of the item in the GridView to this card's image.
        cardView.setImageResource(context.getResources().getIdentifier(cardImageFileName, "drawable", context.getPackageName()));
        return cardView;
    }

    // Used to change the card list that is displayed. Used for Draft Simulator.
    public void changeCardList(ArrayList<Card> newOpenedCardPool)
    {
        openedCardPool = newOpenedCardPool;
    }
}
