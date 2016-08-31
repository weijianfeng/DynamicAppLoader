package com.wjf.dynamicapploader.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Bind(R.id.server_apk_item_iv_icon)
    ImageView mAppIcon; // 图标
    @Bind(R.id.server_apk_item_tv_name)
    TextView mAppName; // 标题
    @Bind(R.id.server_apk_item_tv_brief)
    TextView mAppBrief; // 简介
    @Bind(R.id.server_apk_item_button)
    Button mDownloadButton; // 下载

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

        mAppName.setText(serverApkItem.appName);
        mAppBrief.setText(serverApkItem.appBrief);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPlugin(mServerApkItem.appDownloadURL, mServerApkItem.appName);
            }
        });
    }

    private void downloadPlugin(String apkUrl, String appName) {
        DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //String apkUrl = "http://pluginapk-plugin.stor.sinaapp.com/app-debug.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
        long downloadId = downloadManager.enqueue(request);
    }
}
