package com.honzens.hzpodcast.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honzens.hzpodcast.classes.FavorFeedItem;
import com.honzens.hzpodcast.classes.Programs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FeedCache {
    private static HashMap<String, FavorFeedItem> m_favorMap;
    private static boolean update_favor_map = false;

    //最愛播客列表
    public static List<FavorFeedItem> getFavorList() {
        List<FavorFeedItem> list = new ArrayList<>();
        if (FeedCache.m_favorMap == null) {
            FeedCache.m_favorMap = new HashMap<>();
        }

        Gson gson = new Gson();
        List<String> keysToRemove = new ArrayList<>();

        for (Map.Entry<String, FavorFeedItem> entry : FeedCache.m_favorMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof FavorFeedItem) {
                list.add((FavorFeedItem) value);
            } else if (value != null) {
                // 如果在反序列化時被解析為 LinkedTreeMap 或其他 Map，嘗試轉換回 FavorFeedItem
                try {
                    FavorFeedItem item = gson.fromJson(gson.toJsonTree(value), FavorFeedItem.class);
                    if (item != null) {
                        entry.setValue(item);
                        list.add(item);
                        update_favor_map = true;
                    } else {
                        keysToRemove.add(entry.getKey());
                    }
                } catch (Exception e) {
                    keysToRemove.add(entry.getKey());
                }
            }
        }

        for (String key : keysToRemove) {
            FeedCache.m_favorMap.remove(key);
            update_favor_map = true;
        }

        list.sort((o1, o2) -> Long.compare(o2.add_time, o1.add_time));
        return list;
    }

    //載入我的最愛列表
    public static void loadFavorMap(Context context) {
        try {
            File file = new File(context.getCacheDir(), "favor_lst.json");
            if (!file.exists()) {
                FeedCache.m_favorMap = new HashMap<>();
                return;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, FavorFeedItem>>(){}.getType();
            FileReader reader = new FileReader(file);
            HashMap<String, Object> rawMap = gson.fromJson(reader, new TypeToken<HashMap<String, Object>>(){}.getType());
            reader.close();

            HashMap<String, FavorFeedItem> cleanMap = new HashMap<>();
            if (rawMap != null) {
                for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                    Object val = entry.getValue();
                    if (val instanceof FavorFeedItem) {
                        cleanMap.put(entry.getKey(), (FavorFeedItem) val);
                    } else if (val != null) {
                        try {
                            FavorFeedItem item = gson.fromJson(gson.toJsonTree(val), FavorFeedItem.class);
                            if (item != null) {
                                cleanMap.put(entry.getKey(), item);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            FeedCache.m_favorMap = cleanMap;
        } catch (Exception e) {
            e.printStackTrace();
            if (FeedCache.m_favorMap == null) {
                FeedCache.m_favorMap = new HashMap<>();
            }
        }
    }

    //儲存我的最愛列表
    public static void saveFavorMap(Context context) {
        if (!update_favor_map)
            return;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(FeedCache.m_favorMap);
            File file = new File(context.getCacheDir(), "favor_lst.json");
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
            update_favor_map = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFavor(String artworkurl, String url, String channel_name, String author) {
        if (FeedCache.m_favorMap == null)
            FeedCache.m_favorMap = new HashMap<>();
        if (FeedCache.m_favorMap.containsKey(url))
            return;
        FeedCache.m_favorMap.put(url, new FavorFeedItem(artworkurl, channel_name, author, url));
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
    public static String getSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 將 byte 陣列轉成 16 進位字串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getEpCacheName(String url) {
        return String.format(Locale.getDefault(), "%s.json", getSha256(url));
    }

    public static void clear_all(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            File[] files = cacheDir.listFiles((dir, name) ->
                    name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("favor_lst"))
                        continue;
                    long time = file.lastModified();
                    if (new Date().getTime() - time > 1000 * 60 * 60 * 24 * 15)
                        file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save_ep_cache(Context context, String url, @NonNull Programs progs) {
        if (progs.title == null || progs.title.isEmpty())
            return;
        String sFile = getEpCacheName(url);
        try {
            Gson gson = new Gson();
            String json = gson.toJson(progs);
            File file = new File(context.getCacheDir(), sFile);
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Programs load_ep_cache(Context context, String url) {
        String sFile = getEpCacheName(url);
        try {
            File file = new File(context.getCacheDir(), sFile);
            if (!file.exists())
                return new Programs();
            FileReader reader = new FileReader(file);
            Gson gson = new Gson();
            // 將 JSON 內容自動解析並轉成 Programs 物件
            Programs progs = gson.fromJson(reader, Programs.class);
            reader.close();
            return progs != null ? progs : new Programs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Programs();
    }
}
