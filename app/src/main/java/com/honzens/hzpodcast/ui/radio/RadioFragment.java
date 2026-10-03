package com.honzens.hzpodcast.ui.radio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.common.utility;

import java.util.ArrayList;
import java.util.List;

public class RadioFragment extends Fragment {

    private ExoPlayer player;

    private TextView tvStation;
    private TextView tvFrequency;
    private ImageButton btnPlay;

    private RadioAdapter adapter;

    private final List<RadioStation> stations = new ArrayList<>();

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
        btnPlay = view.findViewById(R.id.btn_play);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_radio);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new RadioAdapter(stations,station -> playStation(station));
        recyclerView.setAdapter(adapter);
        loadStations();

        btnPlay.setOnClickListener(v -> {

            if (player == null) {
                if (!stations.isEmpty()) {
                    playStation(stations.get(0));
                }
                return;
            }

            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }
        });

        initPlayer();

        return view;
    }

    private void initPlayer() {

        player = new ExoPlayer.Builder(requireContext())
                .build();

        player.addListener(new Player.Listener() {

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    btnPlay.setImageResource(
                            android.R.drawable.ic_media_pause
                    );
                } else {
                    btnPlay.setImageResource(
                            android.R.drawable.ic_media_play
                    );
                }
            }
        });
    }

    private void playStation(RadioStation station) {

        if (player == null) {
            initPlayer();
        }

        tvStation.setText(station.getName());
        tvFrequency.setText(station.getFrequency());
        MediaItem mediaItem = MediaItem.fromUri(station.getUrl());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        utility.SetScreenAlwaysOn(requireContext(), true);

        adapter.setSelectedStation(station);
    }

    private void loadStations() {
        stations.clear();
        stations.addAll(
                RadioStationData.getStations()
        );
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
            utility.SetScreenAlwaysOn(requireContext(), false);
        }
    }
    @Override
    public void onDestroyView() {
        if (player != null) {
            player.release();
            player = null;
            utility.SetScreenAlwaysOn(requireContext(), false);
        }
        super.onDestroyView();
    }

    // --------------------------------------------------
    // Adapter
    // --------------------------------------------------

    public static class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.ViewHolder> {
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

            View view = LayoutInflater.from(parent.getContext())
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
            RadioStation station = stations.get(position);
            holder.tvName.setText(station.getName());
            holder.tvFrequency.setText(station.getFrequency());
            if (station == selectedStation) {
                holder.itemView.setAlpha(1.0f);

            } else {
                holder.itemView.setAlpha(0.65f);
            }
            holder.itemView.setOnClickListener(v ->
                    listener.onClick(station));
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