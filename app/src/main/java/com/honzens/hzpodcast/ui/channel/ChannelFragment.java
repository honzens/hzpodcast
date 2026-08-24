package com.honzens.hzpodcast.ui.channel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.honzens.hzpodcast.MainActivity;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.common.FeedCache;
import com.honzens.hzpodcast.databinding.FragmentChannelBinding;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ChannelFragment extends Fragment {
    private FragmentChannelBinding binding;
    private FavorFeedAdapter m_favor_adapter;
    private ProgramAdapter m_program_adapter;
    private Handler m_handler_callback;
    private ChannelViewModel m_channelViewModel;
    public ChannelFragment() {
        super();
    }
    @Override
    public void onResume() {
        super.onResume();
        m_favor_adapter.setData(FeedCache.getFavorList());
    }
    public void onPause() {
        super.onPause();
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
        ((MainActivity) ctx).setActionBarText(getString(R.string.title_channel));
        binding.btnBack.setOnClickListener(v -> switch_to_favor());
        binding.recyclerViewChannels.setLayoutManager(new LinearLayoutManager(ctx));
        binding.recyclerViewChannels.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
        //binding.recyclerView.setBackgroundColor(Color.BLACK);
        if (m_channelViewModel == null)
            m_channelViewModel = new ViewModelProvider(this).get(com.honzens.hzpodcast.ui.channel.ChannelViewModel.class);
        if (m_favor_adapter == null) {
            if (m_handler_callback == null) {
                m_handler_callback = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(@NonNull Message msg) {
                        switch (msg.what) {
                            case 1:
                                String url = (String) msg.obj;
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
            m_program_adapter = new ProgramAdapter(ctx, m_handler_callback);
            binding.recyclerViewPrograms.setAdapter(m_program_adapter);
        }
        //===========
        m_channelViewModel.getFeeds().observe(
                getViewLifecycleOwner(),
                data_items -> {
                    m_program_adapter.setData(data_items);
                    switch_to_program();
                    binding.recyclerViewPrograms.post(() ->
                            binding.recyclerViewPrograms.scrollToPosition(0)
                    );
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ChannelViewModel atomViewModel =
                new ViewModelProvider(this).get(com.honzens.hzpodcast.ui.channel.ChannelViewModel.class);

        binding = FragmentChannelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
    private void switch_to_program() {
        binding.recyclerViewPrograms.setVisibility(View.VISIBLE);
        binding.recyclerViewChannels.setVisibility(View.GONE);
        binding.btnBack.setVisibility(View.VISIBLE);
    }
    private void switch_to_favor() {
        binding.recyclerViewPrograms.setVisibility(View.GONE);
        binding.recyclerViewChannels.setVisibility(View.VISIBLE);
        binding.btnBack.setVisibility(View.GONE);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
