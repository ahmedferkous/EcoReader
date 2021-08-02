package com.example.ecoreader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetDataJobService extends JobService {
    private static final String TAG = "GetDataJobService";
    public static final String ECO_UPDATES = "news_updates";
    public static final String ECO_LIST = "eco_list";
    private GetNews downloadAsyncTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Created");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        downloadAsyncTask = new GetNews();
        downloadAsyncTask.execute();
        Log.d(TAG, "onStartJob: Successfully started!");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (null != downloadAsyncTask) {
            if (!downloadAsyncTask.isCancelled()) {
                downloadAsyncTask.cancel(true);
            }
        }
        return true; // reschedule job
    }

    private void sendNotification(NewsObject latestNews) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_1)
                .setContentText("New Economic News Available")
                .setContentText("Latest News:\n" + latestNews.getTitle() + "\n" + latestNews.getDesc() + "\n" + latestNews.getPubDate())
                .setContentIntent(pIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    private void writeToPreferences(ArrayList<NewsObject> newsList) {
        if (newsList.size() > 0) {
            SharedPreferences sharedPreferences = getSharedPreferences(ECO_UPDATES, MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(ECO_LIST, new Gson().toJson(newsList));
            edit.apply();
            Log.d(TAG, "writeToPreferences: Written!!");
            sendNotification(newsList.get(newsList.size()-1));
        }
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
