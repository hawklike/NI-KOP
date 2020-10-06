import java.io.File

class Statistics(nTasks: Int) {
    val tasks = arrayOfNulls<TaskStats>(nTasks)
}



data class TaskStats(val task: File, val nInstances: Int, val iterations: ULong, val time: Double? = null)