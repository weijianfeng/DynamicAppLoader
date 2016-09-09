package com.wjf.pluginapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wjf.dynamicapploader.ILocalDataManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyTestActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MSG_FROM_PLUGIN = 1001;
    private static final int MSG_FROM_HOST = 1002;

    @Bind(R.id.bt_sp)
    Button bt_sp;
    @Bind(R.id.bt_aidl)
    Button bt_aidl;
    @Bind(R.id.bt_messenger)
    Button bt_messenger;

    @Bind(R.id.tv_bundle)
    TextView tv_bundle;
    @Bind(R.id.tv_sp)
    TextView tv_sp;
    @Bind(R.id.tv_aidl)
    TextView tv_aidl;
    @Bind(R.id.tv_messenger)
    TextView tv_messenger;

    private Messenger mMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int pid = android.os.Process.myPid();
        Log.i("DynamicAppLoader", "Plugin App pid is: " + pid);

        initView();
        initData();
    }

    private ServiceConnection mAIDLConnection = new ServiceConnection() {
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

    private ServiceConnection mMessengerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            Message msg = Message.obtain(null, MSG_FROM_PLUGIN);
            msg.replyTo = mReplyMessenger;
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Messenger mReplyMessenger = new Messenger(new MessengerHandler());

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_HOST:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        String str = bundle.getString("reply");
                        tv_messenger.setText(str);
                        //Toast.makeText(MyTestActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void initView() {
//        tv_bundle = (TextView)findViewById(R.id.tv_bundle);
//        tv_sp = (TextView)findViewById(R.id.tv_sp);
//        tv_aidl = (TextView)findViewById(R.id.tv_aidl);
//        tv_messenger = (TextView)findViewById(R.id.tv_messenger);
//
//        bt_sp = (Button)findViewById(R.id.bt_sp);
//        bt_aidl = (Button)findViewById(R.id.bt_aidl);
//        bt_messenger = (Button)findViewById(R.id.bt_messenger);

        bt_sp.setOnClickListener(this);
        bt_aidl.setOnClickListener(this);
        bt_messenger.setOnClickListener(this);
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
            case R.id.bt_messenger:
                bindMessengerSerivice();
                break;
            default:
                break;
        }
    }

    private void bindAIDLService() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.HostDataService");
        intent.setPackage("com.wjf.dynamicapploader");
        getApplicationContext().bindService(intent, mAIDLConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindMessengerSerivice() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.HostMessengerService");
        intent.setPackage("com.wjf.dynamicapploader");
        getApplicationContext().bindService(intent, mMessengerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unbindService(mAIDLConnection);
        unbindService(mMessengerConnection);
    }

}
