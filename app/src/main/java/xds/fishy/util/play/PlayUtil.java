package xds.fishy.util.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

interface IPlayer {
    void onCompletion();
}

public class PlayUtil implements MediaPlayer.OnCompletionListener, IPlayer {
    private MediaPlayer mediaPlayer = null;
    private Context context;
    private Uri file;
    private boolean isPlaying = false;
    private boolean isPausing = false;

    public PlayUtil(Context context, Uri file) {
        this.context = context;
        this.file = file;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPausing() {
        return isPausing;
    }

    public int getDuration() {
        if (!isPlaying) return -1;
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        if (!isPlaying) return -1;
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        if (!isPlaying) return;
        mediaPlayer.seekTo(position);
    }

    public boolean play() {
        if (isPlaying) return false;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, file);
            } catch (IOException e) {
                return false;
            }
            mediaPlayer.setOnCompletionListener(this);
        }
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            isPausing = false;
            return true;
        } catch (IOException ignore) {
        }
        return false;
    }

    public boolean pause() {
        if (!isPausing) {
            try {
                mediaPlayer.pause();
            } catch (IllegalStateException e) {
                return false;
            }
            isPausing = true;
            return true;
        }
        return false;
    }

    public boolean resume() {
        if (isPausing) {
            try {
                mediaPlayer.start();
            } catch (IllegalStateException e) {
                return false;
            }
            isPausing = false;
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (isPlaying) {
            if (isPausing) {
                if (!resume()) return false;
            }
            isPlaying = false;
            isPausing = false;
            try {
                mediaPlayer.stop();
            } catch (RuntimeException ignore) {
            }
            mediaPlayer.release();
            mediaPlayer = null;
            return true;
        }
        return false;
    }

    @Override
    public void onCompletion() {
    }

    @Override
    public void onCompletion(MediaPlayer unused) {
        if (isPlaying) {
            if (isPausing) {
                resume();
            }
            isPlaying = false;
            isPausing = false;
            mediaPlayer.release();
            mediaPlayer = null;
        }
        onCompletion();
    }
}
