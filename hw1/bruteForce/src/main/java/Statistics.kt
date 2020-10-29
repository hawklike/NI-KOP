import java.io.File

@ExperimentalUnsignedTypes
class Statistics(dataBase: String) {

    private var outputBase: String = when(dataBase) {
        Configuration.DATA_BASE_FOLDER_NK -> {
            "${Configuration.OUTPUT_BASE_FILE}/NK"
        }
        Configuration.DATA_BASE_FOLDER_ZKC -> {
            "${Configuration.OUTPUT_BASE_FILE}/ZKC"
        }
        Configuration.DATA_BASE_FOLDER_ZKW -> {
            "${Configuration.OUTPUT_BASE_FILE}/ZKW"
        }
        else -> {
            Configuration.OUTPUT_BASE_FILE
        }
    }

    @Suppress("ConstantConditionIf")
    fun printToFile(
            task: TaskStats, filename: String = getFilename(task.method)
    ) {
        val output = """
            //////// TASK: ${task.task.name} ////////
            method: ${task.method}
            n: ${task.nInstances}
            time: ${task.time} s
            total iterations: ${task.iterations}
            avg iterations: ${task.avgIterations}
            max iterations: ${task.maxIterations}
            avg delta: ${task.avgDelta * 100} %
            max delta: ${task.maxDelta * 100} %
        """.trimIndent()

        OutputWriter(outputBase, filename).appendToEnd(output + "\n")
    }

    fun printToFile(
            histogram: Histogram, filename: String = getFilename(histogram.method)) {
        OutputWriter(outputBase, "histogram_${histogram.taskName}_${filename}_iterations_new").appendToEnd(histogram.iterations.toString())
        OutputWriter(outputBase, "histogram_${histogram.taskName}_${filename}_time_new").appendToEnd(histogram.time.toString())
    }

    private fun getFilename(method: KnapsackProblem.Method): String = when(method) {
        KnapsackProblem.Method.BRUTEFORCE -> Configuration.OUTPUT_FILENAME_BRUTEFORCE
        KnapsackProblem.Method.SMART_BRUTEFORCE -> Configuration.OUTPUT_FILENAME_SMART_BRUTEFORCE
        KnapsackProblem.Method.BRANCH_AND_BOUND -> Configuration.OUTPUT_FILENAME_BRANCH_AND_BOUND
        KnapsackProblem.Method.GREEDY -> Configuration.OUTPUT_FILENAME_GREEDY_HEURISTIC
        KnapsackProblem.Method.REDUX -> Configuration.OUTPUT_FILENAME_REDUX_HEURISTIC
        KnapsackProblem.Method.DYNAMIC_PROGRAMMING -> Configuration.OUTPUT_FILENAME_DYNAMIC_PROGRAMMING
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
        val time: Double,
        val avgDelta: Double,
        val maxDelta: Double
)

@ExperimentalUnsignedTypes
data class Histogram(val method: KnapsackProblem.Method, val taskName: String, val iterations: ULong, val time: Double) {
    override fun toString(): String {
        return "iterations: $iterations time: $time s"
    }
}