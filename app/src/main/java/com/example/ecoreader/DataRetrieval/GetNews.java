package com.example.ecoreader.DataRetrieval;

import android.os.AsyncTask;
import android.util.Xml;

import com.example.ecoreader.Application.GetDataService;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetNews extends AsyncTask<Void, Void, Void> {
    public interface OnCompletedRequest {
        void onCompetedData(ArrayList<NewsObject> arrayList);
    }
    private ArrayList<NewsObject> arrayList;
    private final OnCompletedRequest onComplete;

    public GetNews(GetDataService service) {
        arrayList = new ArrayList<>();
        onComplete = service;
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
        onComplete.onCompetedData(arrayList);
    }
}