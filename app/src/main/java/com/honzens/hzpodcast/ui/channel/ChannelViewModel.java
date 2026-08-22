package com.honzens.hzpodcast.ui.channel;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.honzens.hzpodcast.classes.FeedItem;
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
    private final MutableLiveData<List<FeedItem>> m_feeds = new MutableLiveData<>();
    public LiveData<List<FeedItem>> getFeeds()
    {
        return m_feeds;
    }
    public void loadAtom(int idx, String url, Context context)
    {
    }
}