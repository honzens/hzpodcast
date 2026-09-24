package com.honzens.hzpodcast.ui.home;

import static com.honzens.hzpodcast.common.utility.hard_save_current_setting;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.honzens.hzpodcast.MainActivity;
import com.honzens.hzpodcast.R;
import com.honzens.hzpodcast.common.FeedCache;
import com.honzens.hzpodcast.databinding.FragmentHomeBinding;
import com.honzens.hzpodcast.global_params;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    //private PodcastAdapter m_adapter;
    private Handler m_handler_callback;
    private HomeViewModel m_homeViewModel;
    public HomeFragment() {
        super();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString("screen", "StockFragment");
            MainActivity.mFirebaseAnalytics.logEvent("open_screen", bundle);
        }
        Context ctx = requireContext();
        ((MainActivity) ctx).setActionBarText(getString(R.string.title_home));
        Button btnSearch = binding.searchButton;
        //RecyclerView recycler = binding.recyclerView;
        if (m_homeViewModel == null)
            m_homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        if (m_handler_callback == null) {
            m_handler_callback = new Handler(Looper.getMainLooper()) {
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case 1:
                            // 收到 message 1
                            //加入最愛
                            FeedCache.saveFavorMap(requireContext());
                            break;
                        case 2:
                            // 收到 message 2
                            //填入keyword
                            binding.keywordEdit.setText((String)msg.obj);
                            break;
                    }
                }
            };
        }
        PodcastAdapter adapter = new PodcastAdapter(ctx, m_handler_callback);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
        //binding.recyclerView.setBackgroundColor(Color.BLACK);
        //
        KeywordAdapter adapter1 = new KeywordAdapter(ctx, m_handler_callback);
        adapter1.setData(global_params.m_code_last);
        binding.recycleKeyword.setLayoutManager(new LinearLayoutManager(ctx));
        binding.recycleKeyword.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
        binding.recycleKeyword.setAdapter(adapter1);
        //
        m_homeViewModel.getFeeds().observe(
                getViewLifecycleOwner(),
                data_items -> {
                    adapter.setData(data_items);
                    btnSearch.setEnabled(true);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.resultText.setText("找到 " + data_items.size() + " 個播客");
                    binding.recyclerView.post(() ->
                            binding.recyclerView.scrollToPosition(0)
                    );
                });
        TextInputEditText keyEdit = binding.keywordEdit;
        keyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String code = charSequence.toString().trim();
                if (code.isEmpty()) {
                    binding.recycleKeyword.setVisibility(View.VISIBLE);
                }
                else {
                    binding.recycleKeyword.setVisibility(View.INVISIBLE);
                }
            }
        });
        MaterialButton searchButton = binding.searchButton;
        searchButton.setOnClickListener(v -> {
            String code = "";
            if (keyEdit.getText() != null) {
                code = keyEdit.getText()
                        .toString()
                        .trim();
            }
            if (!code.isEmpty()) {
                global_params.m_code_last = code;
                hard_save_current_setting(ctx);
                hideKeyboard(keyEdit);
                m_homeViewModel.searchPodcasts(code, "TW",1000, ctx);
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        });
    }
    private void hideKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
