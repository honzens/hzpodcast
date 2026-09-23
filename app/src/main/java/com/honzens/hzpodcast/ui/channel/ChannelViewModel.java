package com.honzens.hzpodcast.ui.channel;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.honzens.hzpodcast.classes.Episode;
import com.honzens.hzpodcast.classes.Programs;
import com.honzens.hzpodcast.common.AtomParser;
import com.honzens.hzpodcast.common.FeedCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChannelViewModel extends ViewModel {
    private final MutableLiveData<List<Episode>> m_feeds = new MutableLiveData<>();
    public LiveData<List<Episode>> getFeeds()
    {
        return m_feeds;
    }
    public String channel_name = "";
    public void loadPrograms(String url, Context ctx)
    {
        Programs progs = FeedCache.load_ep_cache(ctx, url);
        if (progs!=null && progs.episodes!=null && !progs.episodes.isEmpty()) {
            m_feeds.postValue(progs.episodes);
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                List<Episode> news_items = new ArrayList<>();
                channel_name = "";
                m_feeds.postValue(news_items);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    Programs item = AtomParser.parsePodcast(response.body().byteStream());
                    channel_name = item.title;
                    m_feeds.postValue(item.episodes);
                    //save to cache
                    FeedCache.save_ep_cache(ctx, url, item);
                    //=============
                } catch (Exception e) {
                    List<Episode> news_items = new ArrayList<>();
                    channel_name = "";
                    m_feeds.postValue(news_items);
                    e.printStackTrace();
                }
            }
        });
    }
}