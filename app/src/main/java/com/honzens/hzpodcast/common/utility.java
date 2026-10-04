package com.honzens.hzpodcast.common;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.honzens.hzpodcast.global_params;

public class utility {
    private final static String TAG = "Utility";
    static public void hard_save_current_setting(Context ctx) {
        try {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences("count", MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (global_params.m_code_last != null)
                    editor.putString("code_last", global_params.m_code_last);
                if (global_params.m_program_url != null)
                    editor.putString("program_url", global_params.m_program_url);
                else
                    editor.remove("program_url");
                if (global_params.m_program_player_speed != null)
                    editor.putString("program_player_speed", global_params.m_program_player_speed);
                else
                    editor.remove("program_player_speed");
                if (global_params.m_download_player_speed != null)
                    editor.putString("download_player_speed", global_params.m_download_player_speed);
                else
                    editor.remove("download_player_speed");
                if (global_params.m_country != null)
                    editor.putString("country", global_params.m_country);
                else
                    editor.remove("country");
                if (global_params.m_live_name != null)
                    editor.putString("live_name", global_params.m_live_name);
                else
                    editor.remove("live_name");
                if (global_params.m_live_region != null)
                    editor.putString("live_region", global_params.m_live_region);
                else
                    editor.remove("live_region");
                if (global_params.m_live_language != null)
                    editor.putString("live_language", global_params.m_live_language);
                else
                    editor.remove("live_language");
                if (global_params.getM_live_url != null)
                    editor.putString("live_url", global_params.getM_live_url);
                else
                    editor.remove("live_url");
                editor.apply();
            }
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }
    static public void hard_load_current_setting(Context ctx) {
        try {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences("count", MODE_PRIVATE);
            if (sharedPreferences != null) {
                global_params.m_code_last = sharedPreferences.getString("code_last", null);
                global_params.m_program_url = sharedPreferences.getString("program_url", null);
                global_params.m_program_player_speed = sharedPreferences.getString("program_player_speed", "1.0x");
                global_params.m_download_player_speed = sharedPreferences.getString("download_player_speed", "1.0x");
                global_params.m_country = sharedPreferences.getString("country", "tw");
                global_params.m_live_name = sharedPreferences.getString("live_name", null);
                global_params.m_live_region = sharedPreferences.getString("live_region", null);
                global_params.m_live_language = sharedPreferences.getString("live_language", null);
                global_params.getM_live_url = sharedPreferences.getString("live_url", null);
            }
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }
    static public void SetScreenAlwaysOn(@NonNull Context ctx, boolean bYes) {
        if (bYes)
            ((Activity)ctx).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            ((Activity)ctx).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}

