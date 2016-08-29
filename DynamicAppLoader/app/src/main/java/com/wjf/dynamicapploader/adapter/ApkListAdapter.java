package com.wjf.dynamicapploader.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.model.ApkItem;

import java.util.ArrayList;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class ApkListAdapter extends RecyclerView.Adapter<ApkItemViewHolder> {

    private ArrayList<ApkItem> mApkItems;
    private Activity mActivity;
    private int mType; // 类型

    public ApkListAdapter(Activity activity, int type) {
        mActivity = activity;
        mApkItems = new ArrayList<>();
        mType = type;
    }

    public void setApkItems(ArrayList<ApkItem> apkItems) {
        mApkItems = apkItems;
        notifyDataSetChanged();
    }

    public void addApkItem(ApkItem apkItem) {
        mApkItems.add(apkItem);
        notifyItemInserted(mApkItems.size() + 1);
    }

    public void removeApkItem(ApkItem apkItem) {
        mApkItems.remove(apkItem);
        notifyDataSetChanged();
    }

    public ApkItem getApkItem(int index) {
        return mApkItems.get(index);
    }

    public int getCount() {
        return mApkItems.size();
    }

    @Override public ApkItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apk, parent, false);
        return new ApkItemViewHolder(mActivity, view, mType, new ApkOperator.RemoveCallback() {
            @Override
            public void removeItem(ApkItem apkItem) {
                removeApkItem(apkItem);
            }
        });
    }

    @Override public void onBindViewHolder(ApkItemViewHolder holder, int position) {
        holder.bindTo(mApkItems.get(position));
    }

    @Override public int getItemCount() {
        return mApkItems.size();
    }
}
