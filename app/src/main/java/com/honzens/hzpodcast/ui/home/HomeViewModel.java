package com.honzens.hzpodcast.ui.home;


import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.honzens.hzpodcast.classes.FeedItem;
import com.honzens.hzpodcast.classes.Podcast;
import com.honzens.hzpodcast.common.AtomParser;
import com.honzens.hzpodcast.common.FeedCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Podcast>> m_feeds = new MutableLiveData<>();
    private static final String API_URL = "https://itunes.apple.com/search";
    public LiveData<List<Podcast>> getFeeds()
    {
        return m_feeds;
    }
    public void searchPodcasts(String sKey, int limit, Context context)
    {
        String urlString = API_URL
                + "?term=" + Uri.encode(sKey)
                + "&media=podcast"
                + "&entity=podcast"
                + "&country=TW"
                + "&limit=" + Math.clamp(limit, 1, 200);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .get()
                .header("Accept", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                List<Podcast> news_items = new ArrayList<>();
                m_feeds.postValue(news_items);
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
                            if (!p.feedUrl.isEmpty()) {
                                p.setFavorite(FeedCache.isFavor(p.feedUrl));
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