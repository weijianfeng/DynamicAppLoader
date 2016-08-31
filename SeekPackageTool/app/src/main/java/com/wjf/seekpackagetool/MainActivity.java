package com.wjf.seekpackagetool;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PACKAGE_NAME = "com.wjf.pluginapp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekAppInstalled();
    }


    private void seekAppInstalled(){

        // 获取系统内的所有程序信息
        Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
        mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<PackageInfo> packageinfo = this.getPackageManager().getInstalledPackages(0);

        int count = packageinfo.size();
        for(int i=0; i<count; i++){

            PackageInfo pinfo = packageinfo.get(i);
            ApplicationInfo appInfo = pinfo.applicationInfo;
            if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)
            {
                //系统程序 忽略
            }else{
                //非系统程序
                if (appInfo.packageName.equals(PACKAGE_NAME)) {
                    Toast.makeText(MainActivity.this, "find it", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        Toast.makeText(MainActivity.this, "cann't find it", Toast.LENGTH_SHORT).show();
    }
}
