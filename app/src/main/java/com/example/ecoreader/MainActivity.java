package com.example.ecoreader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
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

import static com.example.ecoreader.GetDataJobService.ECO_LIST;
import static com.example.ecoreader.GetDataJobService.ECO_UPDATES;

// TODO: 3/08/2021 Service not running?? 
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String FIRST_JOB_RUN = "job_scheduled";
    public static final int DOWNLOAD_JOB_ID = 20005;
    private TextView txtFetch;
    private RecyclerView recView;
    private NewsAdapter newsAdapter;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleService();

        txtFetch = findViewById(R.id.txtFetch);
        recView = findViewById(R.id.recView);
        newsAdapter = new NewsAdapter(this);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(newsAdapter);

        loadNews();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadNews() {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<ArrayList<NewsObject>>(){}.getType();
        ArrayList<NewsObject> newsList = gson.fromJson(getSharedPreferences(ECO_UPDATES, MODE_PRIVATE).getString(ECO_LIST, gson.toJson(new ArrayList<NewsObject>())), typeToken);
        newsAdapter.setNewsArrayList(newsList);
        txtFetch.setVisibility(View.GONE);
    }

    private void scheduleService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean(FIRST_JOB_RUN, true)) {
                Log.d(TAG, "scheduleService: SCHEDULED!!! XD");
                ComponentName componentName = new ComponentName(this, GetDataJobService.class);
                JobInfo.Builder builder = new JobInfo.Builder(DOWNLOAD_JOB_ID, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true);
                builder.setPeriodic(3600000); // one hour - 3600000
                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.schedule(builder.build());
                preferences.edit().putBoolean(FIRST_JOB_RUN, false).apply();
            } else {

            }
        }

    }




}