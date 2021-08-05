package com.example.ecoreader;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NewsObject.class}, version = 1)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDao newsDao();
    private static NewsDatabase instance;

    public static synchronized NewsDatabase getInstance(Context context) {
        if (null == instance) {
            instance = Room.databaseBuilder(context, NewsDatabase.class, "news_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
