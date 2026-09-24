package com.honzens.hzpodcast.ui.home;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.classes.Podcast;
import com.honzens.hzpodcast.common.FeedCache;

import java.util.ArrayList;
import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.VH> {
    private final List<String> data = new ArrayList<>();
    private final Handler m_handler;
    private final Context m_ctx;
    public KeywordAdapter(Context context, Handler handler) {
        this.m_ctx = context;
        this.m_handler = handler;
    }
    public void setData(String kw_last) {
        data.clear();
        data.add("歷史");
        data.add("學英文");
        data.add("陶子");
        data.add("下班");
        data.add("懸案");
        data.add("腦洞");
        data.add("天下雜誌");
        data.add("輕音樂");
        data.add("科學");
        if (kw_last != null && !kw_last.isEmpty()) {
            boolean bFind = false;
            for (String kw : data) {
                if (kw.equals(kw_last)) {
                    bFind = true;
                    break;
                }
            }
            if (!bFind) {
                data.add(0, kw_last);
            }
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public KeywordAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeywordAdapter.VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_keyword, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordAdapter.VH holder, int position) {
        String keyword = data.get(position);
        holder.keyword.setText(keyword);
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            Message msg = m_handler.obtainMessage(2, data.get(adapterPosition));
            msg.sendToTarget();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    static class VH extends RecyclerView.ViewHolder {
        TextView keyword;
        VH(@NonNull View v) {
            super(v);
            keyword = v.findViewById(R.id.txt_keyword);
        }
    }
}
