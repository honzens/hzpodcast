package com.honzens.hzpodcast.common;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honzens.hzpodcast.classes.FavorFeedItem;
import com.honzens.hzpodcast.classes.ReadItem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FeedCache {
    private static HashMap<String, FavorFeedItem> m_favorMap;
    private static boolean update_favor_map = false;
    public static List<FavorFeedItem> getFavorList() {
        List<FavorFeedItem> list = new ArrayList<>();
        if (FeedCache.m_favorMap == null)
            FeedCache.m_favorMap = new HashMap<>();
        for (String key : FeedCache.m_favorMap.keySet()) {
            list.add(FeedCache.m_favorMap.get(key));
        }
        return list;
    }
    public static void loadFavorMap(Context context) {
        try {
            File file = new File(context.getCacheDir(), "favor_set.json");
            if (!file.exists()) {
                FeedCache.m_favorMap = new HashMap<>();
                return;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, FavorFeedItem>>(){}.getType();
            FileReader reader = new FileReader(file);
            FeedCache.m_favorMap = gson.fromJson(reader, type);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void saveFavorMap(Context context) {
        if (!update_favor_map)
            return;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(FeedCache.m_favorMap);
            File file = new File(context.getCacheDir(), "favor_set.json");
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
            update_favor_map = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void addFavor(String url, String channel_name, String author) {
        if (FeedCache.m_favorMap == null)
            FeedCache.m_favorMap = new HashMap<>();
        if (FeedCache.m_favorMap.containsKey(url))
            return;
        FeedCache.m_favorMap.put(url, new FavorFeedItem(channel_name, author, url));
        update_favor_map = true;
    }
    public static void delFavor(String url) {
        if (FeedCache.m_favorMap == null)
            FeedCache.m_favorMap = new HashMap<>();
        FeedCache.m_favorMap.remove(url);
        update_favor_map = true;
    }
    public static boolean isFavor(String Key) {
        if (FeedCache.m_favorMap == null)
            FeedCache.m_favorMap = new HashMap<>();
        return FeedCache.m_favorMap.containsKey(Key);
    }
    //========
    private static String getNewsCacheName(int idx) {
        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DAY_OF_MONTH, -iday);
        return String.format(Locale.getDefault(), "%d_cache_%d%02d%02d%02d.json", idx,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY));
    }
    private static String getStockCacheName(String sKey) {
        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DAY_OF_MONTH, -iday);
        return String.format(Locale.getDefault(), "%s_cache_%d%02d%02d%02d.json", sKey,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY));
    }
    public static void clear_all(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            File[] files = cacheDir.listFiles((dir, name) ->
                    name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    long time = file.lastModified();
                    if (new Date().getTime() - time > 1000 * 60 * 60 * 24 * 7)
                        file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void save_stock_cache(String sKey, Context context, List<FavorFeedItem> list) {
        String sFile = getStockCacheName(sKey);
        try {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            File file = new File(context.getCacheDir(), sFile);
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void save_news_cache(int idx, Context context, List<FavorFeedItem> list) {
        String sFile = getNewsCacheName(idx);
        try {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            File file = new File(context.getCacheDir(), sFile);
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<FavorFeedItem> load_stock_cache(String sKey, Context context) {
        String sFile = getStockCacheName(sKey);
        try {
            File file = new File(context.getCacheDir(), sFile);
            if (!file.exists())
                return new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<List<FavorFeedItem>>(){}.getType();
            FileReader reader = new FileReader(file);
            List<FavorFeedItem> list = gson.fromJson(reader, type);
            reader.close();
            for (FavorFeedItem item : list) {
                //item.isRead = (FeedCache.isRead(item.getUrl()));
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public static List<FavorFeedItem> load_news_cache(int idx, Context context) {
        String sFile = getNewsCacheName(idx);
        try {
            File file = new File(context.getCacheDir(), sFile);
            if (!file.exists())
                return new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<List<FavorFeedItem>>(){}.getType();
            FileReader reader = new FileReader(file);
            List<FavorFeedItem> list = gson.fromJson(reader, type);
            reader.close();
            for (FavorFeedItem item : list) {
                //item.isRead = (FeedCache.isRead(item.getUrl()));
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
