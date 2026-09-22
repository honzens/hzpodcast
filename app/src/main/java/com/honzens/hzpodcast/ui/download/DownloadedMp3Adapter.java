package com.honzens.hzpodcast.ui.download;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.honzens.hzpodcast.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DownloadedMp3Adapter extends RecyclerView.Adapter<DownloadedMp3Adapter.ViewHolder> {
    public interface Listener {
        void onPlay(DownloadedEpisode episode);
    }
    private List<DownloadedEpisode> episodes = new ArrayList<>();
    private final Context m_ctx;
    private final Listener listener;
    public DownloadedMp3Adapter(Context context, Listener listener) {
        this.m_ctx = context;
        this.listener = listener;
    }
    public void setData(List<DownloadedEpisode> episodes) {
        this.episodes = episodes;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_downloaded_mp3,
                                parent,
                                false
                        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadedEpisode episode = episodes.get(position);
        holder.txtName.setText(episode.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        holder.txtDate.setText(sdf.format(new Date(episode.getFile().lastModified())));
        holder.txtSize.setText(formatFileSize(episode.getSize()));
        holder.btnPlay.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            listener.onPlay(episodes.get(adapterPosition));
        });
        holder.btnDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            DownloadedEpisode ep = episodes.get(adapterPosition);
            String sMessage = String.format("確定要刪除 %s 嗎?", ep.getFile().getName());
            new AlertDialog.Builder(m_ctx)
                    .setTitle("確認")
                    .setMessage(sMessage)
                    .setPositiveButton("確定", (dialog, which) -> {
                        // 使用者按確
                        try {
                            if (ep.getFile().delete()) {
                                episodes.remove(ep);
                                notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        // 使用者按取消
                        dialog.dismiss();
                    })
                    .show();
        });
    }
    @Override
    public int getItemCount() {
        return episodes.size();
    }
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format(
                    Locale.getDefault(),
                    "%.1f KB",
                    size / 1024.0
            );
        } else {
            return String.format(
                    Locale.getDefault(),
                    "%.1f MB",
                    size / 1024.0 / 1024.0
            );
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtDate;
        TextView txtSize;
        ImageButton btnPlay;
        ImageButton btnDelete;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSize = itemView.findViewById(R.id.txtSize);
            btnPlay = itemView.findViewById(R.id.btnPlayDownload);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}