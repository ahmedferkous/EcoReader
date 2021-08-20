package com.example.ecoreader.DataRetrieval.Interfaces;

import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.StatisticsObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LabourStatisticsEndpoint {
    @Headers({"apikey: a5346b2e-8b4f-450c-aa21-1b00e4ce3f95"})
    @GET("labour-force-statistics")
    Call<StatisticsObject> receiveStats(@Query("region") String region, @Query("sex") String sex,
                                        @Query("data_item") String data_item, @Query("age") String age,
                                        @Query("adjustment_type") String adjustment_type);

    // TODO: 20/08/2021 Investigate if needed 
    @Headers({"apikey: a5346b2e-8b4f-450c-aa21-1b00e4ce3f95"})
    @GET("labour-force-statistics")
    Call<StatisticsObject> receiveStatsOverPeriod(@Query("region") String region, @Query("sex") String sex,
                                                  @Query("data_item") String data_item, @Query("age") String age,
                                                  @Query("adjustment_type") String adjustment_type,
                                                  @Query("start_period") String start_period,
                                                  @Query("end_period") String end_period);
}
