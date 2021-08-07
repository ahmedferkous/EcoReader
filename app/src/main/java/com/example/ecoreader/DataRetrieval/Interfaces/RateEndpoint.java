package com.example.ecoreader.DataRetrieval.Interfaces;

import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// base URL: api.frankfurter.app
public interface RateEndpoint {
    @GET("/latest")
    Call<LatestRatesObject> getRates(@Query("from") String currency);

    @GET("/{date}..") //yyyy-dd-mm
    Call<TimeSeriesObject> getTimeSeries(@Path("date") String date, @Query("to") String to);

    @GET("/latest")
    Call<LatestRatesObject> getConversion(@Query("amount") float amount, @Query("from") String from, @Query("to") String to);

    @GET("/currencies")
    Call<HashMap<String, String>> getAvailableCurrencies();
}

