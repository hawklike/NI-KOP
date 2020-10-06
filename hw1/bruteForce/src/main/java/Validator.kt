import java.lang.RuntimeException

private val Boolean.toInt: Int
    get() {
        return if(this) 1 else 0
    }


@ExperimentalUnsignedTypes
class Validator {
    fun validate(base: String) {
        val inputReaderNR = InputReader(base)
        val tasks = inputReaderNR.initKnapsackProblems()
        val solutions = inputReaderNR.prepareSolutions()

        val stats = Statistics(tasks.size)

        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            var iterations: ULong = 0u
            println("------------TASK ${task.file.name} -----------------")

            task.instances.forEachIndexed { j, problem ->
                val result: Pair<Boolean, ULong> = problem.compute(true)
                iterations += result.second
                val minPrice = problem.minPrice
                val referencedPrice = solutions[i].solutions[j].bestPrice
                print("${j+1}:\t res: ${result.first.toInt} ref: $referencedPrice ")
                if(referencedPrice >= minPrice == result.first) println("OK") else println("FAIL")
            }

            stats.tasks[i] = TaskStats(task.file, task.nItems, iterations)
            println("total iterations: $iterations")
        }
    }
}

fun main() {

    val validator = Validator()
    validator.validate("example/NR")
}