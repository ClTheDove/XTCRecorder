package xds.fishy.recorder.view.activity.list.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import xds.fishy.recorder.R;
import xds.fishy.util.file.FileUtil;

public class FileListAdapter extends android.widget.BaseAdapter {
    public ArrayList<String> data;
    ViewHolder holder;
    private Context context;

    public FileListAdapter(Context context, String path, String[] extFilter, boolean allowDirectory) {
        this.context = context;
        data = FileUtil.getFilenameListArrayListSortedByModifyTimeByPath(path, extFilter, allowDirectory);
    }

    @Override
    public int getCount() {
        if (data == null) return 0;
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_file_list, null);
            holder = new ViewHolder();
            holder.tv_file_name = view.findViewById(R.id.tv_file_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_file_name.setText(data.get(i));
        return view;
    }

    private static class ViewHolder {
        TextView tv_file_name;
    }
}
