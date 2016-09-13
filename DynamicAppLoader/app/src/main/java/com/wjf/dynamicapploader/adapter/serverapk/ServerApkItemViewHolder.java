package com.wjf.dynamicapploader.adapter.serverapk;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.multithreaddownload.DownloadRequest;
import com.squareup.picasso.Picasso;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.model.ServerApkItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/31
 */
public class ServerApkItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.iv_icon)
    public ImageView mAppIcon; // 图标
    @Bind(R.id.tv_name)
    public TextView mAppName; // 标题
//    @Bind(R.id.server_apk_item_tv_brief)
//    TextView mAppBrief; // 简介
    @Bind(R.id.bt_download)
    public Button mDownloadButton; // 下载

    @Bind(R.id.tv_downloadPerSize)
    public TextView mDownloadPerSize;
    @Bind(R.id.tv_status)
    public TextView mStatus;
    @Bind(R.id.progressBar)
    public ProgressBar mProgressBar;

    private Context mContext; // 上下文
    private ServerApkItem mServerApkItem;

    public ServerApkItemViewHolder(Activity activity, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = activity.getApplicationContext();
    }

    // 绑定ViewHolder
    public void bindTo(ServerApkItem serverApkItem) {
        mServerApkItem = serverApkItem;

        Picasso.with(mContext).load(mServerApkItem.appIconURL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(mAppIcon);
        mAppName.setText(serverApkItem.appName);
        //mAppBrief.setText(serverApkItem.appBrief);
        mDownloadPerSize.setText(serverApkItem.getDownloadPerSize());
        mStatus.setText(serverApkItem.getStatusText());
        mDownloadButton.setText(serverApkItem.getButtonText());
        mProgressBar.setProgress(serverApkItem.getProgress());

//        mDownloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downloadPlugin(mServerApkItem.appDownloadURL, mServerApkItem.appName);
//            }
//        });
    }

    private void downloadPlugin(String apkUrl, String appName) {
        DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //String apkUrl = "http://pluginapk-plugin.stor.sinaapp.com/app-debug.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
        long downloadId = downloadManager.enqueue(request);
    }
}
