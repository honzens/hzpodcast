package com.honzens.hzpodcast.ui.channel;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.classes.FeedItem;
import com.honzens.hzpodcast.common.FeedCache;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final Context context;
    private final List<FeedItem> list = new ArrayList<>();
    private final Handler m_handler;

    public FeedAdapter(Context context, Handler handler) {
        this.context = context;
        m_handler = handler;
    }

    public void setData(List<FeedItem> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_feed, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedItem item = list.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.txtAuthor.setText(item.getSummary());
        holder.txtDate.setText(item.getPubDate());
        if (item.isRead) {
            holder.txtTitle.setTextColor(context.getColor(R.color.purple_100));
            //holder.txtAuthor.setTextColor(context.getColor(R.color.light_gray));
            //holder.txtDate.setTextColor(context.getColor(R.color.light_gray));
        }
        else {
            holder.txtTitle.setTextColor(context.getColor(R.color.purple_99));
        }
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            FeedItem currentItem = list.get(adapterPosition);
            currentItem.isRead = true;
            //FeedCache.addRead(currentItem.getUrl());
            Message msg = m_handler.obtainMessage();
            msg.what = 1;
            msg.arg1 = adapterPosition;
            m_handler.sendMessage(msg);

            msg = m_handler.obtainMessage();
            msg.what = 2;
            msg.obj = currentItem;
            m_handler.sendMessage(msg);
         });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtAuthor;
        TextView txtDate;

        ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

}
