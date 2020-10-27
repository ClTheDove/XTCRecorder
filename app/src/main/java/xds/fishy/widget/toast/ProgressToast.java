package xds.fishy.widget.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import xds.fishy.recorder.R;


public class ProgressToast extends Toast {
    private ProgressBar pb;

    public ProgressToast(Context context, int progress, int max, int duration) {
        super(context);
        @SuppressLint("InflateParams")
        View rootView = LayoutInflater.from(context).inflate(R.layout.progress_toast, null);
        pb = rootView.findViewById(R.id.pb);
        pb.setMax(max);
        pb.setProgress(progress);
        setView(rootView);
        setDuration(duration);
    }

    public int getProgress() {
        return pb.getProgress();
    }

    public void setProgress(int i) {
        pb.setProgress(i);
    }
}
