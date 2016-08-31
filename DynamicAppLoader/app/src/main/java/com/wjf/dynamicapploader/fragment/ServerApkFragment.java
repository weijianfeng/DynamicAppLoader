package com.wjf.dynamicapploader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.serverapk.ServerApkListAdapter;
import com.wjf.dynamicapploader.model.ServerApkItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/30
 */
public class ServerApkFragment extends Fragment {

    @Bind(R.id.list_serverapk)
    RecyclerView mRvRecycler;

    private ServerApkListAdapter mServerApkListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_plugin, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRecycler.setLayoutManager(llm);
        mServerApkListAdapter = new ServerApkListAdapter(getActivity());
        mRvRecycler.setAdapter(mServerApkListAdapter);
    }

    private void initData() {
        ServerApkItem item = new ServerApkItem(null, "Plugin", "a test app",
                "http://pluginapk-plugin.stor.sinaapp.com/app-debug.apk");
        mServerApkListAdapter.addItem(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
