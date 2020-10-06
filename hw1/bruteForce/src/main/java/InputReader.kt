import java.io.File

class InputReader(private val base: String) {

    fun initKnapsackProblems(): List<Task> {
        val files = getFiles(Regex("NR.*"))

        val tasks =  files?.fold(mutableListOf<Task>()) { acc, file ->
            val parsed = parseTasks(file)
            acc.add(Task(file, parsed.first, parsed.second))
            acc
        }

        return tasks?.sorted() ?: emptyList()
    }

    private fun parseTasks(file: File): Pair<List<KnapsackProblem>, Int> {
        val problems = mutableListOf<KnapsackProblem>()
        var nItems = 0
        file.forEachLine { line ->
            val data = line.split(" ")
            val id = kotlin.math.abs(data[0].toInt())
            nItems = data[1].toInt()
            val maxWeight = data[2].toInt()
            val minPrice = data[3].toInt()
            val items = mutableListOf<Item>()
            for(i in 4 until data.size step 2) {
                items.add(Item(data[i].toInt(), data[i+1].toInt()))
            }
            problems.add(KnapsackProblem(id, nItems, maxWeight, minPrice, items))
        }
        return Pair(problems, nItems)
    }

    fun prepareSolutions(): List<Solution> {
        val files = getFiles(Regex("NK.*"))

        val tasks = files?.fold(mutableListOf<Solution>()) { acc, file ->
            val parsed = getSolution(file)
            acc.add(Solution(file, parsed.first, parsed.second))
            acc
        }

        return tasks?.sorted() ?: emptyList()
    }

    private fun getSolution(file: File): Pair<List<KnapsackReferenceSolution>, Int> {
        val solutions = mutableListOf<KnapsackReferenceSolution>()
        var nItems = 0
        file.forEachLine { line ->
            val data = line.split(" ")
            val id = kotlin.math.abs(data[0].toInt())
            nItems = data[1].toInt()
            val bestPrice = data[2].toInt()
            solutions.add(KnapsackReferenceSolution(id, bestPrice))
        }
        return Pair(solutions, nItems)
    }

    private fun getFiles(pattern: Regex): Array<File>? {
        return File(base).listFiles { file ->
            file.name.matches(pattern)
        }
    }

}