package com.example.ecoreader.DataRetrieval;

public class NewsObject {
    private final String title;
    private final String link;
    private final String desc;
    private final String author;
    private final String pubDate;

    public NewsObject(String title, String link, String desc, String author, String pubDate) {
        this.title = title;
        this.link = link;
        this.desc = desc;
        this.author = author;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDesc() {
        return desc;
    }

    public String getAuthor() {
        return author;
    }

    public String getPubDate() {
        return pubDate;
    }
}
