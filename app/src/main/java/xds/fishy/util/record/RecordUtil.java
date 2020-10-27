package xds.fishy.util.record;

import android.media.MediaRecorder;

import java.io.IOException;

import xds.fishy.util.file.FileUtil;

public class RecordUtil {
    private MediaRecorder mediaRecorder = null;
    private String savePath;
    private String fileExt;
    private boolean isRecording = false;
    private boolean isPaused = false;

    public RecordUtil(String savePath, String fileExt) {
        this.savePath = savePath;
        this.fileExt = fileExt;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean start(String name) {
        if (isRecording) return false;
        if (mediaRecorder == null) mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            String fileName = name + fileExt;
            String filePath = savePath + fileName;
            if (!FileUtil.isFileExistsByPath(savePath)) {
                if (!FileUtil.mkdirsByPath(savePath)) {
                    throw new RuntimeException("Failed to mkdir");
                }
            }
            if (FileUtil.isFileExistsByPath(filePath)) FileUtil.deleteFileByPath(filePath);
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            isPaused = false;
            return true;
        } catch (IllegalStateException | IOException ignore) {
        }
        return false;
    }

    public boolean pause() {
        if (!isPaused) {
            try {
                mediaRecorder.pause();
            } catch (IllegalStateException e) {
                return false;
            }
            isPaused = true;
            return true;
        }
        return false;
    }

    public boolean resume() {
        if (isPaused) {
            try {
                mediaRecorder.resume();
            } catch (IllegalStateException e) {
                return false;
            }
            isPaused = false;
            return true;
        }
        return false;
    }

    public boolean save() {
        if (isRecording) {
            if (isPaused) {
                if (!resume()) return false;
            }
            isRecording = false;
            isPaused = false;
            try {
                mediaRecorder.stop();
            } catch (RuntimeException ignore) {
            }
            mediaRecorder.release();
            mediaRecorder = null;
            return true;
        }
        return false;
    }

}
