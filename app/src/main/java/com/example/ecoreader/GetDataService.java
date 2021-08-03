package com.example.ecoreader;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.ecoreader.App.CHANNEL_ID_1;

// TODO: 4/08/2021 Total new news displayed in foreground notification? (i.e Latest News: etc (2 New))
public class GetDataService extends Service {
    private static final String TAG = "GetDataService";
    public static final String ECO_UPDATES = "news_updates";
    public static final String ECO_LIST = "eco_list";
    private GetNews downloadAsyncTask;
    private final Handler handler = new Handler();
    private final Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 7200000); // 2 hours later
            downloadAsyncTask = new GetNews();
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

    public class GetNews extends AsyncTask<Void, Void, Void> {
        private ArrayList<NewsObject> arrayList = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream = getInputStream();
            if (null != inputStream) {
                try {
                    initXMLPullParser(inputStream);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private InputStream getInputStream() {
            try {
                URL url = new URL("https://tradingeconomics.com/australia/rss");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getContent(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
            String content = "";
            parser.require(XmlPullParser.START_TAG, null, tagName);
            if (parser.next() == XmlPullParser.TEXT) {
                content = Jsoup.parse(parser.getText()).text();
                parser.next();
            }
            return content;
        }

        private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }

            int number = 1;

            while (number != 0) {
                switch (parser.next()) {
                    case XmlPullParser.START_TAG:
                        number++;
                        break;
                    case XmlPullParser.END_TAG:
                        number--;
                        break;
                    default:
                        break;
                }
            }
        }

        private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.next();

            parser.require(XmlPullParser.START_TAG, null, "rss");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                parser.require(XmlPullParser.START_TAG, null, "channel");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }

                    if (parser.getName().equals("item")) {
                        parser.require(XmlPullParser.START_TAG, null, "item");

                        String title = "";
                        String link = "";
                        String desc = "";
                        String author = "";
                        String pubDate = "";

                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }

                            String tagName = parser.getName();
                            switch (tagName) {
                                case "title":
                                    title = getContent(parser, "title");
                                    break;
                                case "link":
                                    link = getContent(parser, "link");
                                    break;
                                case "description":
                                    desc = getContent(parser, "description");
                                    break;
                                case "author":
                                    author = getContent(parser, "author");
                                    break;
                                case "pubDate":
                                    pubDate = getContent(parser, "pubDate").replace("GMT", "");
                                    break;
                                default:
                                    skipTag(parser);
                                    break;
                            }
                        }

                        NewsObject item = new NewsObject(title, link, desc, author, pubDate);
                        arrayList.add(item);
                    } else {
                        skipTag(parser);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            writeToPreferences(arrayList);
        }
    }
}
