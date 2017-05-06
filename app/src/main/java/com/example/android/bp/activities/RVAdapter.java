package com.example.android.bp.activities;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bp.R;

import java.util.List;

/**
 * Created by Prashanth on 5/3/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardsViewHolder>{

    public static class CardsViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;

        CardsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }
   List <Cards> cards;

    RVAdapter(List<Cards> cards){
        this.cards = cards;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CardsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        CardsViewHolder pvh = new CardsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CardsViewHolder cardsViewHolder, int i) {
        cardsViewHolder.personName.setText(cards.get(i).name);
        cardsViewHolder.personAge.setText(cards.get(i).age);
        cardsViewHolder.personPhoto.setImageResource(cards.get(i).photoId);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
