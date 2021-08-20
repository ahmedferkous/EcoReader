package com.example.ecoreader.Application;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.example.ecoreader.R;

// TODO: 14/08/2021 backstack issues 
public class LoadingActivity extends AppCompatActivity{
    private static final String TAG = "LoadingActivity";
    public static final int COMPLETE_DATA = 1001;
    public static final String IS_FIRST_LAUNCH = "first_launch";
    public static final String FIRST_TIME = "first_time";
    private BootstrapProgressBar progressBar;
    private Animation rotateClk;
    private TextView txtFetch;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        initViews();

        SharedPreferences settings = getSharedPreferences(IS_FIRST_LAUNCH, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        if (settings.contains(FIRST_TIME)) {
            setupService(false);
        } else {
            editor.putBoolean(FIRST_TIME, true);
            editor.apply();
            setupService(true);
        }

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

    private void setupService(boolean firstTime) {
        Intent mServiceIntent = new Intent(getApplicationContext(), GetDataService.class);
        if (firstTime) {
            PendingIntent pendingResult = createPendingResult(100, new Intent(), 0);
            mServiceIntent.putExtra("pendingIntent", pendingResult);
            mServiceIntent.putExtra(FIRST_TIME, true);
        } else {
            mServiceIntent.putExtra(FIRST_TIME, false);
        }
        GetDataService mService = new GetDataService();
        if (!isMyServiceRunning(mService.getClass())) {
            startService(mServiceIntent);
        }
        if (!firstTime) {
            navigateToMainActivity();
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
        txtFetch.setText("Finished Loading!");
        progressBar.setProgress(8);
        imageView.setAnimation(null);
        imageView.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(LoadingActivity.this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(mainIntent);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100 && resultCode == COMPLETE_DATA) {
            incrementProgress();
            if (progressBar.getProgress() == 8) {
                navigateToMainActivity();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void incrementProgress() {
        int amount = progressBar.getProgress();
        if (amount != 8) {
            progressBar.setProgress(amount+1);
        }
    }
}
