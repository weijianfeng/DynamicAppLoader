package com.wjf.dynamicapploader.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.morgoo.droidplugin.pm.PluginManager;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.MainItemAdapter;
import com.wjf.dynamicapploader.model.LocalApkItem;
import com.wjf.dynamicapploader.model.MainItem;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private GridView workGrid;
    private MainItemAdapter mainWorkAdapter;
    private List<MainItem> mainWorkItems = new ArrayList<>();
    private List<MainItem> pluginItems = new ArrayList<>();

    private InstallApkReceiver mInstallApkReceiver; // Apk安装接收器

    // 服务连接
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            loadApks();
        }

        @Override public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int pid = android.os.Process.myPid();
        Log.i("DynamicAppLoader", "Host App pid is: " + pid);

        initPlugin();
        initView();
    }

    private void initView() {
        workGrid = (GridView)findViewById(R.id.main_work_grid);

        mainWorkItems.add(new MainItem(getDrawable(R.mipmap.main_leave), "请假", false));
        mainWorkItems.add(new MainItem(getDrawable(R.mipmap.main_approval), "审批", false));
        mainWorkItems.add(new MainItem(getDrawable(R.mipmap.main_salary), "工资单", false));
        mainWorkItems.add(new MainItem(getDrawable(R.mipmap.main_performance), "绩效考核", false));
        mainWorkItems.add(new MainItem(getDrawable(R.mipmap.main_didi), "滴滴出行", false));
        mainWorkItems.add(new MainItem(getDrawable(R.mipmap.main_update_gray), "更多应用", false));

        mainWorkAdapter = new MainItemAdapter(MainActivity.this, mainWorkItems);
        workGrid.setAdapter(mainWorkAdapter);

        workGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainWorkAdapter.setDeleteIconInvisible();
                if (position == mainWorkItems.size() - 1) {
                    Intent i = new Intent(MainActivity.this, PluginStoreActivity.class);
                    startActivity(i);
                }

                if (mainWorkItems.get(position).packageInfo != null) {
                    String appPackageName = mainWorkItems.get(position).packageInfo.packageName;
                    switch (appPackageName) {
                        case "com.wjf.pluginapp":
                            Bundle bundle = new Bundle();
                            bundle.putString("host", "date from bundle from host app.");
                            Intent i = new Intent("com.wjf.plugin.action.main");
                            i.putExtras(bundle);
                            startActivity(i);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        workGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mainWorkAdapter.isDeleteIconVisible()) {
                    mainWorkAdapter.setDeleteIconInvisible();
                } else {
                    mainWorkAdapter.setDeleteIconVisible();
                }
                return true;
            }
        });
    }

    private void initPlugin() {
        PluginManager.getInstance().init(getApplication());

        mInstallApkReceiver = new InstallApkReceiver();

        mInstallApkReceiver.registerReceiver(this);

        if (PluginManager.getInstance().isConnected()) {
            loadApks();
        } else {
            PluginManager.getInstance().addServiceConnection(mServiceConnection);
        }
    }


    // 加载Apk
    private void loadApks() {
        // 异步加载, 防止Apk过多, 影响速度
        Observable.just(getApkFromInstall())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<LocalApkItem>>() {
                    @Override
                    public void call(ArrayList<LocalApkItem> localApkItems) {
                        for (LocalApkItem apk : localApkItems) {
                            mainWorkAdapter.addMainItem(new MainItem(apk.icon, apk.name.toString(), true, apk.packageInfo));
                        }
                    }
                });
    }

    // 在安装中获取Apk
    private ArrayList<LocalApkItem> getApkFromInstall() {
        ArrayList<LocalApkItem> localApkItems = new ArrayList<>();
        try {
            final List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(0);
            if (infos == null) {
                return localApkItems;
            }
            final PackageManager pm = MainActivity.this.getPackageManager();
            // noinspection all
            for (final PackageInfo info : infos) {
                localApkItems.add(new LocalApkItem(pm, info, info.applicationInfo.publicSourceDir));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return localApkItems;
    }

    // 安装Apk接收器
    private class InstallApkReceiver extends BroadcastReceiver {

        // 注册监听
        public void registerReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(PluginManager.ACTION_PACKAGE_ADDED);
            filter.addAction(PluginManager.ACTION_PACKAGE_REMOVED);
            filter.addDataScheme("package");
            context.registerReceiver(this, filter);
        }

        // 关闭监听
        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听添加和删除事件
            if (PluginManager.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
                try {
                    PackageManager pm = MainActivity.this.getPackageManager();
                    String pkg = intent.getData().getAuthority();
                    PackageInfo info = PluginManager.getInstance().getPackageInfo(pkg, 0);
                    LocalApkItem apk = new LocalApkItem(pm, info, info.applicationInfo.publicSourceDir);
                    mainWorkAdapter.addMainItem(new MainItem(apk.icon, apk.name.toString(), true, apk.packageInfo));
                    //mApkListAdapter.addApkItem(new LocalApkItem(pm, info, info.applicationInfo.publicSourceDir));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (PluginManager.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
                String pkg = intent.getData().getAuthority();
                //int num = mApkListAdapter.getCount();
                int num = mainWorkAdapter.getCount();
//                LocalApkItem removedItem = null;
                MainItem removedItem = null;
                for (int i = 0; i < num; i++) {
                    //LocalApkItem item = mApkListAdapter.getApkItem(i);
                    MainItem item = (MainItem)mainWorkAdapter.getItem(i);
                    if (item.packageInfo == null) {
                        continue;
                    }

                    if (TextUtils.equals(item.packageInfo.packageName, pkg)) {
                        removedItem = item;
                        break;
                    }
                }
                if (removedItem != null) {
                    //mApkListAdapter.removeApkItem(removedItem);
                    mainWorkAdapter.removeMainItem(removedItem);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstallApkReceiver.unregisterReceiver(this);
    }
}
