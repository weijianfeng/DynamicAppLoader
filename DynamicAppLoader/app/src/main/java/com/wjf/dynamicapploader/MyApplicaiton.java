package com.wjf.dynamicapploader;

import android.app.Application;
import android.content.Context;

import com.morgoo.droidplugin.PluginHelper;
import com.morgoo.droidplugin.pm.PluginManager;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class MyApplicaiton extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }
}
