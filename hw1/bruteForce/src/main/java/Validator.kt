import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.max


@ExperimentalUnsignedTypes
class Validator {
    @Suppress("MemberVisibilityCanBePrivate")
    fun validate(
        base: String, method: KnapsackProblem.Method, input: String? = null, output: String? = null,
        config: KnapsackProblem.SimulatedAnnealingConfig? = null, observingMethod: AnnealingConfigParameter? = null
    ) {
        val inputReader = InputReader(base)
        val tasks = inputReader.initKnapsackProblems(input)
        //        val solutions = inputReader.prepareSolutions(output)

        val stats = Statistics(base, config, observingMethod)

        //        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        val globalEpsilons = mutableListOf<Int>()

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
                val result = problem.compute(method, config)
                iterations += result.iterations
                maxIterations = max(maxIterations, result.iterations)
                val referencedPrice = references[j]
                val computedPrice = result.solution as Int
                val epsilon = calculateEpsilon(referencedPrice, computedPrice)
                epsilons[j] = epsilon
                print("${j + 1}:\t res: $computedPrice ref: $referencedPrice eps: $epsilon $method ")
                if(referencedPrice == result.solution) println("OK") else println("FAIL")
            }

            val time = timer.elapsedTime()


            val taskStats = TaskStats(
                task.file, nInstances, iterations, iterations / task.instances.size.toUInt(), maxIterations, method, time, epsilons.average(),
                epsilons.max() ?: 0.0
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
        //        val stats = Statistics(base, config, observingMethod)

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
                //                stats.printToFile(Histogram(method, task.file.name, result.iterations, time))
            }
        }
    }

    private fun calculateEpsilon(reference: Int, computed: Int): Double {
        return (abs(reference - computed) / max(reference, computed).toDouble()).let {
            if(it.isNaN()) 0.0
            else it
        }
    }

    fun doSomething() {
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
}

@ExperimentalUnsignedTypes
fun main() {
    with(Validator()) {
        //        doSomething()
        //        doSomething()
        repeat(10) {
            //100, 20, 0.8, 40
            var config = KnapsackProblem.SimulatedAnnealingConfig(100.0, 20.0, 0.2, 40)
            validate(
                Configuration.DATA_BASE_FOLDER_ZKW, KnapsackProblem.Method.SIMULATED_ANNEALING, "custom.dat", "", config,
                AnnealingConfigParameter.EQUILIBRIUM
            )
            config = KnapsackProblem.SimulatedAnnealingConfig(100.0, 20.0, 0.99, 40)
            validate(
                Configuration.DATA_BASE_FOLDER_ZKW, KnapsackProblem.Method.SIMULATED_ANNEALING, "custom.dat", "", config,
                AnnealingConfigParameter.EQUILIBRIUM
            )
            //            config = KnapsackProblem.SimulatedAnnealingConfig(100.0, 20.0, 0.8, 20)
            //            validate(
            //                Configuration.DATA_BASE_FOLDER_ZKW, KnapsackProblem.Method.SIMULATED_ANNEALING, "custom.dat", "", config,
            //                AnnealingConfigParameter.EQUILIBRIUM
            //            )
            //            config = KnapsackProblem.SimulatedAnnealingConfig(100.0, 20.0, 0.8, 40)
            //            validate(
            //                Configuration.DATA_BASE_FOLDER_ZKW, KnapsackProblem.Method.SIMULATED_ANNEALING, "custom.dat", "", config,
            //                AnnealingConfigParameter.EQUILIBRIUM
            //            )
            //            config = KnapsackProblem.SimulatedAnnealingConfig(100.0, 20.0, 0.8, 80)
            //            validate(
            //                Configuration.DATA_BASE_FOLDER_ZKW, KnapsackProblem.Method.SIMULATED_ANNEALING, "custom.dat", "", config,
            //                AnnealingConfigParameter.EQUILIBRIUM
            //            )
            //            config = KnapsackProblem.SimulatedAnnealingConfig(100.0, 20.0, 0.8, 160)
            //            validate(
            //                Configuration.DATA_BASE_FOLDER_ZKW, KnapsackProblem.Method.SIMULATED_ANNEALING, "custom.dat", "", config,
            //                AnnealingConfigParameter.EQUILIBRIUM
            //            )
        }
    }
}