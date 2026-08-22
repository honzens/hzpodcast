package com.honzens.hzpodcast;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.honzens.hzpodcast.databinding.ActivityMainBinding;
import com.honzens.hzpodcast.common.FeedCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<IntentSenderRequest> updateLauncher;
    private ActionBar m_actionbar;
    public ActivityMainBinding binding;
    private Handler m_handler;
    private AdView m_adView;
    public static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //Bundle bundle = new Bundle();
        //bundle.putString("screen", "MainActivity");
        //mFirebaseAnalytics.logEvent("open_screen", bundle);
        //=============
        m_handler = new Handler(Looper.getMainLooper());
        global_params.set_save_path(this);
        //------------
        //google ads=====
        //MobileAds.setRequestConfiguration(
        //        new com.google.android.gms.ads.RequestConfiguration.Builder()
        //                .setTestDeviceIds(List.of("3038FCE8C4F733B35E48BC26D82608D6"))
        //                .build()
        //);
        MobileAds.initialize(this, initializationStatus -> {});
        //================
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        ViewCompat.setOnApplyWindowInsetsListener(binding.container, (v, windowInsets) -> {
            // 取得 statusBars (包含 Status Bar / Cutout 瀏海) 的高度
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            // 設定根 View 的 top padding，將 ActionBar/Toolbar 硬推下來
            v.setPadding(v.getPaddingLeft(), insets.top, v.getPaddingRight(), v.getPaddingBottom());
            return windowInsets;
        });
        // Calling the support action bar and setting it to custom
        setSupportActionBar(binding.toolbar);
        m_actionbar = getSupportActionBar();
        if (m_actionbar != null) {
            m_actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            // Displaying the custom layout in the ActionBar
            m_actionbar.setDisplayShowCustomEnabled(true);
            m_actionbar.setCustomView(R.layout.my_actionbar);
            View view = m_actionbar.getCustomView();
            ImageView ico = view.findViewById(R.id.mainbar_image);
            ico.setAdjustViewBounds(true);
            ico.setScaleType(ImageView.ScaleType.FIT_XY);
            //TextView name = view.findViewById(R.id.mainbar_text);
            TextView version = view.findViewById(R.id.mainbar_ver);
            version.setText(com.honzens.hzpodcast.BuildConfig.VERSION_NAME);
        }
        // BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        checkMyPermission();
        //
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_channel)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
        //ads
        m_adView = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        m_adView.loadAd(adRequest);
        //update version
        updateVersion();
    }
    public void updateVersion() {
        /*
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) &&
                    !isFinishing() &&
                    !isDestroyed()) {
                //upgrade
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // an activity result launcher registered via registerForActivityResult
                            updateLauncher,
                            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                            // flexible updates.
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                                    .build());
                } catch (Exception e) {
                    Log.e("Update", "Start update failed", e);
                }
            }
        }).addOnFailureListener(e -> {
            if (e instanceof InstallException) {
                int errorCode = ((InstallException) e).getErrorCode();
                if (errorCode == InstallErrorCode.ERROR_APP_NOT_OWNED) {
                    Log.w("Update", "Get update info failed: App not owned (sideloaded).");
                    return;
                }
            }
            Log.e("Update", "Get update info failed", e);
        });
        */
    }
    public void setActionBarText(String sTitle)
    {
        if (m_actionbar != null) {
            View view = Objects.requireNonNull(m_actionbar).getCustomView();
            TextView name = view.findViewById(R.id.mainbar_text);
            name.setText(sTitle);
        }
    }
    private void checkMyPermission() {
        ArrayList<String> perms = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                perms.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                perms.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            perms.add(android.Manifest.permission.RECORD_AUDIO);
        }
        if (!perms.isEmpty()) {
            requestPermissions(perms.toArray(new String[0]), 200);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            for (int ik=0; ik<permissions.length; ik++) {
                if (grantResults[ik] != PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (m_adView != null) {
            m_adView.resume();
        }
    }
    @Override
    protected void onPause() {
        if (m_adView != null) {
            m_adView.pause();
        }
        com.honzens.hzpodcast.common.utility.hard_save_current_setting(this);
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        if (m_adView != null) {
            m_adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_about) {
            return true;
        }
        else if (id == R.id.menu_quit) {
            m_handler.post(this::finish);
            return true;
        }
        else if (id == R.id.menu_feedback) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}