package com.example.ecoreader.Fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.Application.GetDataService;
import com.example.ecoreader.DataRetrieval.GetRatesData;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRatesRequest;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;
import com.example.ecoreader.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ecoreader.Application.GetDataService.AUD_CODE;
import static com.example.ecoreader.Application.GetDataService.ECO_UPDATES;
import static com.example.ecoreader.Application.GetDataService.SAVED_RATES;
import static com.example.ecoreader.Application.GetDataService.SAVED_TIME_SERIES;
import static com.example.ecoreader.Application.GetDataService.USD_CODE;


public class ChartFragment extends Fragment implements FinishedRatesRequest, DateDialogFragment.onDateChosen {
    private static final String TAG = "ChartFragment";
    @Override
    public void dateResult(String date) {
        chosenDate = date;
        changeDate.setText("Change Starting Date ("+chosenDate+")");
    }

    @Override
    public void onReceivedRates(LatestRatesObject latestRatesObject) { //dw

    }

    @Override
    public void onReceivedTimeSeries(TimeSeriesObject timeSeriesObject, String currencyCode) { //this
        if (timeSeriesObject != null) {
            HashMap<String, HashMap<String, Float>> rates = timeSeriesObject.getRates();
            convertRatesToData(rates, currencyCode);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }
        txtNotiFetch.setVisibility(View.GONE);
        mChart.setVisibility(View.VISIBLE);
        changeDate.setVisibility(View.VISIBLE);
    }

    @Override
    public void availableCurrencies(HashMap<String, String> currenciesMap) { //dw

    }
    private String chosenDate = DateDialogFragment.getDate(Calendar.YEAR, -1);
    private LineChart mChart;
    private TextView changeDate, txtNotiFetch;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        mChart = view.findViewById(R.id.chart);
        changeDate = view.findViewById(R.id.btnDatePicker);
        txtNotiFetch = view.findViewById(R.id.txtNotiFetch);
        changeDate.setText("Change Starting Date ("+chosenDate+")");

        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialogFragment dateDialogFragment = new DateDialogFragment(ChartFragment.this);
                dateDialogFragment.show(getChildFragmentManager(), "");
            }
        });
        setupChart();
        return view;
    }

    private void setupChart() {
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getAxisLeft().setTextColor(Color.WHITE);
        mChart.getXAxis().setTextColor(Color.WHITE);
        mChart.getLegend().setTextColor(Color.WHITE);
        mChart.getAxisRight().setTextColor(Color.WHITE);
        mChart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
        mChart.setNoDataText("Fetching Graphical Data...");
        mChart.getDescription().setText("");
        mChart.getXAxis().setLabelCount(5);
        mChart.setNoDataText("");
        retrieveChartStats(USD_CODE);
    }

    public void retrieveChartStats(String currencyCode) {
        txtNotiFetch.setVisibility(View.VISIBLE);
        mChart.setVisibility(View.GONE);
        changeDate.setVisibility(View.GONE);
        new GetRatesData(this).execute(chosenDate, AUD_CODE, currencyCode);
    }

    private void convertRatesToData(HashMap<String, HashMap<String, Float>> rates, String currencyCode) {
        ArrayList<Entry> values = new ArrayList<>();
        HashMap<Float, HashMap<String, Float>> fixedRates = new HashMap<>();

        for (String date : rates.keySet()) {
            String toCode = new ArrayList<>(rates.get(date).keySet()).get(0);
            HashMap<String, Float> map = new HashMap<>();
            map.put(toCode, rates.get(date).get(toCode));
            fixedRates.put((float) dateToEpoch(date), map);
        }

        ArrayList<Float> sortedEpochDates = new ArrayList<>(fixedRates.keySet());
        Collections.sort(sortedEpochDates);

        for (Float floatItem : sortedEpochDates) {
            String toCode = new ArrayList<>(fixedRates.get(floatItem).keySet()).get(0);
            values.add(new Entry(floatItem, fixedRates.get(floatItem).get(toCode)));
        }

        initData(values, currencyCode);
    }

    private void initData(ArrayList<Entry> values, String currencyCode) {
        LineDataSet lineDataSet1 = new LineDataSet(values, "AUD/"+currencyCode);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData data = new LineData(dataSets);

        mChart.setData(data);
        mChart.invalidate();
        mChart.getData().setDrawValues(false);
        lineDataSet1.setDrawCircles(false);
    }

    private long dateToEpoch(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateObj = format.parse(date);
            return dateObj.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    private class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // Convert float value to date string
            // Convert from seconds back to milliseconds to format time  to show to the user
            long emissionsMilliSince1970Time = ((long) (value));

            // Show time in local version
            Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
            DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

            return dateTimeFormat.format(timeMilliseconds);
        }
    }
}
