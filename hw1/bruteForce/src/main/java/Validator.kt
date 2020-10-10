import java.lang.RuntimeException
import kotlin.math.max

private val Boolean.toInt: Int
    get() {
        return if(this) 1 else 0
    }


@ExperimentalUnsignedTypes
class Validator {
    fun validate(base: String, method: KnapsackProblem.Method) {
        val inputReaderNR = InputReader(base)
        val tasks = inputReaderNR.initKnapsackProblems()
        val solutions = inputReaderNR.prepareSolutions()

        val stats = Statistics(base)

        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            var iterations: ULong = 0u
            var maxIterations: ULong = 0u

            println("------------TASK ${task.file.name} -----------------")

            task.instances.forEachIndexed { j, problem ->
                val result = problem.compute(method)
                iterations += result.iterations
                maxIterations = max(maxIterations, result.iterations)
                val minPrice = problem.minPrice
                val referencedPrice = solutions[i].solutions[j].bestPrice
                print("${j+1}:\t res: ${(result.solution as Boolean).toInt} ref: $referencedPrice ")
                if(referencedPrice >= minPrice == result.solution) println("OK") else println("FAIL")
            }

            stats.printToFile(TaskStats(
                    task.file,
                    task.nItems,
                    iterations,
                    iterations / task.instances.size.toUInt(),
                    maxIterations,
                    method
            ))
            println("total iterations: $iterations")
        }
    }
}

@ExperimentalUnsignedTypes
fun main() {
    val validator = Validator()
    validator.validate(Configuration.DATA_BASE_FOLDER_ZR, KnapsackProblem.Method.BRANCH_AND_BOUND)
}