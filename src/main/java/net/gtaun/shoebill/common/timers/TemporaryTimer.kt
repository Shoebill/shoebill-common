package net.gtaun.shoebill.common.timers

import net.gtaun.shoebill.constant.Collectable
import net.gtaun.shoebill.entities.Timer
import net.gtaun.shoebill.entities.TimerCallback

/**
 * TemporaryTimer is used to create temporary timers than run for a short period of time.
 */

class TemporaryTimer @JvmOverloads constructor(
        interval: Int,
        count: Int = Timer.COUNT_INFINITE,
        callback: TimerCallback? = null) : Timer() {

    private val timer: Timer = Timer.create(interval, count, callback)
    private var hasBeenStarted = false

    init {
        timers.add(this)
    }

    /**
     * The interval of the timer in milliseconds.
     */
    override var interval: Int
        get() = timer.interval
        set(ms) {
            timer.interval = ms
        }

    /**
     * The amount of TimerCallback calls.
     */
    override var count: Int
        get() = timer.count
        set(count) {
            timer.count = count
        }

    /**
     * The running state of the temporary timer.
     */
    override val isRunning: Boolean
        get() = timer.isRunning

    /**
     * Starts the temporary timer.
     */
    override fun start() {
        timer.start()
        hasBeenStarted = true
    }

    /**
     * Stops the temporary timer (this might destroy the timer).
     */
    override fun stop() = timer.stop()

    /**
     * Destroys the temporary timer.
     */
    override fun destroy() = timer.destroy()

    /**
     * The state of the timer.
     */
    override val isDestroyed: Boolean
        get() = timer.isDestroyed

    /**
     * The callback of the temporary timer.
     */
    override var callback: TimerCallback?
        get() = timer.callback
        set(callback) {
            timer.callback = callback
        }

    companion object : Collectable<TemporaryTimer> {

        /**
         * Creates a [TemporaryTimer] with params.
         */
        @JvmStatic
        fun create(interval: Int, callback: TimerCallback? = null): TemporaryTimer = TemporaryTimer.create(interval,
                COUNT_INFINITE, callback)

        /**
         * Creates a [TemporaryTimer] with params.
         * @param interval The interval in milliseconds.
         * @param count How often the Timer will get called.
         * @param callback The callback which will get invoked after the interval.
         * @return The created [TemporaryTimer].
         */
        @JvmStatic
        @JvmOverloads
        fun create(interval: Int, count: Int = COUNT_INFINITE, callback: TimerCallback? = null): TemporaryTimer =
                TemporaryTimer(interval, count, callback)

        private var recycleTimer: Timer = Timer.create(5000, Timer.COUNT_INFINITE, TimerCallback {
            val iterator = timers.iterator()
            while (iterator.hasNext()) {
                val timer = iterator.next()
                if (!timer.isRunning && timer.hasBeenStarted) {
                    timer.destroy()
                    iterator.remove()
                }
            }
        })

        private var timers: MutableList<TemporaryTimer> = mutableListOf()

        /**
         * Gets all available [TemporaryTimer] instances.
         */
        override fun get(): Collection<TemporaryTimer> = timers

        init {
            recycleTimer.start()
        }
    }
}