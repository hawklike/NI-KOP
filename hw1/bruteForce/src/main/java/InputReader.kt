import java.io.File

class InputReader(val base: String) {

    fun initKnapsackProblems(): List<Task> {
        val files = File(base).listFiles { file ->
            file.name.matches(Regex("NR.*"))
        }

        return files?.fold(mutableListOf<Task>()) { acc, file ->
            acc.add(Task(file.name, parseTasks(file)))
            acc
        } ?: emptyList()
    }

    private fun parseTasks(file: File): List<KnapsackProblem> {
        val problems = mutableListOf<KnapsackProblem>()
        file.forEachLine { line ->
            val data = line.split(" ")
            val id = data[0].toInt()
            val nItems = data[1].toInt()
            val maxWeight = data[2].toInt()
            val minPrice = data[3].toInt()
            val items = mutableListOf<Item>()
            for(i in 4 until data.size step 2) {
                items.add(Item(data[i].toInt(), data[i+1].toInt()))
            }
            problems.add(KnapsackProblem(id, nItems, maxWeight, minPrice, items))
        }
        return problems
    }

    fun parseSolution(fileName: String): List<Int> {
        val prices = mutableListOf<Int>()
        File(fileName).forEachLine { line ->
            prices.add(line.split(" ")[2].toInt())
        }
        return prices
    }

}