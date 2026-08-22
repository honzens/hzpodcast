package com.honzens.hzpodcast.classes;

public class FavorItem {
    public String url;
    public String channel_name;
    public String author;
    public FavorItem(String url, String channel_name, String author) {
        this.url = url;
        this.channel_name = channel_name;
        this.author = author;
    }
}
