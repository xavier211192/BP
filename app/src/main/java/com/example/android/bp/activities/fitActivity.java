package com.example.android.bp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import static java.lang.Math.toIntExact;

import com.example.android.bp.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
// New for TTS
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.util.Locale;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Prashanth on 4/11/2017.
 */

public class fitActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,OnInitListener {

    private Button mButtonViewWeek;
    private int time = 0;
    private int speechCount=0;
    private String firsttextToSpeech;
    private String textToSpeech;
    private int weekStep = 0;
    private Button mButtonViewToday;
    private Button mButtonViewCalories;
    private Button mButtonViewGlucose;
    private Button mButtonViewMedication;
    //    private Button mButtonAddSteps;
//    private Button mButtonUpdateSteps;
//    private Button mButtonDeleteSteps;
    private TextView mDisp;
    private TextView mDisplay;
    private ProgressBar progressBar;


    public GoogleApiClient mGoogleApiClient;

    public static GoogleApiClient mGoogleApiClient1;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    //Text to speech
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit);
        initViews();
        initTime();

        //text to speech--start
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent,MY_DATA_CHECK_CODE);
// tts--end
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        mGoogleApiClient1 = mGoogleApiClient;
    }

//init Time method
    public void initTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            time = 0;
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            time = 1;
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            time = 2;
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            time = 3;
        }
    }
// Onint for text to speech
public void onInit(int initStatus){
    if(initStatus == TextToSpeech.SUCCESS){
        myTTS.setLanguage(Locale.US);
    }else if (initStatus == TextToSpeech.ERROR) {
        Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
    }
}

// onActivityResult method for text to speech
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


    private void initViews() {
        mButtonViewWeek = (Button) findViewById(R.id.btn_view_week);
        mButtonViewToday = (Button) findViewById(R.id.btn_view_today);
        mButtonViewCalories = (Button) findViewById(R.id.btn_view_calories);
        mButtonViewGlucose = (Button) findViewById(R.id.btn_view_glucose);
        mButtonViewMedication = (Button) findViewById(R.id.btn_view_medication);
        mDisp = (TextView) findViewById(R.id.dispTextView);
        progressBar = (ProgressBar) findViewById(R.id.cPB);
        mDisplay = (TextView) findViewById(R.id.dispTextView1);


//        mButtonAddSteps = (Button) findViewById(R.id.btn_add_steps);
//        mButtonUpdateSteps = (Button) findViewById(R.id.btn_update_steps);
//        mButtonDeleteSteps = (Button) findViewById(R.id.btn_delete_steps);

        mButtonViewWeek.setOnClickListener(this);
        mButtonViewToday.setOnClickListener(this);
        mButtonViewCalories.setOnClickListener(this);
//        mButtonAddSteps.setOnClickListener(this);
//        mButtonUpdateSteps.setOnClickListener(this);
//        mButtonDeleteSteps.setOnClickListener(this);
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    //In use, call this every 30 seconds in active mode, 60 in ambient on watch faces
    private void displayStepDataForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA).await(1, TimeUnit.MINUTES);

        if (result.getStatus().isSuccess()) {
            DataSet totalSet = result.getTotal();
            long total = totalSet.isEmpty()
                    ? 0
                    : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
//
            double total1 = (((double) total) / 10000) * 100;

            int bar = (int) total1;
            progressBar.setProgress(bar);
            final String yes = String.valueOf(total);
            // check for time of the day and switch accordingly text to speech
            textToSpeech = "You have taken,"+yes+",steps";
            switch (time)
            {
                case 0: firsttextToSpeech = "Good Morning! Your goal is 10,000 steps today." +textToSpeech+" today.Good Luck!";
                        break;

                case 1: firsttextToSpeech = "Good Afternoon!"+textToSpeech+ ",today. remember your goal!";
                    break;
                case 2: firsttextToSpeech = "Good Evening!" +textToSpeech+ "today. remember your goal!";
                    break;
                case 3: firsttextToSpeech = textToSpeech+",today. Get some rest. Good Night!";
                    break;
            }

            if(speechCount==0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    myTTS.speak(firsttextToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
                    speechCount++;
                } else {
                    myTTS.speak(firsttextToSpeech, TextToSpeech.QUEUE_FLUSH, null);
                    speechCount++;
                }
            } else  {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    myTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    myTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);

                }

            }
            // text to speech end
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDisp.setText(yes);
                    mDisplay.setText("Your goal 10,000 steps");
                }
            });

