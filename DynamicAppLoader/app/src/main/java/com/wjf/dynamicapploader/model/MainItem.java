package com.wjf.dynamicapploader.model;

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

    public MainItem(Drawable itemIcon, String itemText, boolean isCanbeDeleted) {
        this.itemIcon = itemIcon;
        this.itemText = itemText;
        this.isCanbeDeleted = isCanbeDeleted;
    }

}
