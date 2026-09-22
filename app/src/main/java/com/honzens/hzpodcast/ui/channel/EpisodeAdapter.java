package com.honzens.hzpodcast.ui.channel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.classes.Episode;

import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private final Context m_ctx;
    public interface Listener {
        void onPlay(Episode episode);
        void onDownload(Episode episode, ProgressBar progressBar, TextView percentText);
    }
    private final List<Episode> episodes;
    private final Listener listener;
    public EpisodeAdapter(Context ctx, Listener listener) {
        this.m_ctx = ctx;
        this.listener = listener;
        this.episodes = new ArrayList<>();
    }
    public void setData(List<Episode> episodes) {
        this.episodes.clear();
        this.episodes.addAll(episodes);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_episode, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.txtTitle.setText(episode.title);
        holder.txtAudioType.setText(episode.audioType);
        holder.txtDuration.setText(episode.duration);
        holder.txtDate.setText(episode.pubDate);
        holder.btnPlay.setOnClickListener(v ->
                listener.onPlay(episode)
        );
        holder.btnDownload.setOnClickListener(v ->
                listener.onDownload(episode, holder.downloadProgress, holder.downloadPercent)
        );
    }
    @Override
    public int getItemCount() {
        return episodes.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtDate;
        TextView txtAudioType;
        TextView txtDuration;
        ImageButton btnPlay;
        ImageButton btnDownload;
        ProgressBar downloadProgress;

        TextView downloadPercent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAudioType = itemView.findViewById(R.id.txtAudioType);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            downloadProgress = itemView.findViewById(R.id.downloadProgress);
            downloadPercent = itemView.findViewById(R.id.downloadPercent);
        }
    }
}
