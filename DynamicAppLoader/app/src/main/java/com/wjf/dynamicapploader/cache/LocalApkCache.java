package com.wjf.dynamicapploader.cache;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wjf.dynamicapploader.MyApplicaiton;
import com.wjf.dynamicapploader.model.ServerApkItem;

import java.lang.reflect.Type;
import java.util.List;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/9/8
 */
public class LocalApkCache {

    private static final String APKLIST = "apklist";

    public static void saveApkList(String jsonStr) {
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(MyApplicaiton.getAppContext());
        sharedPreferences.edit()
                .putString(APKLIST,jsonStr)
                .commit();
    }


    public static List<ServerApkItem> getApkList() {
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(MyApplicaiton.getAppContext());
        String jsonStr = sharedPreferences.getString(APKLIST, null);
        if (jsonStr == null) {
            return null;
        } else {
            Type type = new TypeToken<List<ServerApkItem>>(){}.getType();
            List<ServerApkItem> list = new Gson().fromJson(jsonStr, type);
            return list;
        }
    }

    public static String getAppIconByPackageName(String packageName) {
        List<ServerApkItem> list = getApkList();
        if (list == null) {
            return null;
        } else {
            for (ServerApkItem item : list) {
                if (item.appPackageName.equals(packageName)) {
                    return item.appIconURL;
                }
            }
            return null;
        }
    }

    public static void clear() {
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(MyApplicaiton.getAppContext());
        sharedPreferences.edit().clear().commit();
    }
}
