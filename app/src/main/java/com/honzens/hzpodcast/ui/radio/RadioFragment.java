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
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.honzens.hzpodcast.R;

import java.util.ArrayList;
import java.util.List;

public class RadioFragment extends Fragment {

    private ExoPlayer player;

    private TextView tvStation;
    private TextView tvFrequency;
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

        tvStation = view.findViewById(R.id.tv_station);
        tvFrequency = view.findViewById(R.id.tv_frequency);
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

        recyclerView.setAdapter(adapter);

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

            } else {

                userRequestedPlay = true;

                if (player.getPlaybackState()
                        == Player.STATE_IDLE) {

                    reconnect();

                } else {

                    player.play();
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

        userRequestedPlay = true;

        tvStation.setText(
                station.getName()
        );

        tvFrequency.setText(
                station.getFrequency()
        );

        showStatus("準備連線…");

        btnReconnect.setVisibility(View.GONE);

        adapter.setSelectedStation(station);

        MediaItem.Builder builder =
                new MediaItem.Builder()
                        .setUri(station.getUrl());

        String mimeType = station.getMimeType();

        if (mimeType != null
                && !mimeType.isEmpty()) {

            builder.setMimeType(mimeType);
        }

        MediaItem mediaItem = builder.build();

        player.setMediaItem(mediaItem);

        player.prepare();

        player.play();
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

        private RadioStation selectedStation;

        public RadioAdapter(
                List<RadioStation> stations,
                OnStationClickListener listener) {

            this.stations = stations;
            this.listener = listener;
        }

        public void setSelectedStation(
                RadioStation station) {

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
        public void onBindViewHolder(
                @NonNull ViewHolder holder,
                int position) {

            RadioStation station =
                    stations.get(position);

            holder.tvName.setText(
                    station.getName()
            );

            holder.tvFrequency.setText(
                    station.getFrequency()
            );

            if (station == selectedStation) {

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

        static class ViewHolder
                extends RecyclerView.ViewHolder {

            TextView tvName;
            TextView tvFrequency;

            ViewHolder(@NonNull View itemView) {

                super(itemView);

                tvName = itemView.findViewById(
                        R.id.radio_name
                );

                tvFrequency = itemView.findViewById(
                        R.id.radio_frequency
                );
            }
        }
    }
}