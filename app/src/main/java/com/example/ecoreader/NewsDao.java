package com.example.ecoreader;

import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface NewsDao {
    @Insert
    void insertNews(NewsObject news);

    @Query("SELECT * FROM news_table")
    List<NewsObject> getAllNews();

    // TODO: 5/08/2021 Search specific news by title, author, date etc
}
