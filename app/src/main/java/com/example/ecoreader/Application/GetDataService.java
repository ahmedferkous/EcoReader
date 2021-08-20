package com.example.ecoreader.Application;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecoreader.DataRetrieval.GetLabourStatsData;
import com.example.ecoreader.DataRetrieval.GetRatesData;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedLabourRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedNewsRequest;
import com.example.ecoreader.DataRetrieval.GetNewsData;
import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRatesRequest;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;
import com.example.ecoreader.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.ecoreader.Application.App.CHANNEL_ID_1;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.CIVILIAN_POPULATION;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.EMPLOYED_FULL_TIME;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.EMPLOYMENT_TO_POPULATION_RATIO;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.UNEMPLOYED_PERSONS;

// TODO: 20/08/2021 Perform final checks with retrieving data (for example, after first time launch ensure no more unnecessary fetching of data
public class GetDataService extends Service implements FinishedNewsRequest, FinishedRatesRequest, FinishedLabourRequest {
    @Override
    public void onReceivedPopulation(int population) {
        SharedPreferences.Editor editor = getEditor(this);
        editor.putInt(CIVILIAN_POPULATION, population);
        editor.apply();
        sendPendingIntent();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadRatio.execute(EMPLOYMENT_TO_POPULATION_RATIO);
            }
        }.start();
    }

    @Override
    public void onReceivedEmploymentToPopulationRatio(float ratio) {
        SharedPreferences.Editor editor = getEditor(this);
        editor.putFloat(GetLabourStatsData.EMPLOYMENT_TO_POPULATION_RATIO, ratio/100);
        editor.apply();
        sendPendingIntent();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadUnemployed.execute(UNEMPLOYED_PERSONS);
            }
        }.start();
    }

    @Override
    public void onReceivedUnemployedPersons(int persons) {
        SharedPreferences.Editor editor = getEditor(this);
        editor.putInt(GetLabourStatsData.UNEMPLOYED_PERSONS, persons);
        editor.apply();
        sendPendingIntent();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadEmployed.execute(EMPLOYED_FULL_TIME);
            }
        }.start();
    }

    @Override
    public void onReceivedEmployedFullTime(int persons) {
        SharedPreferences.Editor editor = getEditor(this);
        editor.putInt(GetLabourStatsData.EMPLOYED_FULL_TIME, persons);
        editor.apply();
        sendPendingIntent();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadUnemployedFullTimeWork.execute(UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK);
            }
        }.start();
    }

    @Override
    public void onReceivedUnemployedLookingForFullTimeWork(int persons) {
        SharedPreferences.Editor editor = getEditor(this);
        editor.putInt(GetLabourStatsData.UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK, persons);
        editor.apply();
        sendPendingIntent();
    }

    @Override
    public void onRetrievedNews(ArrayList<NewsObject> arrayList) { //Only here
        writeNews(arrayList);
        sendPendingIntent();
    }

    @Override
    public void onReceivedRates(LatestRatesObject latestRatesObject) { //this
        HashMap<String, Float> rates = latestRatesObject.getRates();
        SharedPreferences.Editor editor = getEditor(this);
        editor.putString(SAVED_RATES, gson.toJson(rates));
        editor.apply();
        sendPendingIntent();
    }

    @Override
    public void onReceivedTimeSeries(TimeSeriesObject timeSeriesObject, String currencyCode) {

    }

    @Override
    public void availableCurrencies(HashMap<String, String> currenciesMap) { //this
        Log.d(TAG, "availableCurrencies: rate received " + currenciesMap.size());
        SharedPreferences.Editor editor = getEditor(this);
        editor.putString(SAVED_AVAILABLE_CURRENCIES, gson.toJson(currenciesMap));
        editor.apply();
        sendPendingIntent();
    }

    private static final String TAG = "GetDataService";
    public static final String ECO_UPDATES = "news_updates";
    public static final String ECO_LIST = "eco_list";
    public static final String SAVED_AVAILABLE_CURRENCIES = "available_currencies";
    public static final String SAVED_RATES = "rates";
    public static final String SAVED_TIME_SERIES = "time_series";
    public static final String AUD_CODE = "AUD";
    public static final String USD_CODE = "USD";

    private PendingIntent data;
    private GetNewsData downloadNewsTask;
    private GetRatesData downloadCurrenciesTask, downloadRatesTask;
    private GetLabourStatsData downloadPopulation, downloadRatio, downloadUnemployed, downloadEmployed, downloadUnemployedFullTimeWork;
    private Gson gson = new Gson();
    private final Handler handler = new Handler();
    private final Runnable newsPeriodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(newsPeriodicUpdate, 7200000); // 7200000 2 hours later
            downloadNewsTask = new GetNewsData(GetDataService.this);
            downloadNewsTask.execute();
        }
    };
    private final Runnable ratesPeriodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(ratesPeriodicUpdate, 86000000); // 86000000  1 day later
            downloadCurrenciesTask = new GetRatesData(GetDataService.this);
            downloadRatesTask = new GetRatesData(GetDataService.this);

            downloadCurrenciesTask.execute();
            downloadRatesTask.execute(AUD_CODE);
        }
    };
    private final Runnable labourStatsPeriodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(labourStatsPeriodicUpdate, 86000000); //placeholder
            downloadPopulation = new GetLabourStatsData(GetDataService.this);
            downloadRatio = new GetLabourStatsData(GetDataService.this);
            downloadUnemployed = new GetLabourStatsData(GetDataService.this);
            downloadEmployed = new GetLabourStatsData(GetDataService.this);
            downloadUnemployedFullTimeWork = new GetLabourStatsData(GetDataService.this);

            downloadPopulation.execute(CIVILIAN_POPULATION);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        startOwnForeground();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            data = intent.getParcelableExtra("pendingIntent");
        }

        handler.postDelayed(newsPeriodicUpdate, 1000);
        handler.postDelayed(ratesPeriodicUpdate, 1000);
        handler.postDelayed(labourStatsPeriodicUpdate, 1000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadNewsTask != null) {
            if (!downloadNewsTask.isCancelled()) {
                downloadNewsTask.cancel(true);
            }
        }
        if (downloadCurrenciesTask != null) {
            if (!downloadCurrenciesTask.isCancelled()) {
                downloadCurrenciesTask.cancel(true);
            }
        }
        if (downloadRatesTask != null) {
            if (!downloadRatesTask.isCancelled()) {
                downloadRatesTask.cancel(true);
            }
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    private void sendPendingIntent() {
        try {
            if (data != null) {
                data.send(this, LoadingActivity.COMPLETE_DATA, null);
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void startOwnForeground() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManagerCompat.IMPORTANCE_MIN);
        startForeground(1, builder.build());

    }

    public static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ECO_UPDATES, MODE_PRIVATE);
        return sharedPreferences.edit();
    }

    private void writeNews(ArrayList<NewsObject> newsList) {
        if (newsList.size() > 0) {
            SharedPreferences.Editor edit = getEditor(this);
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
