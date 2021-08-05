package com.example.ecoreader.Application;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecoreader.DataRetrieval.GetNews;
import com.example.ecoreader.DataRetrieval.NewsObject;
import com.example.ecoreader.R;
import com.example.ecoreader.Application.RestartReceiver;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.ecoreader.Application.App.CHANNEL_ID_1;

public class GetDataService extends Service implements GetNews.OnCompletedRequest {
    @Override
    public void onCompetedData(ArrayList<NewsObject> arrayList) {
        writeToPreferences(arrayList);
    }

    private static final String TAG = "GetDataService";
    public static final String ECO_UPDATES = "news_updates";
    public static final String ECO_LIST = "eco_list";
    private GetNews downloadAsyncTask;
    private final Handler handler = new Handler();
    private final Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 7200000); // 2 hours later
            downloadAsyncTask = new GetNews(GetDataService.this);
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
        handler.postDelayed(periodicUpdate, 10000);
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

    private void writeToPreferences(ArrayList<NewsObject> newsList) {
        if (newsList.size() > 0) {
            SharedPreferences sharedPreferences = getSharedPreferences(ECO_UPDATES, MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(ECO_LIST, new Gson().toJson(newsList));
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
