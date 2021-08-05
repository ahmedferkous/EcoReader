package com.example.ecoreader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.ecoreader.GetDataService.ECO_LIST;
import static com.example.ecoreader.GetDataService.ECO_UPDATES;

public class MainActivity extends AppCompatActivity {
    private NewsAdapter newsAdapter;
    private NewsDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetDataService mService = new GetDataService();
        Intent mServiceIntent = new Intent(this, mService.getClass());
        if (!isMyServiceRunning(mService.getClass())) {
            startService(mServiceIntent);
        }

        db = NewsDatabase.getInstance(this);
        RecyclerView recView = findViewById(R.id.recView);
        newsAdapter = new NewsAdapter(this);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(newsAdapter);

        loadNews();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
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
        ArrayList<NewsObject> newsList = (ArrayList<NewsObject>) db.newsDao().getAllNews();
        newsAdapter.setNewsArrayList(newsList);
    }

}