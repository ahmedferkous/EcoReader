package com.example.ecoreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private ArrayList<NewsObject> newsArrayList = new ArrayList<>();
    private final Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    public void setNewsArrayList(ArrayList<NewsObject> newsArrayList) {
        this.newsArrayList = newsArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsObject boundNews = newsArrayList.get(position);

        holder.txtTitle.setText(boundNews.getTitle());
        holder.txtDesc.setText(boundNews.getDesc());
        if (boundNews.getAuthor() == null || boundNews.getAuthor().length() == 0) {
            holder.txtAuthor.setVisibility(View.GONE);
        } else {
            holder.txtAuthor.setText(boundNews.getAuthor());
            holder.txtAuthor.setVisibility(View.VISIBLE);
        }
        holder.txtPubDate.setText(boundNews.getPubDate());

        holder.parent.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setMessage("Goto " + boundNews.getLink() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW);
                        webIntent.setData(Uri.parse(boundNews.getLink()));
                        context.startActivity(webIntent);
                    })
                    .setNegativeButton("No", null);
            builder.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitle, txtDesc, txtAuthor, txtPubDate;
        private final MaterialCardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtPubDate = itemView.findViewById(R.id.txtPubDate);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
