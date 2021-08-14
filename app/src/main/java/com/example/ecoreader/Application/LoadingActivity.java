package com.example.ecoreader.Application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.example.ecoreader.R;

public class LoadingActivity extends AppCompatActivity{
    private BootstrapProgressBar progressBar;
    private Animation rotateClk;
    private TextView txtFetch;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        initViews();
        setupService();

    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartReceiver.class);
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_horizontal);
        txtFetch = findViewById(R.id.txtFetch);
        imageView = findViewById(R.id.imageView);
        rotateClk = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        imageView.setAnimation(rotateClk);
    }

    private void setupService() {
        GetDataService mService = new GetDataService();
        Intent mServiceIntent = new Intent(this, mService.getClass());
        if (!isMyServiceRunning(mService.getClass())) {
            startService(mServiceIntent);
        }

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

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(LoadingActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(mainIntent);
    }

}
