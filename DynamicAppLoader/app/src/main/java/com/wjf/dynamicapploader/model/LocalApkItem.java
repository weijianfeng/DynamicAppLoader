package com.wjf.dynamicapploader.model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class LocalApkItem {

    public Drawable icon; // 图标
    public CharSequence name; // 标题
    public String versionName; // 版本名称
    public int versionCode; // 版本号
    public String apkFile; // Apk路径
    public PackageInfo packageInfo; // 包信息

    public LocalApkItem(PackageManager pm, PackageInfo pi, String path) {

        // 必须设置, 否则title无法获取
        pi.applicationInfo.sourceDir = path;
        pi.applicationInfo.publicSourceDir = path;

        try {
            icon = pm.getApplicationIcon(pi.applicationInfo);
        } catch (Exception e) {
            icon = pm.getDefaultActivityIcon();
        }
        try {
            name = pm.getApplicationLabel(pi.applicationInfo);
        } catch (Exception e) {
            name = pi.packageName;
        }
        versionName = pi.versionName;
        versionCode = pi.versionCode;
        apkFile = path;
        packageInfo = pi;
    }
}
