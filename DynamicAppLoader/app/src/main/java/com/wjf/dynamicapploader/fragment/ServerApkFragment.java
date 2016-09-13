package com.wjf.dynamicapploader.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadInfo;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.serverapk.ServerApkItemViewHolder;
import com.wjf.dynamicapploader.adapter.serverapk.ServerApkListAdapter;
import com.wjf.dynamicapploader.cache.LocalApkCache;
import com.wjf.dynamicapploader.listener.DownloadOnItemClickListener;
import com.wjf.dynamicapploader.model.ServerApkItem;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/30
 */
public class ServerApkFragment extends Fragment implements DownloadOnItemClickListener<ServerApkItem>{
    
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
        mServerApkListAdapter.setDownloadOnItemClickListener(this);
        mRvRecycler.setAdapter(mServerApkListAdapter);
    }

    private void initData() {
        LocalApkCache.clear();
        String mock = "[{\"app_icon\":\"http://pluginapk-plugin.stor.sinaapp.com/youzancoin_icon.png\"," +
                "\"app_name\":\"有赞币\"," +
                "\"app_brief\":\"an app to appreciate others\"," +
                "\"app_download_url\":\"http://pluginapk-plugin.stor.sinaapp.com/youzancoin_1.apk\"," +
                "\"app_package_name\":\"com.wjf.pluginapp\"}]";

        LocalApkCache.saveApkList(mock);
        Type type = new TypeToken<List<ServerApkItem>>(){}.getType();
        List<ServerApkItem> list = new Gson().fromJson(mock, type);

        for(ServerApkItem item : list) {
            DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadInfo(item.appDownloadURL);
            if (downloadInfo != null) {
                item.setProgress(downloadInfo.getProgress());
                item.setDownloadPerSize(getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
                item.setStatus(item.STATUS_PAUSED);
            }
            mServerApkListAdapter.addItem(item);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        DownloadManager.getInstance().pauseAll();
    }
    
    /*************  下载相关 ************/
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private final File mDownloadDir = new File(Environment.getExternalStorageDirectory(), "Download");
    
    private String getDownloadPerSize(long finished, long total) {
        return DF.format((float) finished / (1024 * 1024)) + "M/" + DF.format((float) total / (1024 * 1024)) + "M";
    }

    @Override
    public void onItemClick(View v, int position, ServerApkItem serverApkItem) {
        if (serverApkItem.getStatus() == serverApkItem.STATUS_DOWNLOADING 
                || serverApkItem.getStatus() == serverApkItem.STATUS_CONNECTING) {
            pause(serverApkItem.appDownloadURL);
        } else if (serverApkItem.getStatus() == serverApkItem.STATUS_COMPLETE) {
            //install(serverApkItem);
        } else if (serverApkItem.getStatus() == serverApkItem.STATUS_INSTALLED) {
            //unInstall(ServerApkItem);
        } else {
            download(position, serverApkItem.appDownloadURL, serverApkItem);
        }
    }

    private void download(final int position, String tag, final ServerApkItem serverApkItem) {
        final DownloadRequest request = new DownloadRequest.Builder()
                .setName(serverApkItem.appName + ".apk")
                .setUri(serverApkItem.appDownloadURL)
                .setFolder(mDownloadDir)
                .build();

        DownloadManager.getInstance().download(request, tag, new DownloadCallback(position, serverApkItem));
    }

    private void pause(String tag) {
        DownloadManager.getInstance().pause(tag);
    }

    class DownloadCallback implements CallBack {
        private int mPosition;
        private ServerApkItem mServerApkItem;

        public DownloadCallback(int position, ServerApkItem serverApkItem) {
            mServerApkItem = serverApkItem;
            mPosition = position;
        }

        @Override
        public void onStarted() {

        }

        @Override
        public void onConnecting() {
            mServerApkItem.setStatus(ServerApkItem.STATUS_CONNECTING);
            if (isCurrentListViewItemVisible(mPosition)) {
                ServerApkItemViewHolder holder = getViewHolder(mPosition);
                holder.mStatus.setText(mServerApkItem.getStatusText());
                holder.mDownloadButton.setText(mServerApkItem.getButtonText());
            }
        }

        @Override
        public void onConnected(long total, boolean isRangeSupport) {
            mServerApkItem.setStatus(ServerApkItem.STATUS_DOWNLOADING);
            if (isCurrentListViewItemVisible(mPosition)) {
                ServerApkItemViewHolder holder = getViewHolder(mPosition);
                holder.mStatus.setText(mServerApkItem.getStatusText());
                holder.mDownloadButton.setText(mServerApkItem.getButtonText());
            }
        }

        @Override
        public void onProgress(long finished, long total, int progress) {
            String downloadPerSize = getDownloadPerSize(finished, total);
            mServerApkItem.setProgress(progress);
            mServerApkItem.setDownloadPerSize(downloadPerSize);
            mServerApkItem.setStatus(ServerApkItem.STATUS_DOWNLOADING);
            if (isCurrentListViewItemVisible(mPosition)) {
                ServerApkItemViewHolder holder = getViewHolder(mPosition);
                holder.mDownloadPerSize.setText(downloadPerSize);
                holder.mProgressBar.setProgress(progress);
                holder.mStatus.setText(mServerApkItem.getStatusText());
                holder.mDownloadButton.setText(mServerApkItem.getButtonText());
            }
        }

        @Override
        public void onCompleted() {
            mServerApkItem.setStatus(ServerApkItem.STATUS_COMPLETE);
//            File apk = new File(mDownloadDir, mServerApkItem.getName() + ".apk");
//            if (apk.isFile() && apk.exists()) {
//                String packageName = Utils.getApkFilePackage(getActivity(), apk);
//                mServerApkItem.setPackageName(packageName);
//                if (Utils.isAppInstalled(getActivity(), packageName)) {
//                    mServerApkItem.setStatus(ServerApkItem.STATUS_INSTALLED);
//                }
//            }

            if (isCurrentListViewItemVisible(mPosition)) {
                ServerApkItemViewHolder holder = getViewHolder(mPosition);
                holder.mStatus.setText(mServerApkItem.getStatusText());
                holder.mDownloadButton.setText(mServerApkItem.getButtonText());
            }
        }

        @Override
        public void onDownloadPaused() {
            mServerApkItem.setStatus(ServerApkItem.STATUS_PAUSED);
            if (isCurrentListViewItemVisible(mPosition)) {
                ServerApkItemViewHolder holder = getViewHolder(mPosition);
                holder.mStatus.setText(mServerApkItem.getStatusText());
                holder.mDownloadButton.setText(mServerApkItem.getButtonText());
            }
        }

        @Override
        public void onDownloadCanceled() {
            mServerApkItem.setStatus(ServerApkItem.STATUS_NOT_DOWNLOAD);
            mServerApkItem.setDownloadPerSize("");
            if (isCurrentListViewItemVisible(mPosition)) {
                ServerApkItemViewHolder holder = getViewHolder(mPosition);
                holder.mStatus.setText(mServerApkItem.getStatusText());
                holder.mDownloadPerSize.setText("");
                holder.mDownloadButton.setText(mServerApkItem.getButtonText());
            }
        }

        @Override
        public void onFailed(DownloadException e) {
            mServerApkItem.setStatus(ServerApkItem.STATUS_DOWNLOAD_ERROR);
            mServerApkItem.setDownloadPerSize("");
            if (isCurrentListViewItemVisible(mPosition)) {
                ServerApkItemViewHolder holder = getViewHolder(mPosition);
                holder.mStatus.setText(mServerApkItem.getStatusText());
                holder.mDownloadPerSize.setText("");
                holder.mDownloadButton.setText(mServerApkItem.getButtonText());
            }
            e.printStackTrace();
        }
    }

    private ServerApkItemViewHolder getViewHolder(int position) {
        return (ServerApkItemViewHolder) mRvRecycler.findViewHolderForLayoutPosition(position);
    }

    private boolean isCurrentListViewItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRvRecycler.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        return first <= position && position <= last;
    }
}
