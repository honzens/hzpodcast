package com.honzens.hzpodcast.ui.download;

import java.io.File;

public class DownloadedEpisode {
    private final File file;
    public DownloadedEpisode(File file) {
        this.file = file;
    }
    public File getFile() {
        return file;
    }
    public String getName() {
        return file.getName();
    }
    public long getSize() {
        return file.length();
    }
}