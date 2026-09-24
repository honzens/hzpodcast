package com.honzens.hzpodcast.common;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
import android.app.Dialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.app.AlertDialog;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.global_params;
import java.util.Calendar;
import java.util.Locale;

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

