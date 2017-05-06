package com.example.android.bp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.bp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prashanth on 5/3/2017.
 */

public class diabetesinfoActivity  extends AppCompatActivity{


    RecyclerView rv;
    private List<Cards> cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diabetesinfo);
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();
        initializeAdapter();
    }


    private void initializeData(){
        cards = new ArrayList<>();
        cards.add(new Cards("Common Symptoms", "Fatigue\nBlurred Vision\nSores that dont heal\nIncreased urination\nUnexplained weightloss\nBlurred Vision", R.drawable.symptoms));
        cards.add(new Cards("Glucose Range Fasting", "80-130mg/dl(4.5-7.2 mmol/L)", R.drawable.logo6));
        cards.add(new Cards("Glucose Range After Meals", "Less than 180mg/dl(10.0 mmol/L)", R.drawable.logo6));
        cards.add(new Cards("Diabetic Diet","Fresh fruits and vegetables\nCooked beans and peas\nWhole-grain breads\nBrown rice\nBran foods",R.drawable.logo6));
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(cards);
        rv.setAdapter(adapter);
    }
 }