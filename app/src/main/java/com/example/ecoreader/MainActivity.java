package com.example.ecoreader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.ecoreader.GetDataService.ECO_LIST;
import static com.example.ecoreader.GetDataService.ECO_UPDATES;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private GetDataService mService;
    private TextView txtFetch;
    private RecyclerView recView;
    private NewsAdapter newsAdapter;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = new GetDataService();
        Intent mServiceIntent = new Intent(this, mService.getClass());
        if (!isMyServiceRunning(mService.getClass())) {
            startService(mServiceIntent);
        }

        txtFetch = findViewById(R.id.txtFetch);
        recView = findViewById(R.id.recView);
        newsAdapter = new NewsAdapter(this);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(newsAdapter);

        loadNews();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartReceiver.class);
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadNews() {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<ArrayList<NewsObject>>(){}.getType();
        ArrayList<NewsObject> newsList = gson.fromJson(getSharedPreferences(ECO_UPDATES, MODE_PRIVATE).getString(ECO_LIST, gson.toJson(new ArrayList<NewsObject>())), typeToken);
        newsAdapter.setNewsArrayList(newsList);
        txtFetch.setVisibility(View.GONE);
    }

}