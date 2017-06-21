package com.example.android.bp.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import static java.lang.Math.toIntExact;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import com.example.android.bp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalorieFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,TextToSpeech.OnInitListener
{

//    BarChart chart;
    private String textToSpeech;
    LineChart chart;
    ProgressBar progressBar;
    TextView totalTextView;
    TextView averageTextView;
    TextView todayTextView;
    ImageView trendup;
    ImageView trenddown;
    ImageButton speakEnglish;
    ImageButton speakPolish;
   // ArrayAdapter <String> adapter ;
    ListView listView;
   // public ArrayList<BarEntry> entries = new ArrayList<>();
    public ArrayList<Entry> entries = new ArrayList<>();

    public ArrayList<String> labels = new ArrayList<String>();
    //public int weekStep = 0;
    public int total = 0;
    public int today = 0;
    public int averageC = 0;
    public String todayCount;
    public String averageCal;
    public int yesterday = 0;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private String englishTTS;
    private String polishTTS;

    int[] weekStep = new int[10];
    String[] date = new String[10];
    public GoogleApiClient mGoogleApiClient;

    public static GoogleApiClient mGoogleApiClient1;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;

    public CalorieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        initViews();


        View v = inflater.inflate(R.layout.fragment_calorie, container, false);
       // chart =  (BarChart) v.findViewById(R.id.chart);
        chart = (LineChart) v.findViewById(R.id.chart);
        progressBar = (ProgressBar) v.findViewById(R.id.cPB);
        totalTextView =(TextView) v.findViewById(R.id.totalTextView);
        averageTextView = (TextView) v.findViewById(R.id.averageCaloriesTextView);
        todayTextView = (TextView) v.findViewById(R.id.todayCaloriesText);
        trendup = (ImageView)v.findViewById(R.id.trendup);
        trenddown = (ImageView) v.findViewById(R.id.trenddown);
        speakEnglish = (ImageButton) v.findViewById(R.id.speakEnglish);
        speakEnglish.setOnClickListener(this);
        speakPolish = (ImageButton) v.findViewById(R.id.speakPolish);
        speakPolish.setOnClickListener(this);
        trendup.setVisibility(View.INVISIBLE);
        trenddown.setVisibility(View.INVISIBLE);
        // listView = (ListView) v.findViewById(R.id.list_view);

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

    private void displayLastWeeksData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        long startTime = cal.getTimeInMillis();
     //   int weekStep = 0;
        int stepCount;
        total = 0;
        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);
        int size = dataReadResult.getBuckets().size();
        int count=0;


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
                                    " Value: " + dp.getValue(field).asFloat());

                            float temp = dp.getValue(field).asFloat();
                            int temp1 = (int)temp;
                            weekStep[count] = temp1;

                            entries.add(new Entry(weekStep[count], count));
//                            entries.add(new BarEntry(weekStep[count], count));
                            date[count] = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
                            labels.add(date[count]);
                            count ++;
                        }
                    }

                }


            }

           // BarDataSet dataset = new BarDataSet(entries, "# of Calories");
           LineDataSet dataset = new LineDataSet(entries, "Number of Calories");
          //  BarData data = new BarData(labels, dataset);
           LineData data = new LineData(labels, dataset);
//            chart.setNoDataText("Touch to generate graph");
//            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            dataset.setDrawCubic(true);
            dataset.setDrawFilled(true);
//            chart.setDescription("Number of Steps over the week");
            //chart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
           dataset.setFillAlpha(100);
            dataset.setFillColor(8280002);
            chart.setDrawGridBackground(false);
            chart.getXAxis().setDrawGridLines(false);
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisRight().setDrawGridLines(false);
            chart.setDrawBorders(false);
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawAxisLine(false);
            xAxis.setYOffset(15);
            chart.getAxisLeft().setDrawLabels(false);
            chart.getAxisRight().setDrawLabels(false);
            chart.setDescription("");
            chart.setExtraTopOffset(15);
            chart.setExtraLeftOffset(30);
            chart.setExtraRightOffset(30);
            chart.getAxisLeft().setStartAtZero(false);
            chart.getAxisLeft().setAxisMinValue(900);
            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getAxisRight().setDrawAxisLine(false);


            chart.setData(data);
//
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
            averageCal = String.valueOf(averageC);
            todayCount = String.valueOf(today);
            progressBar.setMax(16000);
            progressBar.setProgress(total);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    totalTextView.setText(String.valueOf(total));
                    averageTextView.setText(String.valueOf(averageC + " kcal"));
                    todayTextView.setText(String.valueOf(today + " kcal"));
                    if(today > yesterday){
                        trendup.setVisibility(View.VISIBLE);
                    }else trenddown.setVisibility(View.VISIBLE);
                }
            });

//            chart.animateXY(2000,2000);





        }



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
            englishTTS = " Hi, Your calorie count for today is:" + todayCount+ ",calories. You average," + averageCal + ",calories,this week";


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myTTS.speak(englishTTS, TextToSpeech.QUEUE_FLUSH, null, null);
            }else {
                myTTS.speak(englishTTS,TextToSpeech.QUEUE_FLUSH,null);
            }
            }
            break;
        case R.id.speakPolish: {
            myTTS.setLanguage(new Locale("pl_PL"));
            polishTTS = " CZEŚĆ, Twój kalorii jest dzisiaj:" + todayCount+ ". Ty przeciętnie," + averageCal + ",kalorie,w tym tygodniu";


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
            displayLastWeeksData();

            return null;
        }
    }

}