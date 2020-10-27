package xds.fishy.util.timer.timer;

import java.util.TimerTask;

interface ITimer {
    void run(int i);
}

public class Timer implements ITimer {
    private TimerTask timerTask;
    private java.util.Timer timer;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int lastPos;
    private int interval;

    public Timer(int interval) {
        this.interval = interval;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean start(final int startPos) {
        lastPos = startPos;
        timer = new java.util.Timer();
        if ((!isStarted) || isPaused) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isStarted && !isPaused) {
                        lastPos++;
                        Timer.this.run(lastPos);
                    }
                }
            };
            timer.schedule(timerTask, 0, interval);
            isStarted = true;
            isPaused = false;
            return true;
        }
        return false;
    }

    public int pause() {
        if (!isPaused) {
            isPaused = true;
            timerTask.cancel();
            timer.cancel();
            timerTask = null;
            timer = null;
            return lastPos;
        }
        return -1;
    }

    public boolean resume() {
        if (isPaused) {
            start(lastPos);
            isPaused = false;
        }
        return false;
    }

    public boolean stop() {
        if (isStarted) {
            isPaused = false;
            isStarted = false;
            if (timerTask != null && timer != null) {
                timerTask.cancel();
                timer.cancel();
                timerTask = null;
                timer = null;
            }
            return true;
        }
        return false;
    }

    @Override
    public void run(int i) {
    }
}
