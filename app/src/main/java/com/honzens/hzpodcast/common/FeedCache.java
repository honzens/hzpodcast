package com.honzens.hzpodcast.common;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honzens.hzpodcast.classes.FavorItem;
import com.honzens.hzpodcast.classes.FeedItem;
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
import java.util.Map;

public class FeedCache {
    //已讀功能
    private static HashMap<String, FavorItem> m_favorMap;
    private static boolean update_favor_map = false;
    public static void loadFavorMap(Context context) {
        try {
            File file = new File(context.getCacheDir(), "favor_set.json");
            if (!file.exists()) {
                FeedCache.m_favorMap = new HashMap<>();
                return;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, ReadItem>>(){}.getType();
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
        FeedCache.m_favorMap.put(url, new FavorItem(url, channel_name, author));
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
    public static void save_stock_cache(String sKey, Context context, List<FeedItem> list) {
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
    public static void save_news_cache(int idx, Context context, List<FeedItem> list) {
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
    public static List<FeedItem> load_stock_cache(String sKey, Context context) {
        String sFile = getStockCacheName(sKey);
        try {
            File file = new File(context.getCacheDir(), sFile);
            if (!file.exists())
                return new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<List<FeedItem>>(){}.getType();
            FileReader reader = new FileReader(file);
            List<FeedItem> list = gson.fromJson(reader, type);
            reader.close();
            for (FeedItem item : list) {
                //item.isRead = (FeedCache.isRead(item.getUrl()));
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public static List<FeedItem> load_news_cache(int idx, Context context) {
        String sFile = getNewsCacheName(idx);
        try {
            File file = new File(context.getCacheDir(), sFile);
            if (!file.exists())
                return new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<List<FeedItem>>(){}.getType();
            FileReader reader = new FileReader(file);
            List<FeedItem> list = gson.fromJson(reader, type);
            reader.close();
            for (FeedItem item : list) {
                //item.isRead = (FeedCache.isRead(item.getUrl()));
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
