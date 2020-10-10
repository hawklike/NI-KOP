import java.io.File

@ExperimentalUnsignedTypes
class Statistics(dataBase: String) {

    private var outputBase: String = when(dataBase) {
        Configuration.DATA_BASE_FOLDER_NR -> {
            "${Configuration.OUTPUT_BASE_FILE}/NR"
        }
        Configuration.DATA_BASE_FOLDER_ZR -> {
            "${Configuration.OUTPUT_BASE_FILE}/ZR"
        }
        else -> {
            Configuration.OUTPUT_BASE_FILE
        }
    }

    @Suppress("ConstantConditionIf")
    fun printToFile(
            task: TaskStats,
            filename: String = when(task.method) {
                KnapsackProblem.Method.BRUTEFORCE -> Configuration.OUTPUT_FILENAME_BRUTEFORCE
                KnapsackProblem.Method.SMART_BRUTEFORCE -> Configuration.OUTPUT_FILENAME_SMART_BRUTEFORCE
                KnapsackProblem.Method.BRANCH_AND_BOUND -> Configuration.OUTPUT_FILENAME_BRANCH_AND_BOUND
            }
    ) {
        val output = """
            //////// TASK: ${task.task.name} ////////
            method: ${task.method}
            n: ${task.nInstances}
            time: ${task.time} s
            total iterations: ${task.iterations}
            avg iterations: ${task.avgIterations}
            max iterations: ${task.maxIterations}
        """.trimIndent()

        OutputWriter(outputBase, filename).appendToEnd(output + "\n")
    }

    fun printToFile(
            histogram: Histogram,
            filename: String = when(histogram.method) {
                KnapsackProblem.Method.BRUTEFORCE -> Configuration.OUTPUT_FILENAME_BRUTEFORCE
                KnapsackProblem.Method.SMART_BRUTEFORCE -> Configuration.OUTPUT_FILENAME_SMART_BRUTEFORCE
                KnapsackProblem.Method.BRANCH_AND_BOUND -> Configuration.OUTPUT_FILENAME_BRANCH_AND_BOUND
            }) {
        OutputWriter(outputBase, "histogram_${histogram.taskName}_${filename}_iterations").appendToEnd(histogram.iterations.toString())
        OutputWriter(outputBase, "histogram_${histogram.taskName}_${filename}_time").appendToEnd(histogram.time.toString())
    }
}


@ExperimentalUnsignedTypes
data class TaskStats(
        val task: File,
        val nInstances: Int,
        val iterations: ULong,
        val avgIterations: ULong,
        val maxIterations: ULong,
        val method: KnapsackProblem.Method,
        val time: Double
)

@ExperimentalUnsignedTypes
data class Histogram(val method: KnapsackProblem.Method, val taskName: String, val iterations: ULong, val time: Double) {
    override fun toString(): String {
        return "iterations: $iterations time: $time s"
    }
}