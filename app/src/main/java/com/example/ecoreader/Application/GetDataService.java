package com.example.ecoreader.Application;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRequest;
import com.example.ecoreader.DataRetrieval.GetNewsData;
import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;
import com.example.ecoreader.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.ecoreader.Application.App.CHANNEL_ID_1;

public class GetDataService extends Service implements FinishedRequest {
    @Override
    public void onRetrievedNews(ArrayList<NewsObject> arrayList) { //Only here
        writeNews(arrayList);
    }

    @Override
    public void onReceivedRates(LatestRatesObject latestRatesObject) { //Only here
        HashMap<String, Float> rates = latestRatesObject.getRates();
        SharedPreferences.Editor editor = getEditor();
        editor.putString(RATES, gson.toJson(rates));
        editor.apply();
    }

    @Override
    public void onReceivedTimeSeries(TimeSeriesObject timeSeriesObject) {  // only ran in RateFragment
       /*
       HashMap<String, HashMap<String, Double>> timeSeries = timeSeriesObject.getRates();
       SharedPreferences.Editor editor = getEditor();
       editor.putString(TIME_SERIES, gson.toJson(timeSeries));
       editor.apply();
        */

    }

    @Override
    public void onReceivedConversion(float convertedAmount) { //both
        SharedPreferences.Editor editor = getEditor();
        editor.putFloat(CONVERT_AMOUNT, convertedAmount);
        editor.apply();
    }

    @Override
    public void availableCurrencies(HashMap<String, String> currenciesMap) { //Only here
        SharedPreferences.Editor editor = getEditor();
        editor.putString(AVAILABLE_CURRENCIES, gson.toJson(currenciesMap));
        editor.apply();
    }

    private static final String TAG = "GetDataService";
    public static final String ECO_UPDATES = "news_updates";
    public static final String RATES = "rates";
    public static final String TIME_SERIES = "time_series";
    public static final String CONVERT_AMOUNT = "convert_amount";
    public static final String AVAILABLE_CURRENCIES = "available_currencies";
    public static final String ECO_LIST = "eco_list";

    private GetNewsData downloadAsyncTask;
    private Gson gson = new Gson();
    private final Handler handler = new Handler();
    private final Runnable newsPeriodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(newsPeriodicUpdate, 7200000); // 2 hours later
            downloadAsyncTask = new GetNewsData(GetDataService.this);
            downloadAsyncTask.execute();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        startOwnForeground();
    }

    private void startOwnForeground() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManagerCompat.IMPORTANCE_MIN);
        startForeground(1, builder.build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(newsPeriodicUpdate, 10000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadAsyncTask != null) {
            if (!downloadAsyncTask.isCancelled()) {
                downloadAsyncTask.cancel(true);
            }
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    private SharedPreferences.Editor getEditor() {
        SharedPreferences sharedPreferences = getSharedPreferences(ECO_UPDATES, MODE_PRIVATE);
        return sharedPreferences.edit();
    }

    private void writeNews(ArrayList<NewsObject> newsList) {
        if (newsList.size() > 0) {
            SharedPreferences.Editor edit = getEditor();
            edit.putString(ECO_LIST, gson.toJson(newsList));
            edit.apply();
            Log.d(TAG, "writeToPreferences: Written!!");
            send(newsList.get(0));
            //sendNotification(newsList.get(newsList.size()-1));
        }
    }

    private void send(NewsObject latestNews) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(latestNews.getLink()));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, webIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Latest News: " + latestNews.getTitle())
                        .bigText(latestNews.getDesc()));
        startForeground(1, builder.build());
    }
}
