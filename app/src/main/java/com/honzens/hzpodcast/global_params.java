package com.honzens.hzpodcast;

import android.content.Context;
import android.util.Log;

import com.honzens.hzpodcast.common.utility;

public class global_params {
    //message defined
    public static String m_code_last = null;
    public static String m_program_url = null;
    public static String m_program_player_speed = null;
    public static String m_download_player_speed = null;
    public static void initialize() {

    }

    public static String save_dir;
    //====================================================================
    public static void set_save_path(Context ctx) {
        try {
            java.io.File fs = ctx.getExternalFilesDir(null);
            if (fs == null)
                fs = ctx.getFilesDir();
            //fs.mkdirs();
            save_dir = fs.getAbsolutePath();
        } catch (Exception e) {
            save_dir = "";
            Log.e("global_params",e.toString());
        }
    }
}

