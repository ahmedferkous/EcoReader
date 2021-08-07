package com.example.ecoreader.DataRetrieval;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ecoreader.Application.GetDataService;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.RateEndpoint;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetRatesData extends AsyncTask<String, Void, Void> {
    private static final String TAG = "GetRatesData";
    public static final String BASE_URL = "api.frankfurter.app";
    private FinishedRequest onComplete;
    private RateEndpoint endpoint;

    public GetRatesData(GetDataService service) {
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
                .client(client)
                .build();

        endpoint = retrofit.create(RateEndpoint.class);
    }

    @Override
    protected Void doInBackground(String... strings) {
        switch (strings.length) {
            case 3:
                Call<LatestRatesObject> conversionCall = endpoint.getConversion(Float.parseFloat(strings[0]), strings[1], strings[2]);
                conversionCall.enqueue(new Callback<LatestRatesObject>() {
                    @Override
                    public void onResponse(@NotNull Call<LatestRatesObject> call, @NotNull Response<LatestRatesObject> response) {
                        Log.d(TAG, "onResponse: code: " + response.code());
                        if (response.isSuccessful()) {
                            LatestRatesObject receivedObject = response.body();
                            onComplete.onReceivedConversion(receivedObject.getRates().get(strings[2]));
                        } else {
                            onComplete.onReceivedConversion(0);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<LatestRatesObject> call, @NotNull Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;
            case 2:
                Call<TimeSeriesObject> timeSeriesCall = endpoint.getTimeSeries(strings[0], strings[1]);
                timeSeriesCall.enqueue(new Callback<TimeSeriesObject>() {
                    @Override
                    public void onResponse(@NotNull Call<TimeSeriesObject> call, @NotNull Response<TimeSeriesObject> response) {
                        Log.d(TAG, "onResponse: code: " + response.code());
                        if (response.isSuccessful()) {
                            TimeSeriesObject receivedObject = response.body();
                            onComplete.onReceivedTimeSeries(receivedObject);
                        } else {
                            onComplete.onReceivedTimeSeries(null);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<TimeSeriesObject> call, @NotNull Throwable t) {
                        t.printStackTrace();
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
                        t.printStackTrace();
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
                        t.printStackTrace();
                    }
                });
                break;

        }
        return null;
    }
}
