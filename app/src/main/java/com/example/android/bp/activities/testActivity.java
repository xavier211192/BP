package com.example.android.bp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bp.R;

/**
 * Created by Prashanth on 4/9/2017.
 */

public class testActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.cPB);
        progressBar.setProgress(45);

        TextView txtView = (TextView)findViewById(R.id.textView1);
        txtView.setText("50%");

    }
}