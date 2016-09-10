package com.wjf.dynamicapploader.adapter.localapk;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.ApkOperator;
import com.wjf.dynamicapploader.cache.LocalApkCache;
import com.wjf.dynamicapploader.model.LocalApkItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class LocalApkItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.apk_item_iv_icon)
    ImageView mAppIcon; // 图标
    @Bind(R.id.apk_item_tv_title)
    TextView mAppTitle; // 标题
    @Bind(R.id.apk_item_tv_version)
    TextView mAppVersion; // 版本号
    @Bind(R.id.apk_item_button)
    Button mDownloadButton; // 下载
    @Bind(R.id.apk_item_delete)
    Button mDeleteButton;   // 删除

    private LocalApkItem mLocalApkItem; // Apk项
    private Context mContext; // 上下文
    private ApkOperator mApkOperator; // Apk操作

    /**
     * 初始化ViewHolder
     *
     * @param activity Dialog绑定Activity
     * @param itemView 项视图
     * @param callback 删除Item的调用
     */
    public LocalApkItemViewHolder(Activity activity, View itemView
            , ApkOperator.RemoveCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = activity.getApplicationContext();
        mApkOperator = new ApkOperator(activity, callback); // Apk操作
    }

    // 绑定ViewHolder
    public void bindTo(LocalApkItem localApkItem) {
        mLocalApkItem = localApkItem;

        //mAppIcon.setImageDrawable(localApkItem.icon);
        Picasso.with(mContext).
                load(LocalApkCache.getAppIconByPackageName(localApkItem.packageInfo.packageName))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(mAppIcon);

        mAppTitle.setText(localApkItem.name);
        mAppVersion.setText(String.format("%s(%s)", localApkItem.versionName, localApkItem.versionCode));

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InstallApkTask().execute();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApkOperator.deleteApk(mLocalApkItem);
            }
        });
    }

    // 安装Apk的线程, Rx无法使用.
    private class InstallApkTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String v) {
            Toast.makeText(mContext, v, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return mApkOperator.installApk(mLocalApkItem);
        }
    }
}
