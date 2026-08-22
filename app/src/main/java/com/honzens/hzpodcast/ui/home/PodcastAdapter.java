package com.honzens.hzpodcast.ui.home;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.classes.Podcast;
import com.honzens.hzpodcast.common.FeedCache;

import java.util.ArrayList;
import java.util.List;

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.VH> {
    private final List<Podcast> data = new ArrayList<>();
    private Handler m_handler;
    private Context m_context;
    public PodcastAdapter(Context context, Handler handler) {
        this.m_context = context;
        this.m_handler = handler;
    }

    public void setData(List<Podcast> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_podcast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Podcast p = data.get(position);
        h.title.setText(p.collectionName);
        h.author.setText(p.artistName);
        h.feed.setText(p.feedUrl);
        if (!p.isFavorite()) {
            h.favoriteButton.setText("加入最愛");
            h.favoriteButton.setOnClickListener(v -> {
                // 加入最愛
                FeedCache.addFavor(p.feedUrl, p.collectionName, p.artistName);
                p.setFavorite(!p.isFavorite());
                h.favoriteButton.setVisibility(View.GONE);
            });
        }
        else {
            h.favoriteButton.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, author, feed;
        MaterialButton favoriteButton;
        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.title);
            author = v.findViewById(R.id.author);
            feed = v.findViewById(R.id.feed);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}
