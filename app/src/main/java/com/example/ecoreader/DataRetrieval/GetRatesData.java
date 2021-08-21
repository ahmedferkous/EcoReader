package com.example.ecoreader.DataRetrieval;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ecoreader.Adapters.LabourCallbackAdapter;
import com.example.ecoreader.Application.GetDataService;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedNewsRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRatesRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.RateEndpoint;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.StatisticsObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;
import com.example.ecoreader.Fragments.ChartFragment;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetRatesData extends AsyncTask<String, Void, Void> {
    private static final String TAG = "GetRatesData";
    public static final String BASE_URL = "https://api.frankfurter.app/";
    private FinishedRatesRequest onComplete;
    private RateEndpoint endpoint;

    public GetRatesData(GetDataService service) {
        onComplete = service;
        initEndpoint();
    }

    public GetRatesData(ChartFragment fragment) {
        onComplete = fragment;
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

        endpoint = retrofit.create(RateEndpoint.class);
    }

    @Override
    protected Void doInBackground(String... strings) {
        switch (strings.length) {
            case 3:
                Call<TimeSeriesObject> timeSeriesCall = endpoint.getTimeSeries(strings[0], strings[1], strings[2]);
                timeSeriesCall.enqueue(new Callback<TimeSeriesObject>() {
                    @Override
                    public void onResponse(@NotNull Call<TimeSeriesObject> call, @NotNull Response<TimeSeriesObject> response) {
                        Log.d(TAG, "onResponse: code: " + response.code());
                        if (response.isSuccessful()) {
                            TimeSeriesObject receivedObject = response.body();
                            onComplete.onReceivedTimeSeries(receivedObject, strings[2]);
                        } else {
                            onComplete.onReceivedTimeSeries(null, "");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<TimeSeriesObject> call, @NotNull Throwable t) {
                        Call<TimeSeriesObject> retryCall = call.clone();
                        retryCall.enqueue(this);
                    }
                });
                break;
            case 1:
                Call<LatestRatesObject> ratesCall = endpoint.getRates(strings[0]);
                ratesCall.enqueue(new Callback<LatestRatesObject>() {
                    @Override
                    public void onResponse(@NotNull Call<LatestRatesObject> call, @NotNull Response<LatestRatesObject> response) {
                        Log.d(TAG, "onResponse: code: " + response.code());
                        if (response.isSuccessful()) {
                            LatestRatesObject receivedObject = response.body();
                            onComplete.onReceivedRates(receivedObject);
                        } else {
                            onComplete.onReceivedRates(null);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<LatestRatesObject> call, @NotNull Throwable t) {
                        Call<LatestRatesObject> retryCall = call.clone();
                        retryCall.enqueue(this);
                    }
                });
                break;
            default:
                Call<HashMap<String, String>> call = endpoint.getAvailableCurrencies();
                call.enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(@NotNull Call<HashMap<String, String>> call, @NotNull Response<HashMap<String, String>> response) {
                        Log.d(TAG, "onResponse: code: " + response.code());
                        if (response.isSuccessful()) {
                            HashMap<String, String> receivedMap = response.body();
                            onComplete.availableCurrencies(receivedMap);
                        } else {
                            onComplete.availableCurrencies(null);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<HashMap<String, String>> call, @NotNull Throwable t) {
                        Call<HashMap<String, String>> retryCall = call.clone();
                        retryCall.enqueue(this);
                    }
                });
                break;

        }
        return null;
    }
}
