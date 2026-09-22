package com.honzens.hzpodcast.ui.channel;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.honzens.hzpodcast.classes.Episode;

public class PodcastDownloader {

    public static void download(Context context, Episode episode, ProgressBar progressBar, TextView percentText) {
        String url = episode.audioUrl;
        if (url == null || url.isEmpty()) {
            Toast.makeText(
                    context,
                    "沒有 MP3 URL",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        String fileName = createFileName(episode);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(episode.title);
        request.setDescription("正在下載 Podcast");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MUSIC,"Podcast/" + fileName);
        DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager == null) {
            return;
        }
        long downloadId = manager.enqueue(request);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        percentText.setVisibility(TextView.VISIBLE);
        monitorDownload(manager, downloadId, progressBar, percentText);
    }
    private static void monitorDownload(DownloadManager manager, long downloadId, ProgressBar progressBar, TextView percentText) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DownloadManager.Query query =
                        new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor =
                        manager.query(query);
                if (cursor == null) {
                    return;
                }
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DownloadManager.COLUMN_STATUS
                            )
                    );
                    long downloaded =
                            cursor.getLong(
                                    cursor.getColumnIndexOrThrow(
                                            DownloadManager
                                                    .COLUMN_BYTES_DOWNLOADED_SO_FAR
                                    )
                            );
                    long total =
                            cursor.getLong(
                                    cursor.getColumnIndexOrThrow(
                                            DownloadManager
                                                    .COLUMN_TOTAL_SIZE_BYTES
                                    )
                            );
                    if (total > 0) {
                        int progress = (int)((downloaded * 100) / total);
                        progressBar.setProgress(
                                progress
                        );
                        percentText.setText(
                                progress + "%"
                        );
                    }
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        progressBar.setProgress(100);
                        percentText.setText("完成");
                        cursor.close();
                        return;
                    }
                    if (status == DownloadManager.STATUS_FAILED) {
                        percentText.setText("失敗");
                        cursor.close();
                        return;
                    }
                }
                cursor.close();
                handler.postDelayed(
                        this,
                        500
                );
            }
        };
        handler.post(runnable);
    }
    private static String createFileName(
            Episode episode) {
        String title = episode.title;
        if (title == null) {
            title = "podcast";
        }
        title = title.replaceAll(
                "[\\\\/:*?\"<>|]",
                "_"
        );
        return title + ".mp3";
    }
}
