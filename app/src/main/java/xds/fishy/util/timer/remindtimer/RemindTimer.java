package xds.fishy.util.timer.remindtimer;

import java.util.Timer;
import java.util.TimerTask;

public class RemindTimer {
    private Timer timer;
    private boolean isPlanned = false;

    public boolean isPlanned() {
        return isPlanned;
    }

    public boolean plan(TimerTask task, long delay) {
        timer = new Timer();
        if (!isPlanned) {
            timer.schedule(task, delay);
            isPlanned = true;
            return true;
        }
        return false;
    }

    public boolean reset() {
        if (isPlanned) {
            timer.cancel();
            isPlanned = false;
            return true;
        }
        return false;
    }
}
