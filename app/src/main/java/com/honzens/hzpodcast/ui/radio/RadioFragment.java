package com.honzens.hzpodcast.ui.radio;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.honzens.hzpodcast.MainActivity;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.common.utility;
import com.honzens.hzpodcast.global_params;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RadioFragment extends Fragment {
    private ExoPlayer player;
    private TextView tvStation;
    private TextView tvRegion;
    private TextView tvStatus;
    private ImageButton btnPlay;
    private Button btnReconnect;
    private RecyclerView recyclerView;
    private RadioAdapter adapter;
    private final List<RadioStation> stations = new ArrayList<>();
    private RadioStation currentStation;
    private boolean userRequestedPlay = false;
    private final Handler handler =
            new Handler(Looper.getMainLooper());
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_radio,
                container,
                false
        );
        ((MainActivity) requireActivity()).setActionBarText(getString(R.string.title_radio));
        tvStation = view.findViewById(R.id.tv_station);
        tvRegion = view.findViewById(R.id.tv_region);
        tvStatus = view.findViewById(R.id.tv_status);
        btnPlay = view.findViewById(R.id.btn_play);
        btnReconnect = view.findViewById(R.id.btn_reconnect);
        recyclerView = view.findViewById(
                R.id.recycler_radio
        );
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        loadStations();
        adapter = new RadioAdapter(
                stations,
                station -> playStation(station)
        );
        if (global_params.m_live_name != null && global_params.m_live_region != null && global_params.getM_live_url != null) {
            tvStation.setText(global_params.m_live_name);
            tvRegion.setText(global_params.m_live_region);
            currentStation = new RadioStation(global_params.m_live_name,global_params.m_live_region,global_params.m_live_language,global_params.getM_live_url,null);
            adapter.selectedStation = currentStation;
        }
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        initPlayer();
        btnPlay.setOnClickListener(v -> {
            if (player == null) {
                initPlayer();
            }
            if (currentStation == null) {
                if (!stations.isEmpty()) {
                    playStation(stations.get(0));
                }
                return;
            }
            if (player.isPlaying()) {
                userRequestedPlay = false;
                player.pause();
                utility.SetScreenAlwaysOn(requireContext(), false);
            } else {
                userRequestedPlay = true;
                if (player.getPlaybackState()
                        == Player.STATE_IDLE) {
                    reconnect();
                } else {
                    player.play();
                    utility.SetScreenAlwaysOn(requireContext(), true);
                }
            }
        });
        btnReconnect.setOnClickListener(v -> reconnect());
        return view;
    }
    // ============================================================
    // Player
    // ============================================================
    private void initPlayer() {
        if (player != null) {
            return;
        }
        player = new ExoPlayer.Builder(requireContext())
                .build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {

                switch (playbackState) {

                    case Player.STATE_IDLE:
                        if (currentStation != null) {
                            showStatus("等待播放");
                        }
                        break;
                    case Player.STATE_BUFFERING:
                        showStatus("正在緩衝…");
                        break;
                    case Player.STATE_READY:
                        if (player.isPlaying()) {
                            showStatus("正在播放");
                        } else {
                            showStatus("已暫停");
                        }
                        updatePlayButton();
                        break;
                    case Player.STATE_ENDED:
                        showStatus("播放結束");
                        updatePlayButton();
                        break;
                }
            }
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                updatePlayButton();
                if (isPlaying) {
                    showStatus("正在播放");
                } else if (player != null
                        && player.getPlaybackState()
                        == Player.STATE_READY) {
                    showStatus("已暫停");
                }
            }
            @Override
            public void onPlayerError(
                    @NonNull PlaybackException error) {
                userRequestedPlay = false;
                showError(
                        "播放失敗：" + getErrorMessage(error)
                );
                updatePlayButton();
            }
        });
    }
    // ============================================================
    // Play station
    // ============================================================
    private void playStation(RadioStation station) {
        if (player == null) {
            initPlayer();
        }
        currentStation = station;
        //
        global_params.m_live_name = station.getName();
        global_params.m_live_region = station.getRegion();
        global_params.m_live_language = station.getLanguage();
        global_params.getM_live_url = station.getUrl();
        utility.hard_save_current_setting(requireContext());
        //
        userRequestedPlay = true;
        tvStation.setText(
                station.getName()
        );
        tvRegion.setText(
                station.getRegion()
        );
        showStatus("準備連線…");
        btnReconnect.setVisibility(View.GONE);
        adapter.setSelectedStation(station);
        MediaItem.Builder builder = new MediaItem.Builder()
                        .setUri(station.getUrl());
        String mimeType = station.getMimeType();
        if (mimeType != null && !mimeType.isEmpty()) {
            builder.setMimeType(mimeType);
        }
        MediaItem mediaItem = builder.build();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        utility.SetScreenAlwaysOn(requireContext(), true);
    }
    // ============================================================
    // Reconnect
    // ============================================================
    private void reconnect() {
        if (currentStation == null) {
            return;
        }
        showStatus("重新連線…");
        btnReconnect.setVisibility(View.GONE);
        userRequestedPlay = true;
        player.stop();
        MediaItem.Builder builder =
                new MediaItem.Builder()
                        .setUri(currentStation.getUrl());
        String mimeType =
                currentStation.getMimeType();
        if (mimeType != null
                && !mimeType.isEmpty()) {
            builder.setMimeType(mimeType);
        }
        player.setMediaItem(builder.build());
        player.prepare();
        player.play();
        utility.SetScreenAlwaysOn(requireContext(), true);
    }
    // ============================================================
    // Error
    // ============================================================
    private void showError(String message) {
        tvStatus.setText(message);
        btnReconnect.setVisibility(View.VISIBLE);
    }
    private String getErrorMessage(
            PlaybackException error) {
        if (error.errorCode
                == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED) {
            return "網路連線失敗";
        } else if (error.errorCode
                == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT) {
            return "網路連線逾時";
        } else if (error.errorCode
                == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) {
            return "伺服器回應錯誤";
        } else if (error.errorCode
                == PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED) {
            return "音訊格式錯誤";
        }
        return error.getMessage() != null
                ? error.getMessage()
                : "無法播放此電台";
    }
    // ============================================================
    // Status
    // ============================================================
    private void showStatus(String status) {
        if (tvStatus != null) {
            tvStatus.setText(status);
        }
        if (btnReconnect != null
                && !status.startsWith("播放失敗")) {
            btnReconnect.setVisibility(View.GONE);
        }
    }
    private void updatePlayButton() {
        if (btnPlay == null || player == null) {
            return;
        }
        if (player.isPlaying()) {
            btnPlay.setImageResource(
                    android.R.drawable.ic_media_pause
            );
            btnPlay.setContentDescription("暫停");
        } else {
            btnPlay.setImageResource(
                    android.R.drawable.ic_media_play
            );
            btnPlay.setContentDescription("播放");
        }
    }
    // ============================================================
    // Station data
    // ============================================================
    private void loadStations() {
        stations.clear();
        stations.addAll(
                RadioStationData.getStations()
        );
    }
    // ============================================================
    // Fragment lifecycle
    // ============================================================
    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroyView();
    }
    @Override
    public void onPause() {
        if (player != null) {
            player.pause();
        }
        super.onPause();
    }
    // ============================================================
    // Adapter
    // ============================================================
    public static class RadioAdapter
            extends RecyclerView.Adapter<
            RadioAdapter.ViewHolder> {
        public interface OnStationClickListener {
            void onClick(RadioStation station);
        }
        private final List<RadioStation> stations;
        private final OnStationClickListener listener;
        public RadioStation selectedStation;
        public RadioAdapter(List<RadioStation> stations, OnStationClickListener listener) {
            this.stations = stations;
            this.listener = listener;
        }
        public void setSelectedStation(RadioStation station) {
            selectedStation = station;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext())
                            .inflate(
                                    R.layout.radio_item,
                                    parent,
                                    false
                            );
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RadioStation station = stations.get(position);
            holder.tvName.setText(
                    station.getName()
            );
            holder.tvRegion.setText(
                    station.getRegion()
            );
            holder.tvLanguage.setText(
                    station.getLanguage()
            );
            if (selectedStation != null && Objects.equals(station.getName(), selectedStation.getName())) {
                holder.itemView.setAlpha(1.0f);

            } else {
                holder.itemView.setAlpha(0.65f);
            }
            holder.itemView.setOnClickListener(
                    v -> listener.onClick(station)
            );
        }
        @Override
        public int getItemCount() {
            return stations.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            TextView tvRegion;
            TextView tvLanguage;
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(
                        R.id.radio_name
                );
                tvRegion = itemView.findViewById(
                        R.id.radio_region
                );
                tvLanguage = itemView.findViewById(
                        R.id.radio_language
                );
            }
        }
    }
}