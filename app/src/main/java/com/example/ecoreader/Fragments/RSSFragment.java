package com.example.ecoreader.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoreader.Adapters.NewsAdapter;
import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ecoreader.Application.GetDataService.ECO_LIST;
import static com.example.ecoreader.Application.GetDataService.ECO_UPDATES;

public class RSSFragment extends Fragment {
    private RecyclerView recView;
    private NewsAdapter newsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        recView = view.findViewById(R.id.recView);

        newsAdapter = new NewsAdapter(getContext());
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.setAdapter(newsAdapter);

        loadNews();

        return view;
    }

    private void loadNews() {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<ArrayList<NewsObject>>() {
        }.getType();
        ArrayList<NewsObject> newsList = gson.fromJson(getContext().getSharedPreferences(ECO_UPDATES, MODE_PRIVATE).getString(ECO_LIST, gson.toJson(new ArrayList<NewsObject>())), typeToken);
        newsAdapter.setNewsArrayList(newsList);
    }
}
