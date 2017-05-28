package com.example.android.bp.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import com.example.android.bp.R;
import java.util.Locale;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Prashanth on 5/26/2017.
 */

public class ttsActivity extends AppCompatActivity implements OnClickListener,OnInitListener
{

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        Button speakButton = (Button)findViewById(R.id.speak);
         text  =(EditText) findViewById(R.id.enterW);
        //words = text.getText().toString();
        speakButton.setOnClickListener(this);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent,MY_DATA_CHECK_CODE);
}

    public void onClick(View v){
        String words = text.getText().toString();
        speakWords(words);
    }

    private void speakWords(String speech){

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            myTTS.speak(speech,TextToSpeech.QUEUE_FLUSH,null,null);
//        } else {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public void onInit(int initStatus){
        if(initStatus == TextToSpeech.SUCCESS){
            myTTS.setLanguage(Locale.US);
        }else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }


}
