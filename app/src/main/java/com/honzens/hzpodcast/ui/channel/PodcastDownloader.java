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
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
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
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = manager.query(query);
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
                handler.postDelayed(this,500);
            }
        };
        handler.post(runnable);
    }
    private static String createFileName(Episode episode) {
        String title = episode != null ? episode.title : null;
        if (title == null || title.trim().isEmpty()) {
            title = "podcast";
        }
        // 1. 正則：僅保留 中文、英文字母、數字、底線(_) 與 連字號(-)
        // 這能完美過濾斜線 /、冒號 :、單雙引號、空白鍵及任何不可見字元
        title = title.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5_-]", "");
        // 2. 防呆：若過濾後變成空字串，給予預設檔名
        if (title.isEmpty()) {
            title = "podcast_" + System.currentTimeMillis();
        }
        // 3. 限制檔名長度（建議保留，避免系統限制 max path 長度 255 字元）
        if (title.length() > 50) {
            title = title.substring(0, 50);
        }
        // 4. 補上副檔名
        return title + ".mp3";
    }
}
