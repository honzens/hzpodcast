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
            }
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }

    static public void description_dialog(Context ctx, String strTitle, String strContent, float fontSize, android.content.DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if (strTitle != null && !strTitle.isEmpty()) {
            LinearLayout layout = new LinearLayout(ctx);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.setBackgroundColor(Color.LTGRAY);
            //layout.setBackground(AppCompatResources.getDrawable(ctx, R.drawable.textview_border_only));
            LinearLayout.LayoutParams lpps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageView myIcon = new ImageView(ctx);
            myIcon.setImageDrawable(AppCompatResources.getDrawable(ctx, R.drawable.ic_info_24dp));
            layout.addView(myIcon, lpps);
            TextView myTitle=new TextView(ctx);
            myTitle.setText(strTitle);
            myTitle.setTextSize(fontSize);
            layout.addView(myTitle, lpps);
            //TextView myVersion=new TextView(ctx);
            //myVersion.setText(BuildConfig.VERSION_NAME);
            //myVersion.setTextSize(fontSize);
            //layout.addView(myVersion, lpps);
            builder.setCustomTitle(layout);
        }
        //builder.setMessage(strContent);
        TextView myView = new TextView(ctx);
        myView.setText(strContent);
        myView.setTextSize(fontSize);
        myView.setTextIsSelectable(true);
        builder.setView(myView);
        builder.setNegativeButton(ctx.getString(R.string.i_know), listener);
        //
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(Dialog.BUTTON_POSITIVE).setTextSize(fontSize);
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextSize(fontSize);
            }
        });
        //
        dialog.show();
    }

    public static void SetScreenAlwaysOn(@NonNull Context ctx, boolean bYes) {
        if (bYes)
            ((Activity)ctx).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            ((Activity)ctx).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
/*
    private void sendNotification(Context ctx, Bundle bundle) {
        int notificationID;
        int pendingIntentRequestCode;

        notificationID = pendingIntentRequestCode = (int)System.currentTimeMillis(); //使用當前時間作為ID與RequestCode
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Activity.NOTIFICATION_SERVICE);
        String channelId = FirebaseService.FCM_CHANNEL_ID;
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, FirebaseService.FCM_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // 初始化介面控件與點擊事件
        Intent notificationIntent = new Intent(ctx, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        notificationIntent.putExtras(bundle);

        final int flags;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        else
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, pendingIntentRequestCode, notificationIntent, flags);
        Resources res = ctx.getResources();
        int smallIconID = R.drawable.ic_notify;
        // 震動(DEFAULT_VIBRATE).
        // 音效(DEFAULT_VIBRATE).
        // 燈光(DEFAULT_LIGHT).
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(ctx, channelId)
                        .setSmallIcon(smallIconID)
                        .setContentTitle(data.Title)
                        .setContentText(data.Message)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_ALL)                  // 加上提醒效果
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(data.Message));

        notificationBuilder.setSmallIcon(smallIconID);
        notificationBuilder.setColor(res.getColor(R.color.transparent, null));
        try {
            notificationManager.notify("", notificationID, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
/*
    // 建立 WindowManager
    static public void add_hint(@NonNull Context ctx, String strContent)
    {
        int iwidth;
        WindowManager aWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = aWindowManager.getCurrentWindowMetrics();
            iwidth = windowMetrics.getBounds().width();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            aWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
            iwidth = displayMetrics.widthPixels;
        }
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout aRlMain = (ConstraintLayout) layoutInflater.inflate(R.layout.popup_message_layout, null, false);
        aRlMain.setVisibility(View.INVISIBLE);

        TextView aText = (TextView) aRlMain.findViewById(R.id.popup_textView);
        aText.setText(strContent);

        WindowManager.LayoutParams wmlp = new WindowManager.LayoutParams();
        wmlp.width = iwidth;
        wmlp.height = 40;
        wmlp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        wmlp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmlp.format = PixelFormat.TRANSLUCENT;
        wmlp.gravity = Gravity.BOTTOM | Gravity.START;
        wmlp.x = 0;
        wmlp.y = 0;
        try {
            aWindowManager.addView(aRlMain, wmlp);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
 */
}

