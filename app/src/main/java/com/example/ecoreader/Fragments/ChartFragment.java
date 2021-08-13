package com.example.ecoreader.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecoreader.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ChartFragment extends Fragment {
    private LineChart mChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        mChart = view.findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getAxisLeft().setTextColor(Color.WHITE);
        mChart.getXAxis().setTextColor(Color.WHITE);
        mChart.getLegend().setTextColor(Color.WHITE);
        mChart.getAxisRight().setTextColor(Color.WHITE);
        mChart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
        mChart.getDescription().setText("");
        initData();
        return view;
    }

    private void initData() {
        LineDataSet lineDataSet1 = new LineDataSet(getDataSet(), "Data Set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        mChart.setData(data);
        mChart.invalidate();
        mChart.getData().setValueTextColor(Color.WHITE);
    }

    private ArrayList<Entry> getDataSet() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1628741039, 50));
        values.add(new Entry(1628744639, 100));
        return values;
    }

    private class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // Convert float value to date string
            // Convert from seconds back to milliseconds to format time  to show to the user
            long emissionsMilliSince1970Time = ((long) value) * 1000;

            // Show time in local version
            Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
            DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

            return dateTimeFormat.format(timeMilliseconds);
        }
    }
}
