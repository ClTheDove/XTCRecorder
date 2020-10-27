package xds.fishy.recorder.view.activity.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import xds.fishy.recorder.R;
import xds.fishy.recorder.view.activity.menu.MenuActivity;
import xds.fishy.recorder.widget.popupwindow.dialog.PopupWindowDialog;
import xds.fishy.util.file.FileUtil;
import xds.fishy.util.record.RecordUtil;
import xds.fishy.util.timer.remindtimer.RemindTimer;
import xds.fishy.util.timer.timer.Timer;
import xds.fishy.view.activity.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    ImageView iv_menu;
    private RelativeLayout rl_record;
    private TextView tv_status;
    private ImageView iv_record;
    private ImageView iv_pause;
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean isRecording = false;
    private boolean isPaused = false;
    private String recordSavePath;
    private String recordFileExt;
    private String recordName;
    private RecordUtil recordUtil;
    private int timerInterval = 100;
    private Timer timer;
    private RemindTimer remindTimer;
    private PopupWindowDialog popupWindowDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        setContentView(R.layout.activity_recorder);
        initVars();
        initView();
        initObject();
    }

    public boolean checkPermission(String[] permissions) {
        if (permissions.length == 0) return true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onUserLeaveHint() {
        leave();
        super.onUserLeaveHint();
    }

    @Override
    public void initVars() {
        recordSavePath = Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.record_folder) + "/";
        recordFileExt = getResources().getString(R.string.record_file_prefix);
    }

    @Override
    public void initView() {
        rl_record = findViewById(R.id.rl_record);
        iv_menu = findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        tv_status = findViewById(R.id.tv_status);
        iv_record = findViewById(R.id.iv_record);
        iv_record.setOnClickListener(this);
        iv_pause = findViewById(R.id.iv_pause);
        iv_pause.setOnClickListener(this);
    }

    @Override
    public void initObject() {
        recordUtil = new RecordUtil(recordSavePath, recordFileExt);
        timer = new Timer(timerInterval) {
            @Override
            public void run(final int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isRecording && (!isPaused)) {
                            int seconds = i / (1000 / timerInterval);
                            tv_status.setText(String.format(getResources().
                                    getString(R.string.recording), seconds / 60, seconds % 60));
                        }
                    }
                });
            }
        };
        remindTimer = new RemindTimer();
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

    public void showStatus(int targetMsg, final int revertMsg) {
        Toast.makeText(this, targetMsg, Toast.LENGTH_SHORT).show();
        tv_status.setText(revertMsg);
        /*
        if (!remindTimer.plan(new TimerTask() {
            @Override
            public void run() {
                if (remindTimer.isPlanned()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_status.setText(revertMsg);
                        }
                    });
                }
            }
        }, 1500)) {
            remindTimer.reset();
            remindTimer.plan(new TimerTask() {
                @Override
                public void run() {
                    if (remindTimer.isPlanned()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_status.setText(revertMsg);
                            }
                        });
                    }
                }
            }, 1500);
        }
        */
    }

    public void viewStartRecord() {
        iv_record.setImageResource(R.drawable.stop);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(iv_record.getLayoutParams());
        layoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.button_side_space));
        layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.button_bottom);
        iv_record.setLayoutParams(layoutParams);
        iv_record.invalidate();
        iv_pause.setImageResource(R.drawable.pause);
        iv_pause.setVisibility(View.VISIBLE);
    }

    public void viewStopRecord() {
        iv_record.setImageResource(R.drawable.record);
        iv_pause.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(iv_record.getLayoutParams());
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMarginStart(0);
        layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.button_bottom);
        iv_record.setLayoutParams(layoutParams);
        iv_record.invalidate();
    }

    public void viewPause(int i) {
        int seconds = i / (1000 / timerInterval);
        iv_pause.setImageResource(R.drawable.start);
        tv_status.setText(String.format(getResources().
                getString(R.string.record_pause_success), seconds / 60, seconds % 60));
    }

    public void viewResume() {
        iv_pause.setImageResource(R.drawable.pause);
    }

    public void startRecord() {
        if (timer.isStarted()) timer.stop();
        if (recordUtil.isRecording()) recordUtil.save();
        recordName = getResources().getString(R.string.last_not_saved_record);
        if (recordUtil.start(recordName)) {
            viewStartRecord();
            isRecording = true;
            isPaused = false;
            timer.start(0);
        } else {
            showStatus(R.string.error_starting_record, R.string.ready);
        }
    }

    public void stopRecord() {
        isRecording = false;
        isPaused = false;
        if (timer.isStarted()) timer.stop();
        viewStopRecord();
        if (recordUtil.save()) {
            tv_status.setText("");
            popupWindowDialog = new PopupWindowDialog(this);
            popupWindowDialog.showSave(rl_record, new PopupWindowDialog.SaveListener() {
                @Override
                public void onSave(String name) {
                    String path = recordSavePath + recordName + recordFileExt;
                    String path2 = recordSavePath + name + recordFileExt;
                    if (FileUtil.isFileExistsByPath(path2)) {
                        Toast.makeText(MainActivity.this,
                                R.string.error_file_already_exists, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    popupWindowDialog.dismiss();
                    if (FileUtil.renameFileByPath(path, path2)) {
                        showStatus(R.string.save_success, R.string.ready);
                    } else {
                        FileUtil.deleteFileByPath(path);
                        showStatus(R.string.error_saving, R.string.ready);
                    }
                }

                @Override
                public void onCancel() {
                    popupWindowDialog.dismiss();
                    String path = recordSavePath + recordName + recordFileExt;
                    if (!FileUtil.deleteFileByPath(path)) {
                        showStatus(R.string.error_deleting, R.string.ready);
                    } else {
                        showStatus(R.string.delete_success, R.string.ready);
                    }
                }
            });
        } else {
            showStatus(R.string.error_saving, R.string.ready);
        }
    }

    public void pauseRecord() {
        if (recordUtil.pause()) {
            isPaused = true;
            int i = timer.pause();
            viewPause(i);
        } else {
            tv_status.setText(R.string.error_pausing);
        }
    }

    public void resumeRecord() {
        if (recordUtil.resume()) {
            timer.resume();
            isPaused = false;
            viewResume();
        } else {
            tv_status.setText(R.string.error_resuming);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.iv_record:
                if (remindTimer.isPlanned()) remindTimer.reset();
                if (isRecording) {
                    stopRecord();
                } else {
                    startRecord();
                }
                break;
            case R.id.iv_pause:
                if (isRecording) {
                    if (isPaused) {
                        resumeRecord();
                    } else {
                        pauseRecord();
                    }
                }
                break;
        }
    }

    private void leave() {
        if (isRecording) stopRecord();
        if (popupWindowDialog != null) {
            tv_status.setText(R.string.ready);
            popupWindowDialog.dismiss();
        }
    }
}
