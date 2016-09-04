package com.wjf.pluginapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wjf.dynamicapploader.ILocalDataManager;

public class MyTestActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bt_sp;
    private Button bt_messenger;
    private Button bt_aidl;

    private TextView tv_bundle;
    private TextView tv_sp;
    private TextView tv_aidl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ILocalDataManager localDataManager = ILocalDataManager.Stub.asInterface(service);
            try {
                String str = localDataManager.getData();
                tv_aidl.setText(str);
//                Toast.makeText(MyTestActivity.this, str, Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initView() {
        tv_bundle = (TextView)findViewById(R.id.tv_bundle);
        tv_sp = (TextView)findViewById(R.id.tv_sp);
        tv_aidl = (TextView)findViewById(R.id.tv_aidl);

        bt_sp = (Button)findViewById(R.id.bt_sp);
        bt_aidl = (Button)findViewById(R.id.bt_aidl);

        bt_sp.setOnClickListener(this);
        bt_aidl.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                tv_bundle.setText(bundle.getString("host"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sp :
                try {
                    Context otherAppsContext = createPackageContext("com.wjf.dynamicapploader", Context.CONTEXT_IGNORE_SECURITY);
                    SharedPreferences sharedPreferences = otherAppsContext.getSharedPreferences("test", Context.MODE_WORLD_READABLE);
                    if (sharedPreferences != null) {
                        String str = sharedPreferences.getString("key",null);
                        tv_sp.setText(str);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_aidl :
                bindAIDLService();
                break;
            default:
                break;
        }
    }

    private void bindAIDLService() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.HostDataService");
        intent.setPackage("com.wjf.dynamicapploader");
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

}
