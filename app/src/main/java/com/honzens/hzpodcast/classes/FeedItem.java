package com.honzens.hzpodcast.classes;

public class FeedItem {
    private String title;
    private String url;
    private String summary;
    private String content;
    public String pubDate;
    public boolean isRead;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() { return content; }
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
    public String getPubDate() {
        return pubDate;
    }
}
