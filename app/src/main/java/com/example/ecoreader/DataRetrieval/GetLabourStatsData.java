package com.example.ecoreader.DataRetrieval;

import android.os.AsyncTask;

import com.example.ecoreader.Adapters.LabourCallbackAdapter;
import com.example.ecoreader.Application.GetDataService;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedLabourRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.LabourStatisticsEndpoint;
import com.example.ecoreader.DataRetrieval.Interfaces.onCompletedRetrieval;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.StatisticsObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetLabourStatsData extends AsyncTask<String, Void, Void> implements onCompletedRetrieval {
    @Override
    public void onCompletedResultType(String type) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                switch (type) {
                    case CIVILIAN_POPULATION:
                        employmentRatioCall.enqueue(new LabourCallbackAdapter(EMPLOYMENT_TO_POPULATION_RATIO, serviceCallback, GetLabourStatsData.this));
                        break;
                    case EMPLOYMENT_TO_POPULATION_RATIO:
                        unemployedCall.enqueue(new LabourCallbackAdapter(UNEMPLOYED_PERSONS, serviceCallback, GetLabourStatsData.this));
                        break;
                    case UNEMPLOYED_PERSONS:
                        employedCall.enqueue(new LabourCallbackAdapter(EMPLOYED_FULL_TIME, serviceCallback, GetLabourStatsData.this));
                        break;
                    case EMPLOYED_FULL_TIME:
                        unemployedFullTimeCall.enqueue(new LabourCallbackAdapter(UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK, serviceCallback, GetLabourStatsData.this));
                        break;
                    default:
                        break;
                }
            }
        }.start();
    }

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

    private final FinishedLabourRequest serviceCallback;
    private LabourStatisticsEndpoint endpoint;
    private Call<StatisticsObject> employmentRatioCall;
    private Call<StatisticsObject> unemployedCall;
    private Call<StatisticsObject> unemployedFullTimeCall;
    private Call<StatisticsObject> employedCall;

    public GetLabourStatsData(GetDataService service) {
        serviceCallback = service;
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
        Call<StatisticsObject> populationCall = endpoint.receiveStats(REGION, SEX, CIVILIAN_POPULATION, AGE, ADJUSTMENT_TYPE);
        employmentRatioCall = endpoint.receiveStats(REGION, SEX, EMPLOYMENT_TO_POPULATION_RATIO, AGE, ADJUSTMENT_TYPE);
        unemployedCall = endpoint.receiveStats(REGION, SEX, UNEMPLOYED_PERSONS, AGE, ADJUSTMENT_TYPE);
        employedCall = endpoint.receiveStats(REGION, SEX, EMPLOYED_FULL_TIME, AGE, ADJUSTMENT_TYPE);
        unemployedFullTimeCall = endpoint.receiveStats(REGION, SEX, UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK, AGE, ADJUSTMENT_TYPE);

        populationCall.enqueue(new LabourCallbackAdapter(CIVILIAN_POPULATION, serviceCallback, this));
        return null;
    }
}
