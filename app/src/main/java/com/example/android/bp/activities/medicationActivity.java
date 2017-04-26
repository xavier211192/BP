package com.example.android.bp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.android.bp.R;

/**
 * Created by Prashanth on 4/9/2017.
 */

public class medicationActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        FloatingActionButton logGlucose = (FloatingActionButton)  findViewById(R.id.addLogButton);
        logGlucose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(medicationActivity.this, logMedActivity.class);
                startActivity(i);

            }
        });

    }






}