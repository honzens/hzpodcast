package com.honzens.hzpodcast.ui.channel;

import static com.honzens.hzpodcast.common.utility.hard_save_current_setting;

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
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ChannelFragment extends Fragment {
    private FragmentChannelBinding binding;
    FeedAdapter m_adapter;
    private Handler m_handler_callback;
    private ChannelViewModel m_channelViewModel;
    public ChannelFragment() {
        super();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString("screen", "AtomFragment");
            MainActivity.mFirebaseAnalytics.logEvent("open_screen", bundle);
        }
        Context ctx = requireContext();
        ((MainActivity) ctx).setActionBarText(getString(R.string.title_channel));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
        //binding.recyclerView.setBackgroundColor(Color.BLACK);
        if (m_channelViewModel == null)
            m_channelViewModel = new ViewModelProvider(this).get(com.honzens.hzpodcast.ui.channel.ChannelViewModel.class);
        if (m_adapter == null) {
            if (m_handler_callback == null) {
                m_handler_callback = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(@NonNull Message msg) {
                        switch (msg.what) {
                            case 1:
                                break;
                            case 2:
                                break;
                        }
                    }
                };
            }
            m_adapter = new FeedAdapter(ctx, m_handler_callback);
            binding.recyclerView.setAdapter(m_adapter);
        }
        //
        //        //WebSettings settings = binding.webView.getSettings();
        //        //settings.setJavaScriptEnabled(true);
        //        binding.webView.setWebViewClient(new WebViewClient() {
        //            @Override
        //            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        //                return true;
        //            }
        //            @Override
        //            public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //                return true;  // 舊版 Android
        //            }
        //        });
        //        //webView.loadUrl("https://www.google.com");
        //===========
        m_channelViewModel.getFeeds().observe(
                getViewLifecycleOwner(),
                data_items -> {
                    m_adapter.setData(data_items);
                    binding.recyclerView.post(() ->
                            binding.recyclerView.scrollToPosition(0)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
