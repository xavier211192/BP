package com.example.android.bp.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GlucoseFragment extends Fragment {


    public GlucoseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_glucose, container, false);
        return v;
    }

}
