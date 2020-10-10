import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean

class StopwatchCPU(private val units: Double) {
    private val threadTimer: ThreadMXBean = ManagementFactory.getThreadMXBean()
    private val start: Long

    init {
        start = threadTimer.currentThreadCpuTime
    }

    companion object Units {
        const val IN_SECONDS = 1000000000.0
        const val IN_MICROSECONDS = 1000000.0

    }

    //in seconds
    fun elapsedTime(): Double {
        val now = threadTimer.currentThreadCpuTime
        return (now - start) / units
    }
}