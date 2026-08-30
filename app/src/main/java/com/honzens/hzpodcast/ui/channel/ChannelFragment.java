package com.honzens.hzpodcast.ui.channel;

import static com.honzens.hzpodcast.common.utility.hard_save_current_setting;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.honzens.hzpodcast.MainActivity;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.classes.Episode;
import com.honzens.hzpodcast.common.FeedCache;
import com.honzens.hzpodcast.common.utility;
import com.honzens.hzpodcast.databinding.FragmentChannelBinding;
import com.honzens.hzpodcast.global_params;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.media3.common.MediaItem;

import java.util.Locale;

public class ChannelFragment extends Fragment implements EpisodeAdapter.Listener {
    private FragmentChannelBinding binding;
    private FavorFeedAdapter m_favor_adapter;
    private EpisodeAdapter m_program_adapter;
    private Handler m_handler_callback;
    private ExoPlayer m_player;
    private final Handler m_playerHandler = new Handler(Looper.getMainLooper());
    private Runnable m_playerProgressRunnable;
    private ChannelViewModel m_channelViewModel;
    public ChannelFragment() {
        super();
    }
    @Override
    public void onResume() {
        super.onResume();
        m_favor_adapter.setData(FeedCache.getFavorList());
        m_handler_callback.post(m_playerProgressRunnable);
    }
    public void onPause() {
        super.onPause();
        m_handler_callback.removeCallbacks(m_playerProgressRunnable);
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
            bundle.putString("screen", "ChannelFragment");
            MainActivity.mFirebaseAnalytics.logEvent("open_screen", bundle);
        }
        Context ctx = requireContext();
        m_player = new ExoPlayer.Builder(ctx).build();
        ((MainActivity) ctx).setActionBarText(getString(R.string.title_channel));
        binding.btnBack.setOnClickListener(v -> {
            global_params.m_program_url=null;
            switch_to_favor();
        });
        m_playerProgressRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        if (m_player != null) {
                            if (m_player.isPlaying()) {
                                utility.SetScreenAlwaysOn(requireActivity(),true);
                            }
                            long position = m_player.getCurrentPosition();
                            long duration = m_player.getDuration();
                            if (duration > 0) {
                                int progress = (int)((position * 1000) / duration);
                                binding.playerView.playerSeekBar.setProgress(progress);
                                binding.playerView.txtCurrentTime.setText(formatTime(position));
                                binding.playerView.txtDuration.setText(formatTime(duration));
                            }
                        }
                        m_playerHandler.postDelayed(this,500);
                    }
                };
        binding.playerView.playerSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && m_player != null) {
                            long duration = m_player.getDuration();
                            if (duration > 0) {
                                long position = duration * progress / 1000;
                                binding.playerView.txtCurrentTime.setText(formatTime(position));
                            }
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (m_player != null) {
                            long duration = m_player.getDuration();
                            long position = duration * seekBar.getProgress() / 1000;
                            m_player.seekTo(position);
                        }
                    }
                });
        binding.playerView.btnPlayPause.setOnClickListener(v -> {
            if (m_player == null) {
                return;
            }
            if (m_player.isPlaying()) {
                m_player.pause();
                binding.playerView.btnPlayPause.setText("▶");
            } else {
                m_player.play();
                binding.playerView.btnPlayPause.setText("❚❚");
            }
        });
        binding.playerView.btnBack15.setOnClickListener(v -> {
            if (m_player != null) {
                long position = m_player.getCurrentPosition();
                m_player.seekTo(Math.max(0, position - 15_000));
            }
        });
        binding.playerView.btnForward15.setOnClickListener(v -> {
            if (m_player != null) {
                long position = m_player.getCurrentPosition();
                long duration = m_player.getDuration();
                m_player.seekTo(Math.min(duration, position + 15_000));
            }
        });
        binding.recyclerViewPrograms.setLayoutManager(new LinearLayoutManager(ctx));
        binding.recyclerViewPrograms.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
        binding.recyclerViewChannels.setLayoutManager(new LinearLayoutManager(ctx));
        binding.recyclerViewChannels.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
        //binding.recyclerView.setBackgroundColor(Color.BLACK);
        if (m_channelViewModel == null)
            m_channelViewModel = new ViewModelProvider(this).get(com.honzens.hzpodcast.ui.channel.ChannelViewModel.class);
        if (m_favor_adapter == null || m_program_adapter==null) {
            if (m_handler_callback == null) {
                m_handler_callback = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(@NonNull Message msg) {
                        switch (msg.what) {
                            case 1:
                                String url = (String) msg.obj;
                                global_params.m_program_url=url;
                                utility.hard_save_current_setting(ctx);
                                switch_to_program();
                                show_progress_bar();
                                m_channelViewModel.loadPrograms(url, ctx);
                                break;
                            case 2:
                                break;
                        }
                    }
                };
            }
            m_favor_adapter = new FavorFeedAdapter(ctx, m_handler_callback);
            binding.recyclerViewChannels.setAdapter(m_favor_adapter);
            m_program_adapter = new EpisodeAdapter(ctx,this);
            binding.recyclerViewPrograms.setAdapter(m_program_adapter);
        }
        //===========
        m_channelViewModel.getFeeds().observe(
                getViewLifecycleOwner(),
                data_items -> {
                    if (binding.progressBar.getVisibility() == View.VISIBLE) {
                        hide_progress_bar();
                    }
                    binding.txtChannelName.setText(m_channelViewModel.channel_name);
                    m_program_adapter.setData(data_items);
                    binding.recyclerViewPrograms.post(() ->
                            binding.recyclerViewPrograms.scrollToPosition(0)
                    );
                });
        if (global_params.m_program_url != null) {
            m_channelViewModel.loadPrograms(global_params.m_program_url, ctx);
            switch_to_program();
            show_progress_bar();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_channelViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        binding = FragmentChannelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
    private void initPlayer() {
        if (m_player != null && m_player.isPlaying()) {
            m_player.stop();
            binding.playerView.btnPlayPause.setText("▶");
            binding.playerView.txtCurrentTime.setText("00:00");
            binding.playerView.txtDuration.setText("00:00");
            binding.playerView.txtPlayerTitle.setText("目前沒有播放");
            binding.playerView.playerSeekBar.setProgress(0);
        }
    }
    private void hide_progress_bar() {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.recyclerViewPrograms.setVisibility(View.VISIBLE);

    }
    private void show_progress_bar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerViewPrograms.setVisibility(View.INVISIBLE);
    }
    private void switch_to_program() {
        binding.recyclerViewChannels.setVisibility(View.INVISIBLE);
        //
        binding.recyclerViewPrograms.setVisibility(View.VISIBLE);
        binding.btnBack.setVisibility(View.VISIBLE);
        binding.playerContainer.setVisibility(View.VISIBLE);
        binding.txtChannelName.setVisibility(View.VISIBLE);
    }
    private void switch_to_favor() {
        binding.recyclerViewChannels.setVisibility(View.VISIBLE);
        //
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.recyclerViewPrograms.setVisibility(View.INVISIBLE);
        binding.btnBack.setVisibility(View.INVISIBLE);
        initPlayer();
        binding.playerContainer.setVisibility(View.INVISIBLE);
        binding.txtChannelName.setVisibility(View.INVISIBLE);
        binding.txtChannelName.setText(getString(R.string.loading));
    }
    @Override
    public void onDestroyView() {
        hard_save_current_setting(requireActivity());
        super.onDestroyView();
        if (m_player != null) {
            m_player.release();
            m_player = null;
        }
        binding = null;
    }
    private String formatTime(long millis) {
        if (millis < 0) {
            return "00:00";
        }
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        }
    }
    @Override
    public void onPlay(Episode episode) {
        String url = episode.audioUrl;
        if (url == null || url.isEmpty()) {
            Toast.makeText(requireContext(), "沒有 MP3 URL", Toast.LENGTH_SHORT).show();
            return;
        }
        MediaItem mediaItem = MediaItem.fromUri(url);
        m_player.setMediaItem(mediaItem);
        m_player.prepare();
        m_player.play();
        m_playerProgressRunnable.run();
        binding.playerView.txtPlayerTitle.setText(episode.title);
        binding.playerView.btnPlayPause.setText("❚❚");

    }
    @Override
    public void onDownload(Episode episode, ProgressBar download_progress, TextView percentText) {
        PodcastDownloader.download(
                requireContext(),
                episode,
                download_progress,
                percentText
        );
    }
}
