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
            "${Configuration.OUTPUT_BASE_FILE}/${Configuration.ACTUAL_PARAMETER}"
        }
    }

    @Suppress("ConstantConditionIf")
    fun printToFile(task: TaskStats, stats: Stats, filename: String = getFilename(task.method), verbose: Boolean = false
    ) {
        var completeName = filename
        val output = if(verbose) {
            """
            //////// TASK: ${task.task.name} ////////
            method: ${task.method}
            n: ${task.nInstances}
            instance time: ${task.time / task.nInstances.toDouble()} micro s
            total iterations: ${task.iterations}
            avg iterations: ${task.avgIterations}
            max iterations: ${task.maxIterations}
            avg delta: ${task.avgDelta}
            max delta: ${task.maxDelta}
        """.trimIndent()
        } else {
            when(stats) {
                Stats.TIME -> {
                    completeName += "_time"
                    (task.time / task.nInstances.toDouble()).toString().replace(".", ",")
                }
                Stats.MAX_DELTA -> {
                    completeName += "_max_delta"
                    task.maxDelta.toString().replace(".", ",")
                }
                else -> {
                    completeName += "_avg_delta"
                    task.avgDelta.toString().replace(".", ",")
                }
            }
        }


        OutputWriter(outputBase, completeName).appendToEnd(if(verbose) output + "\n" else output)
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
        KnapsackProblem.Method.DYNAMIC_PROGRAMMING_BY_PRICE -> Configuration.OUTPUT_FILENAME_DYNAMIC_PROGRAMMING
        KnapsackProblem.Method.FTPAS -> Configuration.OUTPUT_FILENAME_FTPAS
        KnapsackProblem.Method.DYNAMIC_PROGRAMMING_BY_WEIGHT -> Configuration.OUTPUT_FILENAME_DYNAMIC_PROGRAMMING_BY_WEIGHT
    }

    enum class Stats {
        TIME, AVG_DELTA, MAX_DELTA
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