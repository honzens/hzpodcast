package com.honzens.hzpodcast.ui.radio;

import androidx.annotation.NonNull;

public class RadioStation {
    private final String name;
    private final String frequency;
    private final String url;
    public RadioStation(String name, String frequency, String url) {
        this.name = name;
        this.frequency = frequency;
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public String getFrequency() {
        return frequency;
    }
    public String getUrl() {
        return url;
    }
    @NonNull
    @Override
    public String toString() {
        return name + " " + frequency;
    }
}