package com.example.ecoreader.Application;

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

import com.example.ecoreader.DataRetrieval.NewsObject;
import com.example.ecoreader.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.ecoreader.Application.GetDataService.ECO_LIST;
import static com.example.ecoreader.Application.GetDataService.ECO_UPDATES;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "MainActivity";
    private TextView txtFetch;
    private NewsAdapter newsAdapter;

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

        txtFetch = findViewById(R.id.txtFetch);
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
        Gson gson = new Gson();
        Type typeToken = new TypeToken<ArrayList<NewsObject>>() {
        }.getType();
        ArrayList<NewsObject> newsList = gson.fromJson(getSharedPreferences(ECO_UPDATES, MODE_PRIVATE).getString(ECO_LIST, gson.toJson(new ArrayList<NewsObject>())), typeToken);
        newsAdapter.setNewsArrayList(newsList);
        txtFetch.setVisibility(View.GONE);
    }

}