package com.example.android.bp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android.bp.R;

/**
 * Created by Prashanth on 5/25/2017.
 */

public class DiagnosisActivity extends AppCompatActivity {


    private double weightkg;
    private double heightcm;
    private double bmi;
    private int score = 0;
    private RadioGroup radioGender;
    private RadioGroup radioAge;
    private RadioGroup radioEthnic;
    private RadioGroup radioHistory;
    private RadioGroup radioWaist;
    private RadioGroup radioBloodpressure;
    private RadioButton male;
    private RadioButton female;
    private RadioButton younger49;
    private RadioButton age59;
    private RadioButton age69;
    private RadioButton older70;
    private RadioButton southasian;
    private RadioButton black;
    private RadioButton chinese;
    private RadioButton mixed;
    private RadioButton white;
    private RadioButton none_ethnic;
    private RadioButton history_yes;
    private RadioButton history_no;
    private RadioButton bloodpressure_yes;
    private RadioButton bloodpressure_no;
    private RadioButton waistless90;
    private RadioButton waist99;
    private RadioButton waist109;
    private RadioButton waist110;
    private RadioButton genderRButton;
    private RadioButton ageRButton;
    private RadioButton ethnicRButton;
    private RadioButton waistRButton;
    private RadioButton historyRButton;
    private RadioButton bloodpressureRButton;
    private EditText weight;
    private EditText height;
    private int gender;
    private String selectedGender;
    private int age;
    private String selectedAge;
    private int ethnicity;
    private String selectedEthnicity;
    private int waist;
    private String selectedWaist;
    private int history;
    private String selectedHistory;
    private int bloodPressure;
    private String selectedbloodPressure;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);
        initViews();
    }

    public void initViews(){
        radioGender = (RadioGroup)findViewById(R.id.radio_gender);
        radioAge = (RadioGroup) findViewById(R.id.radio_age);
        radioEthnic = (RadioGroup) findViewById(R.id.radio_ethnic);
        radioHistory =(RadioGroup) findViewById(R.id.radio_history);
        radioWaist = (RadioGroup) findViewById(R.id.radio_waist);
        radioBloodpressure = (RadioGroup) findViewById(R.id.radio_bloodpressure);
        weight =(EditText) findViewById(R.id.weight);
        height = (EditText) findViewById(R.id.height);

    }

    public  void  calculateRisk(View v){

        gender = radioGender.getCheckedRadioButtonId();
        genderRButton = (RadioButton) findViewById(gender);
        selectedGender = (String)genderRButton.getText();
        weightkg = Double.parseDouble(weight.getText().toString());
        heightcm = Double.parseDouble(height.getText().toString());
        heightcm =heightcm/100;
        bmi = (weightkg/(heightcm*heightcm));
        age = radioAge.getCheckedRadioButtonId();
        ageRButton = (RadioButton) findViewById(age);
        selectedAge = (String)ageRButton.getText();
        ethnicity = radioEthnic.getCheckedRadioButtonId();
        ethnicRButton = (RadioButton)findViewById(ethnicity);
        selectedEthnicity = (String)ethnicRButton.getText();
        waist = radioWaist.getCheckedRadioButtonId();
        waistRButton = (RadioButton)findViewById(waist);
        selectedWaist = (String) waistRButton.getText();
        history = radioHistory.getCheckedRadioButtonId();
        historyRButton = (RadioButton)findViewById(history);
        selectedHistory = (String)historyRButton.getText();
        bloodPressure = radioBloodpressure.getCheckedRadioButtonId();
        bloodpressureRButton = (RadioButton)findViewById(bloodPressure);
        selectedbloodPressure = (String)bloodpressureRButton.getText();
        //scoring for gender
        if(selectedGender.equalsIgnoreCase("Male"))
        {
            score = score+1;
        }

//Scoring for bmi
        if (bmi >25 && bmi <29.9){

            score = score+3;
        }else if (bmi>29.9 && bmi <34.9){
            score = score + 5;
        }else if  (bmi>34.9) {
            score = score +8;
        }

// scoring for age group
        if(selectedAge.equalsIgnoreCase("50-59")){
            score = score+5;
        }else if (selectedAge.equalsIgnoreCase("60-69")){
            score = score+9;
        }else if (selectedAge.equalsIgnoreCase("70 or older")){
            score = score+13;
        }

//scoring for ethnicity
        if(!selectedEthnicity.equalsIgnoreCase("White")){
            score= score +6;
        }
// scoring for history
        if(selectedHistory.equalsIgnoreCase("yes")){
            score = score+5;
        }
// scoring for waist
        if(selectedWaist.equalsIgnoreCase("90 - 99.9cm(35.5 - 39.3in)")){
            score = score +4;
        }else if(selectedWaist.equalsIgnoreCase("100 - 109.9cm(39.4 - 43.3in)")){
            score = score +6;
        }else if(selectedWaist.equalsIgnoreCase("110cm(43.4in) or above")){
            score = score+9;
        }
// scoring for bloodPressure
        if(selectedbloodPressure.equalsIgnoreCase("Yes")){
            score = score+5;
        }


        Intent i = new Intent(DiagnosisActivity.this,lowriskActivity.class);
        i.putExtra("Score", score);
        startActivity(i);
    }

}