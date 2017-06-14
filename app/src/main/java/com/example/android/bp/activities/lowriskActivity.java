package com.example.android.bp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bp.R;

/**
 * Created by Prashanth on 5/25/2017.
 */

public class lowriskActivity extends AppCompatActivity{

    private int score;
    LinearLayout layout_risk ;
    LinearLayout empty_one;
    LinearLayout empty_two;
    TextView risk_indicator ;
    TextView score_indicator;
    TextView risk_info;
    TextView help_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowrisk);
            Intent i = getIntent();
            score = i.getIntExtra("Score",0);
            layout_risk = (LinearLayout) findViewById(R.id.risk_indicator_layout);
            risk_indicator = (TextView) findViewById(R.id.risk_indicator_textview);
            score_indicator = (TextView) findViewById(R.id.score_indicator_textview);
            risk_info = (TextView) findViewById(R.id.risk_info);
            help_info = (TextView) findViewById(R.id.help_info);
            empty_one = (LinearLayout) findViewById(R.id.empty_linearone);
            empty_two = (LinearLayout) findViewById(R.id.empty_lineartwo);
            showRisk();
            }

    public  void showRisk() {

        if(score<= 6){
            layout_risk.setBackgroundColor(Color.parseColor("#66bb6a"));
            risk_indicator.setText(getString(R.string.risk_indicatorlow));
            score_indicator.setText(getString(R.string.score_indicator)+ " "+score);
            risk_info.setText(getString(R.string.info_lowrisk));
            help_info.setText(getString(R.string.help_low));
            empty_one.setBackgroundColor(Color.parseColor("#66bb6a"));
            empty_two.setBackgroundColor(Color.parseColor("#66bb6a"));


        }else if(score>=7 && score <=15){
            layout_risk.setBackgroundColor(Color.parseColor("#ffeb3b"));
            risk_indicator.setText(getString(R.string.risk_indicatorincreased));
            score_indicator.setText(getString(R.string.score_indicator)+ " "+score);
            risk_indicator.setTextColor(Color.parseColor("#212121"));
            score_indicator.setTextColor(Color.parseColor("#212121"));
            risk_info.setText(getString(R.string.info_increasedrisk));
            help_info.setText(getString(R.string.help_increased));
            empty_one.setBackgroundColor(Color.parseColor("#ffeb3b"));
            empty_two.setBackgroundColor(Color.parseColor("#ffeb3b"));
        }else if (score>=16 &&  score <=24){
            layout_risk.setBackgroundColor(Color.parseColor("#fb8c00"));
            risk_indicator.setText(getString(R.string.risk_indicatormoderate));
            score_indicator.setText(getString(R.string.score_indicator)+ " "+score);
            risk_indicator.setTextColor(Color.parseColor("#212121"));
            score_indicator.setTextColor(Color.parseColor("#212121"));
            risk_info.setText(getText(R.string.info_moderaterisk));
            help_info.setText(getText(R.string.help_moderate));
            empty_one.setBackgroundColor(Color.parseColor("#fb8c00"));
            empty_two.setBackgroundColor(Color.parseColor("#fb8c00"));
        }else {
            layout_risk.setBackgroundColor(Color.parseColor("#c62828"));
            risk_indicator.setText(getString(R.string.risk_indicatorhigh));
            score_indicator.setText(getString(R.string.score_indicator)+ " "+score);
            risk_indicator.setTextColor(Color.parseColor("#212121"));
            score_indicator.setTextColor(Color.parseColor("#212121"));
            risk_info.setText(getText(R.string.info_highrisk));
            help_info.setText(getText(R.string.help_high));
            empty_one.setBackgroundColor(Color.parseColor("#c62828"));
            empty_two.setBackgroundColor(Color.parseColor("#c62828"));
        }
    }
}
