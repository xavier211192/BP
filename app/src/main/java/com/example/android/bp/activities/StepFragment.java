package com.example.android.bp.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.XAxis;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;

import com.jjoe64.graphview.series.LineGraphSeries;

import com.example.android.bp.R;
import android.view.LayoutInflater;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import com.example.android.bp.R;
import com.jjoe64.graphview.series.BarGraphSeries;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,TextToSpeech.OnInitListener
{


    private String textToSpeech;
    BarChart chart;
    public ArrayList<BarEntry> entries = new ArrayList<>();
    public ArrayList<String> labels = new ArrayList<String>();

    public  int[] weekStep = new int[10];
    public  String[] date = new String[10];
//    public int weekStep = 0;
    TextView totalTextView;
    TextView averageTextView;
    TextView averageDistanceView;
    TextView todayDistanceView;
    TextView todayTextView;
    ProgressBar progressBar ;
    ImageView trendup;
    ImageView trenddown;
    ImageButton speakEnglish;
    ImageButton speakPolish;

    public String todayCount;
    public String todayDistance;
    public String averageSteps;
    public int total = 0;
    public int today = 0;
    public int averageC = 0;
    public int yesterday = 0;
    public double distanceToday = 0;
    public double distanceAvg = 0;
    private TextToSpeech myTTS;
    private String englishTTS;
    private String polishTTS;
    private int MY_DATA_CHECK_CODE = 0;
    public GoogleApiClient mGoogleApiClient;

    public static GoogleApiClient mGoogleApiClient1;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;


    public StepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step, container, false);
        chart =  (BarChart) v.findViewById(R.id.chartx);
        todayTextView = (TextView) v.findViewById(R.id.todayStepsText);
        averageTextView = (TextView) v.findViewById(R.id.averageStepsTextView);
        totalTextView = (TextView) v.findViewById(R.id.totalStepsTextView);
        progressBar = (ProgressBar) v.findViewById(R.id.cPBS);
        trenddown = (ImageView) v.findViewById(R.id.trenddownsteps);
        trendup = (ImageView) v.findViewById(R.id.trendupsteps);
        trendup.setVisibility(View.INVISIBLE);
        trenddown.setVisibility(View.INVISIBLE);
        averageDistanceView = (TextView) v.findViewById(R.id.avgdistanceView);
        todayDistanceView = (TextView) v.findViewById(R.id.todayDistanceView);
        speakEnglish = (ImageButton) v.findViewById(R.id.speakEnglish);
        speakEnglish.setOnClickListener(this);
        speakPolish = (ImageButton) v.findViewById(R.id.speakPolish);
        speakPolish.setOnClickListener(this);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context cont = getContext();
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent,MY_DATA_CHECK_CODE);
        mGoogleApiClient = new GoogleApiClient.Builder(cont)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient1 =mGoogleApiClient;
       new ViewWeekStepCountTask().execute();
    }

    public void onInit(int initStatus){
        if(initStatus == TextToSpeech.SUCCESS){
            myTTS.setLanguage(Locale.UK);
        }else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(getActivity(), "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    // onActivityResult method for text to speech
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(getActivity(), this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    private void displayLastWeeksStepsData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        long startTime = cal.getTimeInMillis();
//        int weekStep = 0;
        int stepCount;
        total = 0;
        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
//                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);
        int size = dataReadResult.getBuckets().size();
        int count=0;
//        int[] weekStep = new int[10];
//        String[] date = new String[10];
        //Used for aggregated data
        if (size > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for ( Bucket bucket : dataReadResult.getBuckets()) {

                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
//                    stepCount = showDataSet(dataSet);
//                    weekStep = weekStep + stepCount;
                    //Changes


                    for (DataPoint dp : dataSet.getDataPoints()) {
                        Log.e("History", "Data point:");

                        Log.e("History", "\tType: " + dp.getDataType().getName());
                        Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                        for (Field field : dp.getDataType().getFields()) {
                            Log.e("History", "\tField: " + field.getName() +
                                    " Value: " + dp.getValue(field));

                            int temp = dp.getValue(field).asInt();
                            weekStep[count] = temp;

                            entries.add(new BarEntry(weekStep[count], count));
                            date[count] = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
                            labels.add(date[count]);
                            count ++;


                        }
                    }

                }


            }

            BarDataSet dataset = new BarDataSet(entries, "No of Steps");
            dataset.setBarSpacePercent(75f);
            BarData data = new BarData(labels, dataset);

//            dataset.setColors(ColorTemplate.LIBERTY_COLORS);
            dataset.setColor(Color.parseColor("#9575cd"));
            chart.setDrawGridBackground(false);
            chart.getXAxis().setDrawGridLines(false);
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisRight().setDrawGridLines(false);
            chart.setDrawBorders(false);
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawAxisLine(false);
            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getAxisRight().setDrawAxisLine(false);
            chart.setDescription("");

            chart.setData(data);


            //
//                    chart.notifyDataSetChanged();
//                    progressBar.setMax(80000);
            for(int i=0; i<8; i++){
                if(i==6){
                    yesterday = weekStep[i];
                }
                if(i==7){
                    today = weekStep[i];
                }
                total = total + weekStep[i];
            }

            averageC = total/8;
            averageSteps = String.valueOf(averageC);
            todayCount = String.valueOf(today);
            distanceAvg = averageC*0.000762;
            distanceToday = Math.floor(today*0.0007620);
            todayDistance = String.valueOf(distanceToday);
            progressBar.setMax(80000);
            progressBar.setProgress(total);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    totalTextView.setText(String.valueOf(total));
                    averageTextView.setText(String.valueOf(averageC + " steps"));
                    todayTextView.setText(String.valueOf(today + " steps"));
                    if(today > yesterday){
                        trendup.setVisibility(View.VISIBLE);
                    }else trenddown.setVisibility(View.VISIBLE);
                    todayDistanceView.setText(new DecimalFormat("##.##").format(distanceToday)+ " km");
                    averageDistanceView.setText(new DecimalFormat("##.##").format(distanceAvg) + " km");


                }
            });
//            Intent i = new Intent(getActivity(),CombinedFragment.class);
//            i.putExtra("numbers",weekStep);
//            startActivity(i);

        }


//


        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {

            }
        }

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.speakEnglish: {
                myTTS.setLanguage(Locale.UK);
                englishTTS = " Hi, Your step count for today is:" + todayCount+ "and you have travelled," + todayDistance+",kilometres. You average," + averageSteps + ",steps,this week";


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    myTTS.speak(englishTTS, TextToSpeech.QUEUE_FLUSH, null, null);
                }else {
                    myTTS.speak(englishTTS,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
            break;
            case R.id.speakPolish: {
                myTTS.setLanguage(new Locale("pl_PL"));
                polishTTS = " CZEŚĆ, Twój krok liczą się na dziś:" + todayCount+ ",i podróżowałeś,"+ todayDistance+",kilometry. Średnio," +averageSteps+" kroków w tym tygodniu" ;


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    myTTS.speak(polishTTS, TextToSpeech.QUEUE_FLUSH, null, null);
                }else {
                    myTTS.speak(polishTTS,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
            break;

        }


    }


    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksStepsData();


            return null;
        }
    }

}
