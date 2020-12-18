import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.max


@ExperimentalUnsignedTypes
class Validator {
    @Suppress("MemberVisibilityCanBePrivate")
    fun validate(base: String, method: KnapsackProblem.Method, input: String? = null, output: String? = null) {
        val inputReader = InputReader(base)
        val tasks = inputReader.initKnapsackProblems(input)
        //        val solutions = inputReader.prepareSolutions(output)

        val stats = Statistics(base)

        //        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            var iterations: ULong = 0u
            var maxIterations: ULong = 0u

            val nInstances = task.instances.size
            val epsilons = MutableList(nInstances) { 0.0 }

            val references = mutableListOf<Int>()

            task.instances.forEach { problem ->
                references.add(problem.compute(KnapsackProblem.Method.DYNAMIC_PROGRAMMING_BY_WEIGHT).solution as Int)
            }

            println("------------TASK ${task.file.name} -----------------")

            val timer = StopwatchCPU(StopwatchCPU.IN_MICROSECONDS)

            task.instances.forEachIndexed { j, problem ->
                val result = problem.compute(method)
                iterations += result.iterations
                maxIterations = max(maxIterations, result.iterations)
                val referencedPrice = references[j]
                val computedPrice = result.solution as Int
                val epsilon = calculateEpsilon(referencedPrice, computedPrice)
                epsilons[j] = epsilon
                //                print("${j + 1}:\t res: $computedPrice ref: $referencedPrice eps: $epsilon $method ")
                //                if(referencedPrice == result.solution) println("OK") else println("FAIL")
            }

            val time = timer.elapsedTime()
            val taskStats = TaskStats(task.file, nInstances, iterations, iterations / task.instances.size.toUInt(), maxIterations, method, time,
                                      epsilons.average(), epsilons.max() ?: 0.0
            )

            stats.printToFile(taskStats, Statistics.Stats.TIME)
            stats.printToFile(taskStats, Statistics.Stats.AVG_DELTA)
            stats.printToFile(taskStats, Statistics.Stats.MAX_DELTA)
            println("time: $time")
        }
    }

    @Suppress("unused")
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
                print("${j + 1}:\t res: ${(result.solution as Int)} ref: $referencedPrice time: $time mcs iterations: ${result.iterations}")
                if(referencedPrice == result.solution) println("OK") else println("FAIL")
                stats.printToFile(Histogram(method, task.file.name, result.iterations, time))
            }
        }
    }

    private fun calculateEpsilon(reference: Int, computed: Int): Double {
        return (abs(reference - computed) / max(reference, computed).toDouble()).let {
            if(it.isNaN()) 0.0
            else it
        }
    }

    private fun doSomething() {
        var list: MutableList<Int>? = mutableListOf()
        repeat(10000000) {
            list?.add(it)
        }
        list?.clear()
        list = null
        var i = 0
        repeat(10000) {
            repeat(2000000) {
                i++
            }
        }
        println(i)
        sleep(1000)
    }

    fun repeat(method: KnapsackProblem.Method) {
        doSomething()
        validate(Configuration.DATA_BASE_FOLDER_ZKW, method)
        doSomething()
        validate(Configuration.DATA_BASE_FOLDER_NK, method)
        doSomething()
        validate(Configuration.DATA_BASE_FOLDER_ZKC, method)
    }

    fun doFtpas(filename: String, epsilon: Double) {
        Configuration.OUTPUT_FILENAME_FTPAS = filename
        Configuration.FPTAS_EPSILON = epsilon
        doSomething()
        validate(Configuration.DATA_BASE_FOLDER_NK, KnapsackProblem.Method.FTPAS)
        doSomething()
        validate(Configuration.DATA_BASE_FOLDER_ZKW, KnapsackProblem.Method.FTPAS)
        doSomething()
        validate(Configuration.DATA_BASE_FOLDER_ZKC, KnapsackProblem.Method.FTPAS)
    }
}

@ExperimentalUnsignedTypes
fun main() {
    with(Validator()) {
        Configuration.ACTUAL_PARAMETER = "k/heavy"
        validate("../../hw3/example/${Configuration.ACTUAL_PARAMETER}", KnapsackProblem.Method.GREEDY)
        //        validate("../../hw3/example/${Configuration.ACTUAL_PARAMETER}/corr", KnapsackProblem.Method.DYNAMIC_PROGRAMMING_BY_PRICE)
        //        validate("../../hw3/example/${Configuration.ACTUAL_PARAMETER}/corr", KnapsackProblem.Method.BRANCH_AND_BOUND)
        //        validate("../../hw3/example/${Configuration.ACTUAL_PARAMETER}/corr", KnapsackProblem.Method.GREEDY)
        //        validate("../../hw3/example/${Configuration.ACTUAL_PARAMETER}/corr", KnapsackProblem.Method.SMART_BRUTEFORCE)
    }
}