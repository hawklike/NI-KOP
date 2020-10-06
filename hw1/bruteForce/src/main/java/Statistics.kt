import java.io.File

@ExperimentalUnsignedTypes
class Statistics {

    @Suppress("ConstantConditionIf")
    fun printToFile(
            task: TaskStats,
            filename: String = if(Configuration.IS_BRUTEFORCE) Configuration.OUTPUT_FILENAME_NAIVE else Configuration.OUTPUT_FILENAME_BETTER
    ) {
        val output = """
            //////// TASK: ${task.task.name} ////////
            bruteforce: ${task.bruteforce}
            n: ${task.nInstances}
            total iterations: ${task.iterations}
            avg iterations: ${task.iterations / 500u}
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