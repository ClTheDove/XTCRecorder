package xds.fishy.recorder.view.activity.menu.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xds.fishy.recorder.R;
import xds.fishy.recorder.view.activity.menu.bean.MenuItem;

public class MenuItemListAdapter extends android.widget.BaseAdapter {
    public MenuItem[] data;
    ViewHolder holder;
    private Context context;

    public MenuItemListAdapter(Context context, MenuItem[] data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data == null) return 0;
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return getView(i, null, null);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_menu, null);
            holder = new ViewHolder();
            holder.tv_menu_item_name = view.findViewById(R.id.tv_menu_item_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MenuItem menuItem = data[i];
        holder.tv_menu_item_name.setText(menuItem.getStringResource());
        holder.tv_menu_item_name.setCompoundDrawablesWithIntrinsicBounds(menuItem.getDrawableResource(), 0, 0, 0);
        return view;
    }

    private static class ViewHolder {
        TextView tv_menu_item_name;
    }
}
