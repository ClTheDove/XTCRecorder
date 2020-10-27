package xds.fishy.recorder.view.activity.play;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import xds.fishy.recorder.R;
import xds.fishy.util.file.FileUtil;
import xds.fishy.util.play.PlayUtil;
import xds.fishy.util.timer.timer.Timer;
import xds.fishy.view.activity.BaseActivity;
import xds.fishy.widget.progressbar.RoundProgressBar;
import xds.fishy.widget.toast.ProgressToast;

public class PlayActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {
    private Uri uri;
    private String audioName;
    private TextView tv_status;
    private RoundProgressBar rpb;
    private ImageView iv_play;
    private ImageView iv_volume_down;
    private ImageView iv_volume_up;
    private int timerInterval = 100;
    private Timer timer;
    private PlayUtil playUtil;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private int audioDuration = -1;
    private AudioManager audioManager;
    private int streamType = AudioManager.STREAM_MUSIC;
    private int maxVolume;
    private int currentVolume;
    private ProgressToast progressToast = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        String action = intent.getAction();
        uri = intent.getData();
        String type = intent.getType();
        if ((!Intent.ACTION_VIEW.equals(action)) || (uri == null || type == null) ||
                (!type.contains(getResources().getString(R.string.audio_type)))) {
            viewUnsupportedAction();
            finish();
            return;
        }
        String path = uri.getPath();
        if (path == null) {
            viewFileNotFound();
            finish();
            return;
        }
        if (!FileUtil.isFileExistsByPath(path)) {
            viewFileNotFound();
            finish();
            return;
        }
        audioName = FileUtil.getFileNoPrefixNameByPath(path);
        initView();
        initObject();
        startPlay();
    }

    private void viewUnsupportedAction() {
        Toast.makeText(this, getResources().getString(R.string.error_unsupported_action), Toast.LENGTH_SHORT).show();
    }

    private void viewFileNotFound() {
        Toast.makeText(this, getResources().getString(R.string.error_file_not_found), Toast.LENGTH_SHORT).show();
    }

    private void viewErrorPlaying() {
        Toast.makeText(this, getResources().getString(R.string.error_starting_play), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initView() {
        tv_status = findViewById(R.id.tv_status);
        rpb = findViewById(R.id.rpb);
        rpb.setOnClickListener(this);
        iv_play = findViewById(R.id.iv_play);
        iv_play.setOnClickListener(this);
        iv_volume_down = findViewById(R.id.iv_volume_down);
        iv_volume_down.setOnClickListener(this);
        iv_volume_down.setOnLongClickListener(this);
        iv_volume_up = findViewById(R.id.iv_volume_up);
        iv_volume_up.setOnClickListener(this);
        iv_volume_up.setOnLongClickListener(this);
        ImageView iv_fast_backward = findViewById(R.id.iv_fast_backward);
        iv_fast_backward.setOnClickListener(this);
        ImageView iv_fast_forward = findViewById(R.id.iv_fast_forward);
        iv_fast_forward.setOnClickListener(this);
    }

    @Override
    public void initObject() {
        timer = new Timer(timerInterval) {
            @Override
            public void run(final int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isPlaying && (!isPaused)) {
                            viewShowProgress(-1);
                        }
                    }
                });
            }
        };
        playUtil = new PlayUtil(this, uri) {
            @Override
            public void onCompletion() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopPlay();
                    }
                });
            }
        };
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            iv_volume_down.setVisibility(View.GONE);
            iv_volume_up.setVisibility(View.GONE);
            return;
        }
        maxVolume = audioManager.getStreamMaxVolume(streamType);
        currentVolume = audioManager.getStreamVolume(streamType);
    }

    private void viewStartPlay() {
        iv_play.setImageResource(R.drawable.pause);
    }

    private void viewStopPlay() {
        iv_play.setImageResource(R.drawable.start);
    }

    private void startPlay() {
        if (timer.isStarted()) timer.stop();
        if (playUtil.isPlaying()) playUtil.stop();
        if (!playUtil.play()) {
            viewErrorPlaying();
            finish();
            return;
        }
        audioDuration = playUtil.getDuration();
        viewStartPlay();
        isPlaying = true;
        isPaused = false;
        timer.start(0);
    }

    private void stopPlay() {
        if (isPlaying) playUtil.stop();
        isPlaying = false;
        isPaused = false;
        if (timer.isStarted()) timer.stop();
        viewStopPlay();
        finish();
    }

    private void viewPausePlay() {
        int seconds = playUtil.getCurrentPosition() / 1000;
        int seconds2 = audioDuration / 1000;
        iv_play.setImageResource(R.drawable.start);
        tv_status.setText(String.format(getResources().getString(R.string.play_pause_success),
                audioName, seconds / 60, seconds % 60, seconds2 / 60, seconds2 % 60));
    }

    private void viewResumePlay() {
        iv_play.setImageResource(R.drawable.pause);
    }

    private void pausePlay() {
        if (playUtil.pause()) {
            isPaused = true;
            timer.pause();
            viewPausePlay();
        } else {
            tv_status.setText(R.string.error_pausing);
        }
    }

    private void resumePlay() {
        if (playUtil.resume()) {
            timer.resume();
            isPaused = false;
            viewResumePlay();
        } else {
            tv_status.setText(R.string.error_resuming);
        }
    }

    private void viewShowVolume() {
        if (progressToast != null) progressToast.cancel();
        progressToast = new ProgressToast(this, currentVolume, maxVolume, Toast.LENGTH_SHORT);
        progressToast.setGravity(48, 0, 0);
        progressToast.show();
    }

    private void volumeDown() {
        currentVolume = audioManager.getStreamVolume(streamType);
        currentVolume--;
        if (currentVolume < 0) currentVolume = 0;
        setVolume();
    }

    private void volumeUp() {
        currentVolume = audioManager.getStreamVolume(streamType);
        currentVolume++;
        if (currentVolume > maxVolume) currentVolume = maxVolume;
        setVolume();
    }

    private void setVolume() {
        audioManager.setStreamVolume(streamType, currentVolume, 0);
        viewShowVolume();
    }

    private void volumeOff() {
        currentVolume = 0;
        setVolume();
    }

    private void volumeMax() {
        currentVolume = maxVolume;
        setVolume();
    }

    private void viewShowProgress(int ms) {
        if (ms == -1) {
            ms = playUtil.getCurrentPosition();
        } else if (ms > audioDuration) {
            ms = audioDuration;
        }
        int seconds = ms / 1000;
        int seconds2 = audioDuration / 1000;
        tv_status.setText(String.format(getResources().getString(isPaused ? R.string.play_pause_success : R.string.playing),
                audioName, seconds / 60, seconds % 60, seconds2 / 60, seconds2 % 60));
        rpb.setProgress(ms * 100 / audioDuration);
    }

    private void fastBackward() {
        int position = playUtil.getCurrentPosition();
        position -= 15 * 1000;
        if (position < 0) position = 0;
        viewShowProgress(position);
        playUtil.seekTo(position);
    }

    private void fastForward() {
        int position = playUtil.getCurrentPosition();
        position += 15 * 1000;
        viewShowProgress(position);
        if (position > audioDuration) {
            stopPlay();
            return;
        }
        playUtil.seekTo(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
            case R.id.rpb:
                if (!isPlaying) {
                    startPlay();
                } else {
                    if (isPaused) {
                        resumePlay();
                    } else {
                        pausePlay();
                    }
                }
                break;
            case R.id.iv_volume_down:
                volumeDown();
                break;
            case R.id.iv_volume_up:
                volumeUp();
                break;
            case R.id.iv_fast_backward:
                if (isPlaying) fastBackward();
                break;
            case R.id.iv_fast_forward:
                if (isPlaying) fastForward();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        leave();
        super.onBackPressed();
    }

    @Override
    public void onUserLeaveHint() {
        leave();
        super.onUserLeaveHint();
    }

    @Override
    public void onDestroy() {
        leave();
        super.onDestroy();
    }

    private void leave() {
        if (isPlaying) stopPlay();
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.iv_volume_down:
                volumeOff();
                break;
            case R.id.iv_volume_up:
                volumeMax();
                break;
        }
        return true;
    }
}
