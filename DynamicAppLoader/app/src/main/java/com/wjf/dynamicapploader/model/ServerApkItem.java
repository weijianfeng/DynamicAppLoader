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
    public static final int STATUS_NOT_DOWNLOAD = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECT_ERROR = 2;
    public static final int STATUS_DOWNLOADING = 3;
    public static final int STATUS_PAUSED = 4;
    public static final int STATUS_DOWNLOAD_ERROR = 5;
    public static final int STATUS_COMPLETE = 6;
    public static final int STATUS_INSTALLED = 7;

    @SerializedName("app_icon")
    public String appIconURL; // 图标

    @SerializedName("app_name")
    public String appName; // 标题

    @SerializedName("app_brief")
    public String appBrief;   // 简介

    @SerializedName("app_download_url")
    public String appDownloadURL;

    @SerializedName("app_package_name")
    public String appPackageName;

    private int progress;
    private String downloadPerSize;
    private int status;

    public ServerApkItem(String appIconURL, String appName, String appBrief, String appDownloadURL,
                         String appPackageName) {
        this.appIconURL = appIconURL;
        this.appName = appName;
        this.appBrief = appBrief;
        this.appDownloadURL = appDownloadURL;
        this.appPackageName = appPackageName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getDownloadPerSize() {
        return downloadPerSize;
    }

    public void setDownloadPerSize(String downloadPerSize) {
        this.downloadPerSize = downloadPerSize;
    }

    public String getStatusText() {
        switch (status) {
            case STATUS_NOT_DOWNLOAD:
                return "Not Download";
            case STATUS_CONNECTING:
                return "Connecting";
            case STATUS_CONNECT_ERROR:
                return "Connect Error";
            case STATUS_DOWNLOADING:
                return "Downloading";
            case STATUS_PAUSED:
                return "Pause";
            case STATUS_DOWNLOAD_ERROR:
                return "Download Error";
            case STATUS_COMPLETE:
                return "Complete";
            case STATUS_INSTALLED:
                return "Installed";
            default:
                return "Not Download";
        }
    }

    public String getButtonText() {
        switch (status) {
            case STATUS_NOT_DOWNLOAD:
                return "下载";
            case STATUS_CONNECTING:
                return "取消";
            case STATUS_CONNECT_ERROR:
                return "重试";
            case STATUS_DOWNLOADING:
                return "暂停";
            case STATUS_PAUSED:
                return "恢复下载";
            case STATUS_DOWNLOAD_ERROR:
                return "重试";
            case STATUS_COMPLETE:
                //return "Install";
                return "已下载";
            case STATUS_INSTALLED:
                return "卸载";
            default:
                return "下载";
        }
    }
}
