package net.gtaun.shoebill.common.timers;

import net.gtaun.shoebill.object.Timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TemporaryTimer is used to create temporary timers than run for a short period of time.
 */
public class TemporaryTimer implements Timer {

    private static Timer recycleTimer;
    private static List<TemporaryTimer> timers;

    static {
        timers = new ArrayList<>();

        recycleTimer = Timer.create(5000, i -> {
            Iterator<TemporaryTimer> iterator = timers.iterator();
            while (iterator.hasNext()) {
                TemporaryTimer timer = iterator.next();
                if (!timer.isRunning() && timer.hasBeenStarted()) {
                    timer.destroy();
                    iterator.remove();
                }
            }
        });
        recycleTimer.start();
    }

    /**
     * Creates a temporary timer.
     *
     * @param interval In which interval the callback should be called.
     * @param callback The callback that will be executed when the interval has been reached.
     * @return The created TemporaryTimer instance.
     */
    public static TemporaryTimer create(int interval, TimerCallback callback) {
        return create(interval, Timer.COUNT_INFINITE, callback);
    }

    /**
     * Creates a temporary timer
     *
     * @param interval In which interval the callback should be called.
     * @param count    How often the callback should be executed before the timer is destroyed.
     * @param callback The callback that will be executed when the interval has been reached.
     * @return The created TemporaryTimer instance.
     */
    public static TemporaryTimer create(int interval, int count, TimerCallback callback) {
        return new TemporaryTimer(interval, count, callback);
    }

    private Timer timer;
    private boolean hasBeenStarted = false;

    private TemporaryTimer(int interval, int count, TimerCallback callback) {
        timer = Timer.create(interval, count, callback);
        timers.add(this);
    }

    /**
     * Gets the interval
     *
     * @return Interval
     */
    @Override
    public int getInterval() {
        return timer.getInterval();
    }

    /**
     * Gets the amount of TimerCallback calls.
     *
     * @return TimerCallback counts.
     */
    @Override
    public int getCount() {
        return timer.getCount();
    }

    /**
     * Returns the running state of the temporary timer.
     *
     * @return Running State
     */
    @Override
    public boolean isRunning() {
        return timer.isRunning();
    }

    /**
     * Sets the interval of the timer.
     *
     * @param ms Interval in milliseconds.
     */
    @Override
    public void setInterval(int ms) {
        timer.setInterval(ms);
    }

    /**
     * Sets the amount of TimerCallback counts.
     *
     * @param count Amount of calls.
     */
    @Override
    public void setCount(int count) {
        timer.setCount(count);
    }

    /**
     * Starts the temporary timer.
     */
    @Override
    public void start() {
        timer.start();
        hasBeenStarted = true;
    }

    /**
     * Stops the temporary timer (this might destroy the timer).
     */
    @Override
    public void stop() {
        timer.stop();
    }

    /**
     * Sets the callback of the temporary timer.
     *
     * @param callback The new TimerCallback.
     */
    @Override
    public void setCallback(TimerCallback callback) {
        timer.setCallback(callback);
    }

    /**
     * Destroys the temporary timer.
     */
    @Override
    public void destroy() {
        timer.destroy();
    }

    /**
     * Returns the state of the timer.
     *
     * @return State of Timer.
     */
    @Override
    public boolean isDestroyed() {
        return timer.isDestroyed();
    }

    private boolean hasBeenStarted() {
        return hasBeenStarted;
    }

}