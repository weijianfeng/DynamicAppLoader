package com.wjf.dynamicapploader.model;

import android.test.suitebuilder.annotation.Suppress;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/31
 */
public class ServerApkItem {

    @SerializedName("app_icon")
    public String appIconURL; // 图标

    @SerializedName("app_name")
    public String appName; // 标题

    @SerializedName("app_brief")
    public String appBrief;   // 简介

    @SerializedName("app_download_url")
    public String appDownloadURL;

    public ServerApkItem(String appIconURL, String appName, String appBrief, String appDownloadURL) {
        this.appIconURL = appIconURL;
        this.appName = appName;
        this.appBrief = appBrief;
        this.appDownloadURL = appDownloadURL;
    }
}
