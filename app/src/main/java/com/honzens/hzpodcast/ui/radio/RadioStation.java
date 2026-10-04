package com.honzens.hzpodcast.ui.radio;

public class RadioStation {

    private final String name;
    private final String region;
    private final String language;
    private final String url;
    private final String mimeType;

    public RadioStation(String name,
                        String reg,
                        String lang,
                        String url,
                        String mimeType) {

        this.name = name;
        this.region = reg;
        this.language = lang;
        this.url = url;
        this.mimeType = mimeType;
    }
    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }
    public String getLanguage() {
        return language;
    }
    public String getUrl() {
        return url;
    }
    public String getMimeType() {
        return mimeType;
    }
}