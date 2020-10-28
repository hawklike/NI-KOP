import kotlin.math.max

private val Boolean.toInt: Int
    get() {
        return if(this) 1 else 0
    }


@ExperimentalUnsignedTypes
class Validator {
    fun validate(base: String, method: KnapsackProblem.Method, input: String? = null, output: String? = null) {
        val inputReaderNR = InputReader(base)
        val tasks = inputReaderNR.initKnapsackProblems(input)
        val solutions = inputReaderNR.prepareSolutions(output)

        val stats = Statistics(base)

        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            var iterations: ULong = 0u
            var maxIterations: ULong = 0u

            println("------------TASK ${task.file.name} -----------------")

            val timer = StopwatchCPU(StopwatchCPU.IN_SECONDS)

            task.instances.forEachIndexed { j, problem ->
                val result = problem.compute(method)
                iterations += result.iterations
                maxIterations = max(maxIterations, result.iterations)
                val minPrice = problem.minPrice
                val referencedPrice = solutions[i].solutions[j].bestPrice
                print("${j+1}:\t res: ${(result.solution as Boolean).toInt} ref: $referencedPrice ")
                if(referencedPrice >= minPrice == result.solution) println("OK") else println("FAIL")
            }

            val time = timer.elapsedTime()

            stats.printToFile(TaskStats(
                    task.file,
                    task.nItems,
                    iterations,
                    iterations / task.instances.size.toUInt(),
                    maxIterations,
                    method,
                    time
            ))
            println("time: $time")
        }
    }

    fun createHistogram(base: String, method: KnapsackProblem.Method, input: String? = null, output: String? = null) {
        val inputReaderNR = InputReader(base)
        val tasks = inputReaderNR.initKnapsackProblems(input)
        val solutions = inputReaderNR.prepareSolutions(output)
        val stats = Statistics(base)

        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            println("------------HISTOGRAM ${task.file.name} -----------------")

            task.instances.forEachIndexed { j, problem ->
                val timer = StopwatchCPU(StopwatchCPU.IN_MICROSECONDS)
                val result = problem.compute(method)
                val time = timer.elapsedTime()
                val minPrice = problem.minPrice
                val referencedPrice = solutions[i].solutions[j].bestPrice
                print("${j+1}:\t res: ${(result.solution as Boolean).toInt} ref: $referencedPrice time: $time mcs iterations: ${result.iterations} ")
                if(referencedPrice >= minPrice == result.solution) println("OK") else println("FAIL")
                stats.printToFile(Histogram(method, task.file.name, result.iterations, time))
            }
        }
    }
}

@ExperimentalUnsignedTypes
fun main() {
   with(Validator()) {
//       var i = 0
//       repeat(1000) {
//           i += it
//       }
//       repeat(500) {
//           createHistogram(Configuration.DATA_BASE_FOLDER_NR, KnapsackProblem.Method.BRANCH_AND_BOUND, "NR32_inst.dat", "NK32_sol.dat")
//       }
       createHistogram(Configuration.DATA_BASE_FOLDER_NR, KnapsackProblem.Method.BRANCH_AND_BOUND, "NR25_inst.dat", "NK25_sol.dat")
       createHistogram(Configuration.DATA_BASE_FOLDER_NR, KnapsackProblem.Method.SMART_BRUTEFORCE, "NR25_inst.dat", "NK25_sol.dat")
       createHistogram(Configuration.DATA_BASE_FOLDER_ZR, KnapsackProblem.Method.BRANCH_AND_BOUND, "ZR25_inst.dat", "ZK25_sol.dat")
       createHistogram(Configuration.DATA_BASE_FOLDER_ZR, KnapsackProblem.Method.SMART_BRUTEFORCE, "ZR25_inst.dat", "ZK25_sol.dat")
   }
}