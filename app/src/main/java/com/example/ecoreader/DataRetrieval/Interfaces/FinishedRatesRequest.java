package com.example.ecoreader.DataRetrieval.Interfaces;

import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;

import java.util.HashMap;

public interface FinishedRatesRequest {
    void onReceivedRates(LatestRatesObject latestRatesObject);

    void onReceivedTimeSeries(TimeSeriesObject timeSeriesObject);

    void availableCurrencies(HashMap<String, String> currenciesMap);
}
