import java.io.File

@ExperimentalUnsignedTypes
class Statistics {

    companion object {
        var FILE_NAME = Configuration.OUTPUT_FILENAME
    }

    fun printToFile(task: TaskStats, filename: String = FILE_NAME) {
        val output = """
            //////// TASK: ${task.task.name} ////////
            bruteforce: ${task.bruteforce}
            n: ${task.nInstances}
            total iterations: ${task.iterations}
            avg iterations: ${task.iterations / task.nInstances.toUInt()}
        """.trimIndent()

        OutputWriter(filename).appendToEnd(output + "\n")
    }
}


@ExperimentalUnsignedTypes
data class TaskStats(
        val task: File,
        val nInstances: Int,
        val iterations: ULong,
        val bruteforce: Boolean,
        val time: Double? = null
)