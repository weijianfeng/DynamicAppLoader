package com.wjf.dynamicapploader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.item_main_icon);
        TextView textView = (TextView)convertView.findViewById(R.id.item_main_text);

        MainItem mainItem = mainItems.get(position);

        imageView.setImageDrawable(mainItem.itemIcon);
        textView.setText(mainItem.itemText);

        return convertView;
    }
}
