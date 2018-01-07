package com.example.moham.magicdrafter.Model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moham.magicdrafter.MyDeckActivity;
import com.example.moham.magicdrafter.MyDeckActivity;
import com.example.moham.magicdrafter.R;
import com.example.moham.magicdrafter.Model.Deck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moham on 2018-01-03.
 */

public class DeckAdapter extends ArrayAdapter<Deck> {

    private Context context;
    private int resource;

    public DeckAdapter(MyDeckActivity context, int resource, List<Deck> mydecks) {
        super(context, resource, mydecks);

        this.context = context;
        this.resource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //get layout inflater from the activity
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }


        Deck deck = getItem(position);

        TextView txtdeckName = convertView.findViewById(R.id.txt_deckname);
        txtdeckName.setText(deck.getDeckName());

        TextView txtdecknumber = convertView.findViewById(R.id.txt_decknumberofcards);
        txtdecknumber.setText(Integer.toString(deck.getNocards()));

        TextView txtdesc = convertView.findViewById(R.id.txt_desc);
        txtdesc.setText(deck.getDeckdesc());

        return convertView;
    }
}