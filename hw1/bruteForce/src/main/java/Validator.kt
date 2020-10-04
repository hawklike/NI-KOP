import java.lang.RuntimeException

class Validator {
    fun validate(base: String) {
        val inputReaderNR = InputReader(base)
        val tasks = inputReaderNR.initKnapsackProblems()
        val solutions = inputReaderNR.prepareSolutions()

        if(tasks.size != solutions.size) throw RuntimeException("tasks and solutions are not equal")

        tasks.forEachIndexed { i, task ->
            println("------------TASK ${task.file.name} -----------------")
            task.instances.forEachIndexed { j, problem ->
                val maxPrice = problem.getMaxPrice()
                val referencedPrice = solutions[i].solutions[j].bestPrice
                print("${j+1}:\t $maxPrice vs $referencedPrice ")
                if(maxPrice == referencedPrice) println("OK") else println("FAIL")
            }
        }
    }
}