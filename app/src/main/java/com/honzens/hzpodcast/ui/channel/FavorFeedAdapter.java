package com.honzens.hzpodcast.ui.channel;


import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.classes.FavorFeedItem;
import com.honzens.hzpodcast.common.FeedCache;

import java.util.ArrayList;
import java.util.List;

public class FavorFeedAdapter extends RecyclerView.Adapter<FavorFeedAdapter.ViewHolder> {
    private final Context m_ctx;
    private final List<FavorFeedItem> list = new ArrayList<>();
    private final Handler m_handler;

    public FavorFeedAdapter(Context context, Handler handler) {
        this.m_ctx = context;
        m_handler = handler;
    }

    public void setData(List<FavorFeedItem> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(m_ctx)
                .inflate(R.layout.item_favor_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavorFeedItem item = list.get(position);
        if (item.artworkUrl != null && !item.artworkUrl.isEmpty())
            Glide.with(m_ctx)
                    .load(item.artworkUrl)
                    .placeholder(R.drawable.ic_podcast)
                    .error(R.drawable.ic_podcast)
                    .into(holder.image);
        holder.txtName.setText(item.collectionName);
        holder.txtArtistName.setText(item.artistName);
        holder.txtFeedUrl.setText(item.feedUrl);
        holder.btnFavor.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            String sMessage = String.format("確定要移除 %s 嗎?", list.get(adapterPosition).collectionName);
            new AlertDialog.Builder(m_ctx)
                    .setTitle("確認")
                    .setMessage(sMessage)
                    .setPositiveButton("確定", (dialog, which) -> {
                        // 使用者按確定
                        FavorFeedItem currentItem = list.get(adapterPosition);
                        list.remove(currentItem);
                        FeedCache.delFavor(currentItem.feedUrl);
                        FeedCache.saveFavorMap(m_ctx);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        // 使用者按取消
                        dialog.dismiss();
                    })
                    .show();
        });
        holder.btnEnterProgram.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            FavorFeedItem currentItem = list.get(adapterPosition);
            Message msg = m_handler.obtainMessage();
            msg.what = 1;
            msg.obj = currentItem.feedUrl;
            m_handler.sendMessage(msg);
         });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView txtName;
        TextView txtArtistName;
        TextView txtFeedUrl;
        ImageButton btnFavor;
        ImageButton btnEnterProgram;
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            txtName = itemView.findViewById(R.id.txtChannelName);
            txtArtistName = itemView.findViewById(R.id.txtArtistName);
            txtFeedUrl = itemView.findViewById(R.id.txtFeedUrl);
            btnFavor = itemView.findViewById(R.id.btn_remove_favor);
            btnEnterProgram = itemView.findViewById(R.id.btn_enter_program);
        }
    }

}
