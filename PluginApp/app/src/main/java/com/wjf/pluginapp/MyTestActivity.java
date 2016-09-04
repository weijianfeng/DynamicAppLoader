package com.wjf.pluginapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MyTestActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bt_sp;
    private Button bt_messenger;
    private Button bt_aidl;

    private TextView tv_bundle;
    private TextView tv_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

    }

    private void initView() {
        tv_bundle = (TextView)findViewById(R.id.tv_bundle);
        tv_sp = (TextView)findViewById(R.id.tv_sp);

        bt_sp = (Button)findViewById(R.id.bt_sp);

        bt_sp.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
