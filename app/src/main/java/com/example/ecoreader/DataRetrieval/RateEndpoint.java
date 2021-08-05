package com.example.ecoreader.DataRetrieval;

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
    Call<LatestRatesObject> getConversion(@Query("amount") int amount, @Query("from") String from, @Query("to") String to);

    @GET("/currencies")
    Call<String[]> getAvailableCurrencies();
}
