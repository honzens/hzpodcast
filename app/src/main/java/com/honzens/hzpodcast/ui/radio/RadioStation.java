package com.honzens.hzpodcast.ui.radio;

public class RadioStation {

    private final String name;
    private final String frequency;
    private final String url;
    private final String mimeType;

    public RadioStation(String name,
                        String frequency,
                        String url,
                        String mimeType) {

        this.name = name;
        this.frequency = frequency;
        this.url = url;
        this.mimeType = mimeType;
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

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String toString() {
        return name + " " + frequency;
    }
}