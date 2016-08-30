package com.wjf.dynamicapploader.fragment;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.morgoo.droidplugin.pm.PluginManager;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.ApkListAdapter;
import com.wjf.dynamicapploader.adapter.ApkOperator;
import com.wjf.dynamicapploader.model.ApkItem;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/30
 */
public class LocalApkFragment extends Fragment{


    @Bind(R.id.list_rv_recycler)
    RecyclerView mRvRecycler;

    private ApkListAdapter mStoreAdapter; // 适配器


    // 服务连接
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            loadApks();
        }

        @Override public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_plugin, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initPlugin();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
//        PluginManager.getInstance().removeServiceConnection(mServiceConnection);
    }

    private void initView() {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRecycler.setLayoutManager(llm);
        mStoreAdapter = new ApkListAdapter(getActivity(), ApkOperator.TYPE_STORE);
        mRvRecycler.setAdapter(mStoreAdapter);
    }

    private void initPlugin() {
        if (PluginManager.getInstance().isConnected()) {
            loadApks();
        } else {
            PluginManager.getInstance().addServiceConnection(mServiceConnection);
        }
    }

    // 加载Apk
    private void loadApks() {
        // 异步加载, 防止Apk过多, 影响速度
        Observable.just(getApkFromDownload())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ApkItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<ApkItem> apkItems) {
                        mStoreAdapter.setApkItems(apkItems);
                    }
                });
    }

    // 从下载文件夹获取Apk
    private ArrayList<ApkItem> getApkFromDownload() {
        File files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        PackageManager pm = getActivity().getPackageManager();
        ArrayList<ApkItem> apkItems = new ArrayList<>();
        for (File file : files.listFiles()) {
            if (file.exists() && file.getPath().toLowerCase().endsWith(".apk")) {
                final PackageInfo info = pm.getPackageArchiveInfo(file.getPath(), 0);
                apkItems.add(new ApkItem(pm, info, file.getPath()));
            }
        }
        return apkItems;
    }
}
