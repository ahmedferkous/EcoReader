package com.example.ecoreader.DataRetrieval.Interfaces;

import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;

import java.util.ArrayList;
import java.util.HashMap;

public interface FinishedNewsRequest {
    void onRetrievedNews(ArrayList<NewsObject> arrayList);
}
