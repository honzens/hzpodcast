package com.honzens.hzpodcast.classes;

import java.util.Date;

public class FavorFeedItem {
    public String collectionName;
    public String artistName;
    public String feedUrl;
    public String artworkUrl;
    public int trackCount;
    public long collectionId;
    public long add_time;

    public String getName() {
        return collectionName;
    }

    public void setName(String name) {
        this.collectionName = name;
    }

    public String getUrl() {
        return feedUrl;
    }

    public void setUrl(String url) {
        this.feedUrl = url;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String name) {
        this.artistName = name;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String url) {
        this.artworkUrl = url;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int count) {
        this.trackCount = count;
    }

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long id) {
        this.collectionId = id;
    }
    public FavorFeedItem(String name, String author, String url) {
        this.collectionName = name;
        this.artistName = author;
        this.feedUrl = url;
        this.add_time = new Date().getTime();
    }
    public FavorFeedItem(String artworkUrl, String name, String author, String url) {
        this.artworkUrl = artworkUrl;
        this.collectionName = name;
        this.artistName = author;
        this.feedUrl = url;
        this.add_time = new Date().getTime();
    }
    public FavorFeedItem() {
        this.artworkUrl = "";
        this.collectionName = "";
        this.artistName = "";
        this.feedUrl = "";
        this.add_time = new Date().getTime();
    }
}
