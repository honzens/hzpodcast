package com.honzens.hzpodcast.ui.home;


import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.honzens.hzpodcast.classes.Podcast;
import com.honzens.hzpodcast.common.FeedCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Podcast>> m_feeds = new MutableLiveData<>();
    private static final String API_URL = "https://itunes.apple.com/search";
    //private static final String API_URL = "https://www.apple.com/tw/search";
    public LiveData<List<Podcast>> getFeeds()
    {
        return m_feeds;
    }
    public void searchPodcasts(String sKey, String zone, int limit, Context context)
    {
        String urlString = API_URL
                + "?term=" + Uri.encode(sKey)
                + "&media=podcast"
                + "&entity=podcast"
                + "&country=" + zone
                + "&limit=" + Math.clamp(limit, 1, 50);
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true) // 允許連線失敗時自動重試
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();

        Request request = new Request.Builder()
                .url(urlString)
                .get()
                .header("Accept", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                m_feeds.postValue(new ArrayList<>());
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<Podcast> news_items = new ArrayList<>();
                try {
                    JSONArray array = new JSONObject(response.body().string()).optJSONArray("results");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.getJSONObject(i);
                            Podcast p = new Podcast();
                            p.collectionName = item.optString("collectionName");
                            p.artistName = item.optString("artistName");
                            p.feedUrl = item.optString("feedUrl");
                            p.artworkUrl = item.optString("artworkUrl600", item.optString("artworkUrl100"));
                            p.genre = item.optString("primaryGenreName");
                            p.trackCount = item.optInt("trackCount");
                            p.collectionId = item.optLong("collectionId");
                            p.artworkUrl100 = item.optString("artworkUrl100");
                            if (!p.feedUrl.isEmpty() && !p.collectionName.isEmpty()) {
                                news_items.add(p);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                m_feeds.postValue(news_items);
            }
        });
    }
}