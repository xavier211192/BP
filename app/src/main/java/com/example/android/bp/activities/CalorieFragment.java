package com.example.android.bp.activities;


import android.content.Context;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.concurrent.TimeUnit;
import com.example.android.bp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalorieFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

//    BarChart chart;
    LineChart chart;
    ProgressBar progressBar;
    TextView totalTextView;
   // ArrayAdapter <String> adapter ;
    ListView listView;
   // public ArrayList<BarEntry> entries = new ArrayList<>();
    public ArrayList<Entry> entries = new ArrayList<>();

    public ArrayList<String> labels = new ArrayList<String>();
    //public int weekStep = 0;
    public int total = 0;

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
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        totalTextView =(TextView) v.findViewById(R.id.total);
        // listView = (ListView) v.findViewById(R.id.list_view);

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

            if (count== 7) break;
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
//            adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.activity_listview,date);

            //listView.setAdapter(adapter);//            progressBar.setMax(16000);
//            for(int i :weekStep){
//                total = total + i;
//            }
//            progressBar.setMax(15000);
//            progressBar.setProgress(total);
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    totalTextView.setText(String.valueOf(total + " Total"));
//                }
//            });

//            chart.animateXY(2000,2000);





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
            displayLastWeeksData();

            return null;
        }
    }

}