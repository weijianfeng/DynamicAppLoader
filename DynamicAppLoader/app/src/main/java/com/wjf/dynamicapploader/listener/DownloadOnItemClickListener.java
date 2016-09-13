package com.wjf.dynamicapploader.listener;

import android.view.View;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/9/13
 */
public interface DownloadOnItemClickListener<T> {
    void onItemClick(View v, int position, T t);
}
