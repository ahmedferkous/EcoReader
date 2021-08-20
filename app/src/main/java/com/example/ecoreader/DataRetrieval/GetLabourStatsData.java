package com.example.ecoreader.DataRetrieval;

/*

CIVILIAN_POPULATION

EMPLOYMENT_TO_POPULATION_RATIO

UNEMPLOYED_PERSONS

EMPLOYED_FULL_TIME

UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK

LABOUR_FORCE_FULL_TIME


EMPLOYED_PERSONS = CIVILIAN_POPULATION * EMPLOYMENT_TO_POPULATION_RATIO

EMPLOYMENT_TO_POPULATION_RATIO = EMPLOYED_PERSONS / CIVILIAN_POPULATION

LABOUR_FORCE = UNEMPLOYED_PERSONS + EMPLOYED_PERSONS

NOT_IN_THE_LABOUR_FORCE = CIVILIAN_POPULATION - LABOUR_FORCE

UNEMPLOYMENT_RATE = UNEMPLOYED_PERSONS / LABOUR_FORCE

PARTICIPATION_RATE = LABOUR_FORCE / CIVILIAN_POPULATION

EMPLOYED_PART_TIME = EMPLOYED_PERSONS - EMPLOYED_FULL_TIME

UNEMPLOYED_LOOKING_FOR_PART_TIME_WORK = UNEMPLOYED_PERSONS - UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK

LABOUR_FORCE_PART_TIME = LABOUR_FORCE - LABOUR_FORCE_FULL_TIME

UNEMPLOYMENT_RATE_LOOKING_FOR_PART_TIME_WORK = UNEMPLOYED_LOOKING_FOR_PART_TIME_WORK / LABOUR_FORCE_PART_TIME

UNEMPLOYMENT_RATE_LOOKING_FOR_FULL_TIME_WORK = UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK / LABOUR_FORCE_FULL_TIME

 */

import android.os.AsyncTask;

import com.example.ecoreader.Application.GetDataService;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedLabourRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRatesRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.LabourStatisticsEndpoint;
import com.example.ecoreader.DataRetrieval.Interfaces.RateEndpoint;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.StatisticsObject;
import com.example.ecoreader.Fragments.ChartFragment;
import com.example.ecoreader.Fragments.LabourFragment;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetLabourStatsData extends AsyncTask<String, Void, Void> {
    public static final String BASE_URL = "https://wovg-community.gateway.prod.api.vic.gov.au/abs/v1.0/";
    public static final String CIVILIAN_POPULATION = "CIVILIAN_POPULATION";
    public static final String EMPLOYMENT_TO_POPULATION_RATIO = "EMPLOYMENT_TO_POPULATION_RATIO";
    public static final String UNEMPLOYED_PERSONS = "UNEMPLOYED_PERSONS";
    public static final String EMPLOYED_FULL_TIME = "EMPLOYED_FULL_TIME";
    public static final String UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK = "UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK";
    public static final String REGION = "AUSTRALIA";
    public static final String SEX = "PERSONS";
    public static final String AGE = "15_AND_OVER";
    public static final String ADJUSTMENT_TYPE = "ORIGINAL";

    private FinishedLabourRequest onComplete;
    private LabourStatisticsEndpoint endpoint;

    public GetLabourStatsData(GetDataService service) {
        onComplete = service;
        initEndpoint();
    }

    private void initEndpoint() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        endpoint = retrofit.create(LabourStatisticsEndpoint.class);
    }

    @Override
    protected Void doInBackground(String... strings) {
        Call<StatisticsObject> call = endpoint.receiveStats(REGION, SEX, strings[0], AGE, ADJUSTMENT_TYPE);
        call.enqueue(new Callback<StatisticsObject>() {
            @Override
            public void onResponse(Call<StatisticsObject> call, Response<StatisticsObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<StatisticsObject.Values> values = response.body().getLabourStatistics();
                    float value = values.get(values.size()-1).getObservationValue();
                    switch (strings[0]) {
                        case CIVILIAN_POPULATION:
                            onComplete.onReceivedPopulation((int) (value*1000));
                            break;
                        case EMPLOYMENT_TO_POPULATION_RATIO:
                            onComplete.onReceivedEmploymentToPopulationRatio(value);
                            break;
                        case UNEMPLOYED_PERSONS:
                            onComplete.onReceivedUnemployedPersons((int) (value*1000));
                            break;
                        case EMPLOYED_FULL_TIME:
                            onComplete.onReceivedEmployedFullTime((int) (value*1000));
                            break;
                        case UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK:
                            onComplete.onReceivedUnemployedLookingForFullTimeWork((int)(value*1000));
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<StatisticsObject> call, Throwable t) {

            }
        });
        return null;
    }
}
