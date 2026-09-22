package com.honzens.hzpodcast.ui.download;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.honzens.hzpodcast.MainActivity;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.common.FeedCache;
import com.honzens.hzpodcast.common.utility;
import com.honzens.hzpodcast.databinding.FragmentDownloadBinding;

import java.io.File;
import java.util.Locale;

public class DownloadFragment extends Fragment implements DownloadedMp3Adapter.Listener {
    private FragmentDownloadBinding binding;
    private DownloadedMp3Adapter m_adapter;
    private ExoPlayer m_player;
    private TextView m_txtPlayerTitle;
    private TextView m_txtCurrentTime;
    private TextView m_txtDuration;
    private SeekBar m_playerSeekBar;
    private Button m_btnPlayPause;
    private Button m_btnBack15;
    private Button m_btnForward15;
    private final Handler m_download_handler =
            new Handler(Looper.getMainLooper()) {
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case 1:
                            // 收到 message 1
                            break;
                        case 2:
                            // 收到 message 2
                            break;
                    }
                }
            };
    private final Runnable progressRunnable =
            new Runnable() {
                @Override
                public void run() {
                    updatePlayerProgress();
                    m_download_handler.postDelayed(this,500);
                }
            };
    private DownloadViewModel m_downloadViewModel;
    public DownloadFragment() {
        super();
    }
    @Override
    public void onResume() {
        super.onResume();
        m_download_handler.post(progressRunnable);
    }
    @Override
    public void onPause() {
        super.onPause();
        m_download_handler.removeCallbacks(progressRunnable);
        if (m_player != null && m_player.isPlaying()) {
            m_player.pause();
            binding.playerView.btnPlayPause.setText("▶");
        }
        utility.SetScreenAlwaysOn(requireActivity(),false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString("screen", "DownloadFragment");
            MainActivity.mFirebaseAnalytics.logEvent("open_screen", bundle);
        }
        Context ctx = requireContext();
        ((MainActivity) ctx).setActionBarText(getString(R.string.title_download));
        if (m_downloadViewModel == null)
            m_downloadViewModel = new ViewModelProvider(this).get(DownloadViewModel.class);
        binding.recyclerViewDownload.setLayoutManager(new LinearLayoutManager(ctx));
        binding.recyclerViewDownload.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
        m_adapter =  new DownloadedMp3Adapter(ctx,this);
        binding.recyclerViewDownload.setAdapter(m_adapter);
        m_downloadViewModel.getFeeds().observe(
                getViewLifecycleOwner(),
                data_items -> {
                    m_adapter.setData(data_items);
                    binding.recyclerViewDownload.post(() ->
                            binding.recyclerViewDownload.scrollToPosition(0)
                    );
                });
        //m_downloadViewModel.loadDownloadedMp3(ctx);
        //m_handler.post(progressRunnable);
        //
        m_txtPlayerTitle = binding.playerView.txtPlayerTitle;
        m_txtCurrentTime = binding.playerView.txtCurrentTime;
        m_txtDuration = binding.playerView.txtDuration;
        m_playerSeekBar = binding.playerView.playerSeekBar;
        m_btnPlayPause = binding.playerView.btnPlayPause;
        m_btnBack15 = binding.playerView.btnBack15;
        m_btnForward15 =  binding.playerView.btnForward15;
        initPlayer();
        m_downloadViewModel.loadDownloadedMp3(ctx);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_downloadViewModel = new ViewModelProvider(this).get(DownloadViewModel.class);
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
    @Override
    public void onDestroyView() {
        if (m_player != null) {
            m_player.release();
            m_player = null;
        }
        super.onDestroyView();
        binding = null;
    }
    private void initPlayer() {
        m_player = new ExoPlayer.Builder(requireContext()).build();
        m_btnPlayPause.setOnClickListener(v -> {
            if (m_player.isPlaying()) {
                m_player.pause();
                m_btnPlayPause.setText("▶");
            } else {
                m_player.play();
                m_btnPlayPause.setText("❚❚");
            }
        });
        m_btnBack15.setOnClickListener(v -> {
            long position = m_player.getCurrentPosition();
            m_player.seekTo(Math.max(0, position - 15_000));
        });
        m_btnForward15.setOnClickListener(v -> {
            long position = m_player.getCurrentPosition();
            long duration = m_player.getDuration();
            if (duration > 0) {
                m_player.seekTo(Math.min(duration, position + 15_000));
            }
        });
        m_playerSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (!fromUser) {
                            return;
                        }
                        long duration = m_player.getDuration();
                        if (duration <= 0) {
                            return;
                        }
                        long position = duration * progress / 1000;
                        m_txtCurrentTime.setText(formatTime(position));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        long duration = m_player.getDuration();
                        if (duration <= 0) {
                            return;
                        }
                        long position = duration * seekBar.getProgress() / 1000;
                        m_player.seekTo(position);
                    }
                });
    }
    @Override
    public void onPlay(DownloadedEpisode episode) {
        File file = episode.getFile();
        MediaItem mediaItem = MediaItem.fromUri(android.net.Uri.fromFile(file));
        m_player.setMediaItem(mediaItem);
        m_player.prepare();
        m_player.play();
        m_txtPlayerTitle.setText(episode.getName());
        m_btnPlayPause.setText("❚❚");
    }

    private void updatePlayerProgress() {
        if (m_player == null) {
            return;
        }
        long position = m_player.getCurrentPosition();
        long duration = m_player.getDuration();
        if (m_player.isPlaying()) {
            utility.SetScreenAlwaysOn(requireActivity(), position < duration);
        }
        else
            utility.SetScreenAlwaysOn(requireActivity(),false);
        if (duration > 0) {
            int progress = (int)((position * 1000) / duration);
            m_playerSeekBar.setProgress(progress);
            m_txtCurrentTime.setText(formatTime(position));
            m_txtDuration.setText(formatTime(duration));
        }
    }
    private String formatTime(long milliseconds) {
        if (milliseconds < 0) {
            return "00:00";
        }
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format(Locale.getDefault(),"%02d:%02d:%02d",hours,minutes,seconds);
        } else {
            return String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        }
    }

}
