package com.example.android.bp.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.*;
import com.jjoe64.graphview.series.LineGraphSeries;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;



import com.example.android.bp.R;
import com.jjoe64.graphview.series.BarGraphSeries;


import com.example.android.bp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CombinedFragment extends Fragment {

    int[] weekStep = new int[]{9000, 2500, 3000, 6000, 9000, 5000, 10000, 7500, 9000, 9500};
    int[] glucose = new int[]{125, 185, 170, 160, 120, 130, 165, 200, 200, 200};
    int count =0;
    int countg = 0;
    private GraphView graph;
    private BarGraphSeries<com.jjoe64.graphview.series.DataPoint> series;
    private LineGraphSeries<com.jjoe64.graphview.series.DataPoint> series2;
    private com.jjoe64.graphview.series.DataPoint[] valuesG = new com.jjoe64.graphview.series.DataPoint[10];
    private com.jjoe64.graphview.series.DataPoint[] values = new com.jjoe64.graphview.series.DataPoint[10];


    public CombinedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_combined, container, false);
        graph = (GraphView) v.findViewById(R.id.graph);
        initGraph(graph);
        return v;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context cont = getContext();
       // initGraph(graph);

    }

    public void initGraph(GraphView graph) {


        for (int i = 0; i < weekStep.length; i++) {
            int in = weekStep[i];

            values[count] = new DataPoint(count, in);
            count++;

        }
        series = new BarGraphSeries<>(values);
        graph.addSeries(series);
        series.setSpacing(50);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"1", "2", "3","4","5","6","7","8","9","10"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        for(int j = 0; j<glucose.length;j++){
            int in = glucose[j];
            valuesG[countg] = new DataPoint(countg,in);
            countg++;
        }
        series2 = new LineGraphSeries<>(valuesG);

        graph.getSecondScale().addSeries(series2);
// the y bounds are always manual for second scale
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(200);
        series2.setColor(Color.RED);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
    }

}