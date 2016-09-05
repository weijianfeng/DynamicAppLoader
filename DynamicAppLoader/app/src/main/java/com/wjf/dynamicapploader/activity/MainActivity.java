package com.wjf.dynamicapploader.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.wjf.dynamicapploader.ILocalDataManager;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.MainItemAdapter;
import com.wjf.dynamicapploader.model.ApkItem;
import com.wjf.dynamicapploader.model.MainItem;
import com.wjf.dynamicapploader.service.HostDataService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
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

//                    if (PluginManager.getInstance().isConnected()) {
//                        Intent i = new Intent("com.wjf.plugin.action.main");
//                        startActivity(i);
//                    }

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
//        final String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pluginapp.apk";
//        PluginManager.getInstance().addServiceConnection(new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                int result = 0;
//                try {
//                    result = PluginManager.getInstance().installPackage(filepath, 0);
//                } catch (RemoteException e) {
//                    Log.i("WJFPlugin", "result " + e.getMessage());
//                    e.printStackTrace();
//                }
//                Log.i("WJFPlugin", "result " + result);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//
//            }
//        });

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
                .subscribe(new Observer<ArrayList<ApkItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<ApkItem> apkItems) {
                        for (ApkItem apk : apkItems) {
                            mainWorkAdapter.addMainItem(new MainItem(apk.icon, apk.title.toString(), true, apk.packageInfo));
                        }
                    }
                });
    }

    // 在安装中获取Apk
    private ArrayList<ApkItem> getApkFromInstall() {
        ArrayList<ApkItem> apkItems = new ArrayList<>();
        try {
            final List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(0);
            if (infos == null) {
                return apkItems;
            }
            final PackageManager pm = MainActivity.this.getPackageManager();
            // noinspection all
            for (final PackageInfo info : infos) {
                apkItems.add(new ApkItem(pm, info, info.applicationInfo.publicSourceDir));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return apkItems;
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
                    ApkItem apk = new ApkItem(pm, info, info.applicationInfo.publicSourceDir);
                    mainWorkAdapter.addMainItem(new MainItem(apk.icon, apk.title.toString(), true, apk.packageInfo));
                    //mApkListAdapter.addApkItem(new ApkItem(pm, info, info.applicationInfo.publicSourceDir));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (PluginManager.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
                String pkg = intent.getData().getAuthority();
                //int num = mApkListAdapter.getCount();
                int num = mainWorkAdapter.getCount();
//                ApkItem removedItem = null;
                MainItem removedItem = null;
                for (int i = 0; i < num; i++) {
                    //ApkItem item = mApkListAdapter.getApkItem(i);
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
