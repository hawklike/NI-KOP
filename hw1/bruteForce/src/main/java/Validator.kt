import java.lang.RuntimeException

private val Boolean.toInt: Int
    get() {
        return if(this) 1 else 0
    }


@ExperimentalUnsignedTypes
class Validator {
    fun validate(base: String, bruteforce: Boolean) {
        val inputReaderNR = InputReader(base)
        val tasks = inputReaderNR.initKnapsackProblems()
        val solutions = inputReaderNR.prepareSolutions()

        val stats = Statistics()

        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            var iterations: ULong = 0u
            println("------------TASK ${task.file.name} -----------------")

            task.instances.forEachIndexed { j, problem ->
                val result: Pair<Boolean, ULong> = problem.compute(bruteforce)
                iterations += result.second
                val minPrice = problem.minPrice
                val referencedPrice = solutions[i].solutions[j].bestPrice
                print("${j+1}:\t res: ${result.first.toInt} ref: $referencedPrice ")
                if(referencedPrice >= minPrice == result.first) println("OK") else println("FAIL")
            }

            stats.printToFile(TaskStats(task.file, task.nItems, iterations, bruteforce))
            println("total iterations: $iterations")
        }
    }
}

@ExperimentalUnsignedTypes
fun main() {
    val validator = Validator()
    validator.validate(Configuration.DATA_BASE_FOLDER, Configuration.IS_BRUTEFORCE)
}