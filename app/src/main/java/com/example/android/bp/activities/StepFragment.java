package com.example.android.bp.activities;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.android.bp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        {

    BarChart chart;
    public ArrayList<BarEntry> entries = new ArrayList<>();
    public ArrayList<String> labels = new ArrayList<String>();
    public int weekStep = 0;
    public int total = 0;


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
//        initViews();


        View v = inflater.inflate(R.layout.fragment_step, container, false);
        chart =  (BarChart) v.findViewById(R.id.chart);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context cont = getContext();
        mGoogleApiClient = new GoogleApiClient.Builder(cont)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient1 =mGoogleApiClient;
        new ViewWeekStepCountTask().execute();
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

                    BarDataSet dataset = new BarDataSet(entries, "# of Steps");
                    BarData data = new BarData(labels, dataset);

                    dataset.setColors(ColorTemplate.LIBERTY_COLORS);
//
                    chart.setData(data);

//
//                    chart.notifyDataSetChanged();
//                    progressBar.setMax(80000);
                    for(int i :weekStep){
                        total = total + i;
                    }
//                    progressBar.setProgress(total);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            week_total_text.setText(String.valueOf(total));
//                            week_total_text.append(" Steps");
//
//                        }
//                    });


                }



                //Used for non-aggregated data
                else if (dataReadResult.getDataSets().size() > 0) {
                    Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                    for (DataSet dataSet : dataReadResult.getDataSets()) {

                    }
                }


            }

            private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
                protected Void doInBackground(Void... params) {
                    displayLastWeeksStepsData();

                    return null;
                }
            }

}