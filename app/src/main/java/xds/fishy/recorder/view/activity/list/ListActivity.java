package xds.fishy.recorder.view.activity.list;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import xds.fishy.recorder.R;
import xds.fishy.recorder.view.activity.list.adapter.FileListAdapter;
import xds.fishy.recorder.view.activity.play.PlayActivity;
import xds.fishy.recorder.widget.popupwindow.dialog.PopupWindowDialog;
import xds.fishy.util.file.FileUtil;
import xds.fishy.view.activity.BaseActivity;

public class ListActivity extends BaseActivity {
    private String searchPath;
    private String fileExt;
    private FileListAdapter fileListAdapter;
    private TextView tv_no_file;
    private ListView lv_file;
    private RelativeLayout rl_list;
    private PopupWindowDialog popupWindowDialog;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_list);
        initVars();
        initView();
    }

    @Override
    public void onUserLeaveHint() {
        leave();
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        leave();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        leave();
        super.onDestroy();
    }

    private void leave() {
        if (popupWindowDialog != null) popupWindowDialog.dismiss();
    }

    @Override
    public void initVars() {
        searchPath = Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.record_folder) + "/";
        fileExt = getResources().getString(R.string.record_file_prefix);
        fileListAdapter = new FileListAdapter(this, searchPath,
                new String[]{fileExt}, false);
    }

    @Override
    public void initView() {
        lv_file = findViewById(R.id.lv_file);
        tv_no_file = findViewById(R.id.tv_no_file);
        rl_list = findViewById(R.id.rl_list);
        lv_file.setAdapter(fileListAdapter);
        lv_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListActivity.this, PlayActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(getResources().getString(R.string.uri_file_header) + getPath(i)),
                        getResources().getString(R.string.audio_type) + fileExt.substring(1));
                startActivity(intent);
            }
        });
        lv_file.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                popupWindowDialog = new PopupWindowDialog(ListActivity.this);
                popupWindowDialog.showManage(rl_list,
                        new PopupWindowDialog.ManageListener() {
                            @Override
                            public void onRename() {
                                popupWindowDialog.dismiss();
                                popupWindowDialog.showRename(rl_list, new PopupWindowDialog.RenameListener() {
                                    @Override
                                    public void onRename(String name) {
                                        String path = getPath(name);
                                        if (FileUtil.isFileExistsByPath(path)) {
                                            Toast.makeText(ListActivity.this, getResources()
                                                    .getString(R.string.error_file_already_exists), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if (!FileUtil.renameFileByPath(getPath(i), path)) {
                                            Toast.makeText(ListActivity.this, getResources()
                                                    .getString(R.string.error_renaming), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        fileListAdapter.data.set(i, name);
                                        fileListAdapter.notifyDataSetChanged();
                                        updateList();
                                        Toast.makeText(ListActivity.this, R.string.rename_success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        popupWindowDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onDelete() {
                                try {
                                    if (!FileUtil.deleteFileByPath(getPath(i))) {
                                        Toast.makeText(ListActivity.this,
                                                getResources().getString(R.string.error_deleting), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(ListActivity.this,
                                            getResources().getString(R.string.error_deleting), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                fileListAdapter.data.remove(i);
                                fileListAdapter.notifyDataSetChanged();
                                updateList();
                                popupWindowDialog.dismiss();
                                Toast.makeText(ListActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
                                popupWindowDialog.dismiss();
                            }
                        });
                return true;
            }
        });
        updateList();
    }

    private String getName(int i) {
        return fileListAdapter.data.get(i);
    }

    private String getPath(int i) {
        return searchPath + getName(i) + fileExt;
    }

    private String getPath(String name) {
        return searchPath + name + fileExt;
    }

    private void updateList() {
        if (fileListAdapter != null && fileListAdapter.data != null) {
            if (!fileListAdapter.data.isEmpty()) {
                tv_no_file.setVisibility(View.GONE);
                lv_file.setVisibility(View.VISIBLE);
                return;
            }
        }
        lv_file.setVisibility(View.GONE);
        tv_no_file.setVisibility(View.VISIBLE);
    }
}
