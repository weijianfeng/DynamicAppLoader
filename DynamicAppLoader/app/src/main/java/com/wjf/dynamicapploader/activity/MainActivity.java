package com.wjf.dynamicapploader.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.morgoo.droidplugin.pm.PluginManager;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.MainItemAdapter;
import com.wjf.dynamicapploader.model.MainItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridView workGrid;
    private MainItemAdapter mainWorkAdapter;
    private List<MainItem> mainWorkItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initPlugin();
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
                if (position == 5) {

//                    if (PluginManager.getInstance().isConnected()) {
//                        Intent i = new Intent("com.wjf.plugin.action.main");
//                        startActivity(i);
//                    }

                    Intent i = new Intent(MainActivity.this, PluginStoreActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private void initPlugin() {
        PluginManager.getInstance().init(getApplication());
        final String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pluginapp.apk";
        PluginManager.getInstance().addServiceConnection(new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                int result = 0;
                try {
                    result = PluginManager.getInstance().installPackage(filepath, 0);
                } catch (RemoteException e) {
                    Log.i("WJFPlugin", "result " + e.getMessage());
                    e.printStackTrace();
                }
                Log.i("WJFPlugin", "result " + result);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
    }
}
