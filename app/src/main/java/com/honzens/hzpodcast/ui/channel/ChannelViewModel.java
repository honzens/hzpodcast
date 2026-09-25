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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
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
    public String author = "";
    public String description = "";
    public String image_url = "";

    public void loadPrograms(String url, Context ctx)
    {
        Programs progs = FeedCache.load_ep_cache(ctx, url);
        if (progs!=null && progs.episodes!=null && !progs.episodes.isEmpty()) {
            channel_name = progs.title;
            author = progs.author;
            description = progs.description;
            image_url = progs.imageUrl;
            m_feeds.postValue(progs.episodes);
            return;
        }
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true) // 允許連線失敗時自動重試
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                channel_name = "下載失敗!";
                author = "";
                description = "";
                image_url = "";
                m_feeds.postValue(new ArrayList<>());
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    Programs item = AtomParser.parsePodcast(response.body().byteStream());
                    channel_name = item.title;
                    author = item.author;
                    description = item.description;
                    image_url = item.imageUrl;
                    m_feeds.postValue(item.episodes);
                    //save to cache
                    FeedCache.save_ep_cache(ctx, url, item);
                    //=============
                } catch (Exception e) {
                    channel_name = "解析內容失敗!";
                    author = "";
                    description = "";
                    image_url = "";
                    m_feeds.postValue(new ArrayList<>());
                    e.printStackTrace();
                }
            }
        });
    }
}