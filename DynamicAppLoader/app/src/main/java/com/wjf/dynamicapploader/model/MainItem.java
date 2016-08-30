package com.wjf.dynamicapploader.model;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class MainItem {

    public Drawable itemIcon;
    public String itemText;
    public boolean isCanbeDeleted;

    public PackageInfo packageInfo; // 包信息

    public MainItem(Drawable itemIcon, String itemText, boolean isCanbeDeleted) {
        this.itemIcon = itemIcon;
        this.itemText = itemText;
        this.isCanbeDeleted = isCanbeDeleted;
        this.packageInfo = null;
    }

    public MainItem(Drawable itemIcon, String itemText, boolean isCanbeDeleted, PackageInfo packageInfo) {
        this.itemIcon = itemIcon;
        this.itemText = itemText;
        this.isCanbeDeleted = isCanbeDeleted;
        this.packageInfo = packageInfo;
    }
}
