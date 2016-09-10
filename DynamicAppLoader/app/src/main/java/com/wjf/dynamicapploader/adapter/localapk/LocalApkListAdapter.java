package com.wjf.dynamicapploader.adapter.localapk;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.adapter.ApkOperator;
import com.wjf.dynamicapploader.model.LocalApkItem;

import java.util.ArrayList;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class LocalApkListAdapter extends RecyclerView.Adapter<LocalApkItemViewHolder> {

    private ArrayList<LocalApkItem> mLocalApkItems;
    private Activity mActivity;

    public LocalApkListAdapter(Activity activity) {
        mActivity = activity;
        mLocalApkItems = new ArrayList<>();
    }

    public void setApkItems(ArrayList<LocalApkItem> localApkItems) {
        mLocalApkItems = localApkItems;
        notifyDataSetChanged();
    }

    public void addApkItem(LocalApkItem localApkItem) {
        mLocalApkItems.add(localApkItem);
        notifyItemInserted(mLocalApkItems.size() + 1);
    }

    public void removeApkItem(LocalApkItem localApkItem) {
        mLocalApkItems.remove(localApkItem);
        notifyDataSetChanged();
    }

    public LocalApkItem getApkItem(int index) {
        return mLocalApkItems.get(index);
    }

    public int getCount() {
        return mLocalApkItems.size();
    }

    @Override public LocalApkItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apk, parent, false);
        return new LocalApkItemViewHolder(mActivity, view, new ApkOperator.RemoveCallback() {
            @Override
            public void removeItem(LocalApkItem localApkItem) {
                removeApkItem(localApkItem);
            }
        });
    }

    @Override public void onBindViewHolder(LocalApkItemViewHolder holder, int position) {
        holder.bindTo(mLocalApkItems.get(position));
    }

    @Override public int getItemCount() {
        return mLocalApkItems.size();
    }
}
