import kotlin.math.max

private val Boolean.toInt: Int
    get() {
        return if(this) 1 else 0
    }


@ExperimentalUnsignedTypes
class Validator {
    fun validate(base: String, method: KnapsackProblem.Method, input: String? = null, output: String? = null) {
        val inputReader = InputReader(base)
        val tasks = inputReader.initKnapsackProblems(input)
        val solutions = inputReader.prepareSolutions(output)

        val stats = Statistics(base)

        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            var iterations: ULong = 0u
            var maxIterations: ULong = 0u

            val epsilons = MutableList(task.instances.size) { 0.0 }

            println("------------TASK ${task.file.name} -----------------")

            val timer = StopwatchCPU(StopwatchCPU.IN_SECONDS)

            task.instances.forEachIndexed { j, problem ->
                val result = problem.compute(method)
                iterations += result.iterations
                maxIterations = max(maxIterations, result.iterations)
                val referencedPrice = solutions[i].solutions[j].bestPrice
                val computedPrice = result.solution as Int
                val epsilon = if(referencedPrice == 0) 0.0 else 1 - (computedPrice / referencedPrice.toDouble())
                epsilons[j] = epsilon
                print("${j+1}:\t res: $computedPrice ref: $referencedPrice eps: $epsilon ")
                if(referencedPrice == result.solution) println("OK") else println("FAIL")
            }

            val time = timer.elapsedTime()

            stats.printToFile(TaskStats(
                    task.file,
                    task.nItems,
                    iterations,
                    iterations / task.instances.size.toUInt(),
                    maxIterations,
                    method,
                    time,
                    epsilons.average(),
                    epsilons.max() ?: 0.0
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
                val referencedPrice = solutions[i].solutions[j].bestPrice
                print("${j+1}:\t res: ${(result.solution as Int)} ref: $referencedPrice time: $time mcs iterations: ${result.iterations} ")
                if(referencedPrice == result.solution) println("OK") else println("FAIL")
                stats.printToFile(Histogram(method, task.file.name, result.iterations, time))
            }
        }
    }
}

@ExperimentalUnsignedTypes
fun main() {
   with(Validator()) {
       validate(Configuration.DATA_BASE_FOLDER_NK, KnapsackProblem.Method.REDUX)
   }
}