package com.wjf.dynamicapploader.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.FragmentAdapter;
import com.wjf.dynamicapploader.fragment.LocalApkFragment;
import com.wjf.dynamicapploader.fragment.ServerApkFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class PluginStoreActivity extends AppCompatActivity{

    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    private FragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pluginstore);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mFragmentAdapter.addFragment(new ServerApkFragment(),"所有插件");
        mFragmentAdapter.addFragment(new LocalApkFragment(), "已下载插件");
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
