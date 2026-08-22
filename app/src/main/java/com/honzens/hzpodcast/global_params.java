package com.honzens.hzpodcast;

import android.content.Context;
import android.util.Log;

public class global_params {
    //message defined
    public static int m_color_write = 0xFFFFFFFF;
    public static int m_color_yellow = 0xFFFFFF00;
    public static int m_color_red = 0xFFFF0000;
    public static int m_color_blue = 0xFF0000FF;
    public static int m_color_green = 0xFF00FF00;
    public static int m_color_black = 0xFF000000;
    public static int m_color_dark_red = 0xFF970000;
    public static int m_actionBar_Height = 0;
    public static String m_area = null;
    public static String m_code_last = null;
    public static void initialize() {
    }

    public static String rec_dir;
    public static int news_category = 0;
    //====================================================================
    public static void set_save_path(Context ctx) {
        try {
            java.io.File fs = ctx.getExternalFilesDir(null);
            if (fs == null)
                fs = ctx.getFilesDir();
            //fs.mkdirs();
            rec_dir = fs.getAbsolutePath();
        } catch (Exception e) {
            rec_dir = "";
            Log.e("global_params",e.toString());
        }
    }
}