//            showDataSet(result.getTotal());
        } else {
            mDisp.setText("Error Loading Data");
        }
    }


    //get calorie count


    private void displayCalorieDataForToday() {


        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(fitActivity.mGoogleApiClient1, DataType.TYPE_CALORIES_EXPENDED).await(1, TimeUnit.MINUTES);

        if (result.getStatus().isSuccess()) {
            DataSet totalSet = result.getTotal();
            float total = totalSet.isEmpty()
                    ? 0
                    : totalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();

            int calorieCount = Math.round(total);
            final String yes = String.valueOf(calorieCount);
            //text to speech--start
            textToSpeech = "You have burnt,"+yes+",calories!";
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                myTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                myTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);

            }
            // text to speech end

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mDisp.setText("");
                    mDisp.setText(yes);
                    mDisplay.setText("Calories burnt today");
                }
            });

//            showDataSet(result.getTotal());
        } else {
            mDisplay.setText("Error Loading Data");
        }
    }


    private void displayLastWeeksData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        weekStep = 0;
        int stepCount;

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    stepCount = showDataSet(dataSet);
                    weekStep = weekStep + stepCount;
                }

            }

        }


        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {

            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String yes = String.valueOf(weekStep);
                mDisplay.setText(yes);
//                mButtonViewWeek.setEnabled(false);
            }
        });

    }


    private int showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        int weekStep = 0;
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");

            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));

                weekStep = dp.getValue(field).asInt();
            }
        }
        return weekStep;
    }

    private void addStepDataToGoogleFit() {
        //Adds steps spread out evenly from start time to end time
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName("Step Count")
                .setType(DataSource.TYPE_RAW)
                .build();

        int stepCountDelta = 1000000;
        DataSet dataSet = DataSet.create(dataSource);

        DataPoint point = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        point.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(point);

        Status status = Fitness.HistoryApi.insertData(mGoogleApiClient, dataSet).await(1, TimeUnit.MINUTES);

        if (!status.isSuccess()) {
            Log.e("History", "Problem with inserting data: " + status.getStatusMessage());
        } else {
            Log.e("History", "data inserted");
        }
    }

    private void updateStepDataOnGoogleFit() {
        //If two entries overlap, the new data is dropped when trying to insert. Instead, you need to use update
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName("Step Count")
                .setType(DataSource.TYPE_RAW)
                .build();

        int stepCountDelta = 2000000;
        DataSet dataSet = DataSet.create(dataSource);

        DataPoint point = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        point.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(point);

        DataUpdateRequest updateRequest = new DataUpdateRequest.Builder().setDataSet(dataSet).setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS).build();
        Fitness.HistoryApi.updateData(mGoogleApiClient, updateRequest).await(1, TimeUnit.MINUTES);
    }

    private void deleteStepDataOnGoogleFit() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        Fitness.HistoryApi.deleteData(mGoogleApiClient, request).await(1, TimeUnit.MINUTES);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view_week: {
                new ViewWeekStepCountTask().execute();
                break;
            }
            case R.id.btn_view_today: {
                new ViewTodaysStepCountTask().execute();
                break;
            }
            case R.id.btn_view_calories: {
                new ViewCaloriesTask().execute();
                break;
            }
//            case R.id.btn_update_steps: {
//                new UpdateStepsOnGoogleFitTask().execute();
//                break;
//            }
//            case R.id.btn_delete_steps: {
//                new DeleteYesterdaysStepsTask().execute();
//                break;
//            }
        }
    }

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData();
            return null;
        }
    }

    private class ViewTodaysStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayStepDataForToday();
            return null;
        }
    }


    private class ViewCaloriesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayCalorieDataForToday();
            return null;
        }
    }


    private class AddStepsToGoogleFitTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            addStepDataToGoogleFit();
            displayLastWeeksData();
            return null;
        }
    }

    private class UpdateStepsOnGoogleFitTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            updateStepDataOnGoogleFit();
            displayLastWeeksData();
            return null;
        }
    }

    private class DeleteYesterdaysStepsTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            deleteStepDataOnGoogleFit();
            displayLastWeeksData();
            return null;
        }
    }

    //open glucose log
    public void glucoseActivity(View view) {
        Intent i = new Intent(fitActivity.this, glucoseActivity.class);
        startActivity(i);
    }

    public void medicationActivity(View view) {
        Intent i = new Intent(fitActivity.this, medicationActivity.class);
        startActivity(i);
    }

    public void testActivity(View view) {
        Intent i = new Intent(fitActivity.this, DiagnosisActivity.class);
        startActivity(i);
    }



    public void diabetesinfoActivity(View view) {
        Intent i = new Intent(fitActivity.this, diabetesinfoActivity.class);
        startActivity(i);
    }


    public void graphActivity(View view) {
        Intent i = new Intent(fitActivity.this, graphActivity.class);
        startActivity(i);
    }

    public void ttsActivity(View view) {
        Intent i = new Intent(fitActivity.this, ttsActivity.class);
        startActivity(i);
    }

}