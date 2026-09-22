package com.honzens.hzpodcast.ui.download;


import android.content.Context;
import android.os.Environment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DownloadViewModel extends ViewModel {
    private final MutableLiveData<List<DownloadedEpisode>> m_feeds = new MutableLiveData<>();
    public LiveData<List<DownloadedEpisode>> getFeeds()
    {
        return m_feeds;
    }
    public void loadDownloadedMp3(Context context)
    {
        List<DownloadedEpisode> episodes = new ArrayList<>();
        File musicDirectory = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (musicDirectory == null) {
            m_feeds.postValue(episodes);
            return;
        }
        File podcastDirectory = new File(musicDirectory,"Podcast");
        if (!podcastDirectory.exists()) {
            podcastDirectory.mkdirs();
        }
        File[] files = podcastDirectory.listFiles();
        if (files == null) {
            m_feeds.postValue(episodes);
            return;
        }
        episodes.clear();
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            String name = file.getName().toLowerCase(Locale.ROOT);
            if (name.endsWith(".mp3")) {
                episodes.add(new DownloadedEpisode(file));
            }
        }
        episodes.sort((a, b) -> {
            if (a.getFile().lastModified() == b.getFile().lastModified())
                return 0;
            else
                return a.getFile().lastModified() < b.getFile().lastModified() ? 1 : -1;
        });
        m_feeds.postValue(episodes);
    }
}