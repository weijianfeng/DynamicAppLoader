package com.wjf.dynamicapploader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.wjf.dynamicapploader.R;

import me.wangyuwei.particleview.ParticleView;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/31
 */
public class SplashActivity extends Activity{

    private ParticleView mParticleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mParticleView = (ParticleView)findViewById(R.id.pv);

        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        mParticleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mParticleView.startAnim();
                writeSharedPreferences();
            }
        }, 200);
    }

    // 预设sp内容
    private void writeSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("test", Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key", "Data in HostApp.");
        editor.commit();
    }
}
