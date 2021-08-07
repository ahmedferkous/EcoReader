package com.example.ecoreader.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecoreader.Adapters.NewsAdapter;
import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRequest;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;
import com.example.ecoreader.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RateFragment extends Fragment implements FinishedRequest {
    @Override
    public void onRetrievedNews(ArrayList<NewsObject> arrayList) { //dont worry

    }

    @Override
    public void onReceivedRates(LatestRatesObject latestRatesObject) { //dont worry

    }

    @Override
    public void onReceivedTimeSeries(TimeSeriesObject timeSeriesObject) { //Only here

    }

    @Override
    public void onReceivedConversion(float convertedAmount) { //both

    }

    @Override
    public void availableCurrencies(HashMap<String, String> currenciesMap) { //dont worry

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        return view;
    }


}
