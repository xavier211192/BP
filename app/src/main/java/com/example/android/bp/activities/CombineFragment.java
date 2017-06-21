package com.example.android.bp.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.*;
import com.jjoe64.graphview.series.LineGraphSeries;
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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;



import com.example.android.bp.R;
import com.jjoe64.graphview.series.BarGraphSeries;


import com.example.android.bp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CombineFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    int [] numbers;
    //int[] weekStep = new int[]{9000, 2500, 3000, 6000, 9000, 5000, 10000, 7500, 9000, 9500};
    int [] weekStep = new int[8];
    int[] glucose = new int[]{85, 185, 170, 160, 120, 130, 165, 200};
    int count =0;
    int countg = 0;
    private GraphView graph;
    private BarGraphSeries<com.jjoe64.graphview.series.DataPoint> series;
    private LineGraphSeries<com.jjoe64.graphview.series.DataPoint> series2;
    private com.jjoe64.graphview.series.DataPoint[] valuesG = new com.jjoe64.graphview.series.DataPoint[8];
    private com.jjoe64.graphview.series.DataPoint[] values = new com.jjoe64.graphview.series.DataPoint[8];
    private int[] array;


    public GoogleApiClient mGoogleApiClient;

    public static GoogleApiClient mGoogleApiClient1;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    public CombineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_combine, container, false);
        graph = (GraphView) v.findViewById(R.id.graph);
//        Bundle extras = getActivity().getIntent().getExtras();
//        array = extras.getIntArray("numbers");
//        numbers = StepFragment.getActivityInstance().getData();
        //initGraph();
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


    public void initGraph() {


        for (int i = 0; i < weekStep.length; i++) {
            int in = weekStep[i];

            values[count] = new com.jjoe64.graphview.series.DataPoint(count, in);
            count++;

        }
        series = new BarGraphSeries<>(values);
        graph.addSeries(series);
       // graph.getViewport().setMaxY(15000);
       // graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setScalableY(true);
        series.setColor(Color.parseColor("#9575cd"));
        series.setSpacing(75);
        series.setAnimated(true);
        series.setDataWidth(1);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);

        staticLabelsFormatter.setHorizontalLabels(new String[] {"13 June", "14 June", "15 June","16 June","17 June","18 June","19 June","20 June"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        for(int j = 0; j<glucose.length;j++){
            int in = glucose[j];
            valuesG[countg] = new com.jjoe64.graphview.series.DataPoint(countg,in);
            countg++;
        }
        series2 = new LineGraphSeries<>(valuesG);

        graph.getSecondScale().addSeries(series2);
// the y bounds are always manual for second scale
        graph.getSecondScale().setMinY(50);
        graph.getSecondScale().setMaxY(300);
        series2.setColor(Color.parseColor("#4dd0e1"));
        series2.setDrawDataPoints(true);
        //series2.setAnimated(true);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.parseColor("#0d47a1"));
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.parseColor("#0d47a1"));
        graph.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
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
        int total = 0;
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
                                                        count++;


                        }
                    }

                }

            }
            }
            else if (dataReadResult.getDataSets().size() > 0) {
                Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                for (DataSet dataSet : dataReadResult.getDataSets()) {

                }
            }

        }
    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksStepsData();
            initGraph();
            return null;
        }
    }
}