package com.example.ecoreader.DataRetrieval.Interfaces;

import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;

import java.util.ArrayList;
import java.util.HashMap;

public interface FinishedRequest {
    void onRetrievedNews(ArrayList<NewsObject> arrayList);

    void onReceivedRates(LatestRatesObject latestRatesObject);

    void onReceivedTimeSeries(TimeSeriesObject timeSeriesObject);

    void onReceivedConversion(float convertedAmount);

    void availableCurrencies(HashMap<String, String> currenciesMap);
}
