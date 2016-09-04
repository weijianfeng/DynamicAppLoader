package com.wjf.dynamicapploader.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.wjf.dynamicapploader.R;
import com.wjf.dynamicapploader.model.MainItem;

import java.util.List;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/8/29
 */
public class MainItemAdapter extends BaseAdapter {

    private Context context;
    private List<MainItem> mainItems;
    private boolean isDeleteIconVisible = false;

    public MainItemAdapter(Context context, List<MainItem> mainItems) {
        this.context = context;
        this.mainItems = mainItems;
    }

    @Override
    public int getCount() {
        return mainItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mainItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addMainItem(MainItem mainItem) {
        int index = getCount();
        mainItems.add(index -1, mainItem);
        notifyDataSetChanged();
    }

    public void removeMainItem(MainItem mainItem) {
        mainItems.remove(mainItem);
        notifyDataSetChanged();
    }

    public void setDeleteIconVisible() {
        this.isDeleteIconVisible = true;
        notifyDataSetChanged();
    }

    public void setDeleteIconInvisible() {
        this.isDeleteIconVisible = false;
        notifyDataSetChanged();
    }

    public boolean isDeleteIconVisible() {
        return this.isDeleteIconVisible;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        }

        ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_main_icon);
        TextView appTitle = (TextView)convertView.findViewById(R.id.item_main_text);
        ImageView deleteView = (ImageView)convertView.findViewById(R.id.delete_markView);

        final MainItem mainItem = mainItems.get(position);
        appIcon.setImageDrawable(mainItem.itemIcon);
        appTitle.setText(mainItem.itemText);

        if (isDeleteIconVisible && mainItem.isCanbeDeleted) {
            deleteView.setVisibility(View.VISIBLE);
        } else {
            deleteView.setVisibility(View.GONE);
        }

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PluginManager.getInstance().isConnected()) {
                    try {
                        PluginManager.getInstance().deletePackage(mainItem.packageInfo.packageName, 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    setDeleteIconInvisible();
                } else {
                    Toast.makeText(context, "卸载插件失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}
