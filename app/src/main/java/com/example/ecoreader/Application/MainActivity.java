package com.example.ecoreader.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.ecoreader.Fragments.LabourFragment;
import com.example.ecoreader.Fragments.RSSFragment;
import com.example.ecoreader.Fragments.RateFragment;
import com.example.ecoreader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavView();

        GetDataService mService = new GetDataService();
        Intent mServiceIntent = new Intent(this, mService.getClass());
        if (!isMyServiceRunning(mService.getClass())) {
            startService(mServiceIntent);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new RSSFragment());
        transaction.commit();
    }

    private void initNavView() {
        navigationView = findViewById(R.id.bottomNavView);
        navigationView.setSelectedItemId(R.id.rssFeed);

        navigationView.setItemOnTouchListener(R.id.rssFeed, new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                navigationView.setSelectedItemId(R.id.rssFeed);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, new RSSFragment());
                transaction.commit();
                return true;
            }
        });

        navigationView.setItemOnTouchListener(R.id.exchangeRate, new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                navigationView.setSelectedItemId(R.id.exchangeRate);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, new RateFragment());
                transaction.commit();
                return true;
            }
        });

        navigationView.setItemOnTouchListener(R.id.labourStats, new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                navigationView.setSelectedItemId(R.id.labourStats);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, new LabourFragment());
                transaction.commit();
                return true;
            }
        });
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

}