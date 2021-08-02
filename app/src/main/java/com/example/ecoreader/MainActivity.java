package com.example.ecoreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Xml;
import android.view.Window;
import android.view.WindowManager;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new GetNews(this).execute();
    }



    private static class GetNews extends AsyncTask<Void, Void, Void> {
        private final WeakReference<MainActivity> activityReference;
        private ArrayList<NewsObject> arrayList = new ArrayList<>();

        public GetNews(MainActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

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
            RecyclerView recView = activityReference.get().findViewById(R.id.recView);
            NewsAdapter newsAdapter = new NewsAdapter(activityReference.get());
            recView.setLayoutManager(new LinearLayoutManager(activityReference.get()));
            recView.setAdapter(newsAdapter);
            newsAdapter.setNewsArrayList(arrayList);
        }
    }
}