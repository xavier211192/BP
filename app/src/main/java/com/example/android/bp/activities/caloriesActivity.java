package com.example.android.bp.activities;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by Prashanth on 4/11/2017.
 */

public class caloriesActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    Button mButtonViewWeek;
//    BarChart chart;
    ProgressBar progressBar ;
    TextView week_total_text;
    LineChart chart;
    public  ArrayList<Entry> entries = new ArrayList<>();
    public ArrayList<String> labels = new ArrayList<String>();


    public  int weekStep = 0;
    public int total = 0;



    public GoogleApiClient mGoogleApiClient;

    public static GoogleApiClient mGoogleApiClient1;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
        initViews();
//        displayLastWeeksData();



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        mGoogleApiClient1 =mGoogleApiClient;
    }

    private void initViews() {
        mButtonViewWeek = (Button) findViewById(R.id.btn_view_week);
        mButtonViewWeek.setOnClickListener(this);
//        chart =  (BarChart) findViewById(R.id.chart);
        chart =  (LineChart) findViewById(R.id.chart);
//        new ViewWeekStepCountTask().execute();
        progressBar = (ProgressBar) findViewById(R.id.progress_calories);
         week_total_text = (TextView) findViewById(R.id.total);

    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    //In use, call this every 30 seconds in active mode, 60 in ambient on watch faces





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
//        int weekStep = 0;
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

        int[] weekStep = new int[10];
        String[] date = new String[10];
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

//            BarDataSet dataset = new BarDataSet(entries, "# of Steps");
            LineDataSet dataset = new LineDataSet(entries, "# of Calories");
//            BarData data = new BarData(labels, dataset);
//            LineData data = new LineData(labels, dataset);
            chart.setNoDataText("Touch to generate graph");
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
//            dataset.setDrawCubic(true);
            dataset.setDrawFilled(true);
//            chart.setDescription("Number of Steps over the week");
//            chart.setData(data);
            progressBar.setMax(16000);
            for(int i :weekStep){
                total = total + i;
            }
            progressBar.setProgress(total);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    week_total_text.setText(String.valueOf(total));
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

//
        }
    }

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData();
            return null;
        }
    }



    //open glucose log

}