import java.io.File

@ExperimentalUnsignedTypes
class InputReader(private val base: String) {

    fun initKnapsackProblems(filename: String?): List<Task> {
        return if(filename != null) pInitKnapsackProblems(filename)
        else pInitKnapsackProblems()
    }

    private fun pInitKnapsackProblems(): List<Task> {
        val pattern = when(base) {
            Configuration.DATA_BASE_FOLDER_NK -> Regex("NK\\d{1,2}_inst.dat")
            Configuration.DATA_BASE_FOLDER_ZKC -> Regex("ZKC\\d{1,2}_inst.dat")
            Configuration.DATA_BASE_FOLDER_ZKW -> Regex("ZKW\\d{1,2}_inst.dat")
            else -> throw IllegalArgumentException("base of this name doesn't exist")
        }

        val files = getFiles(pattern)

        val tasks =  files?.fold(mutableListOf<Task>()) { acc, file ->
            val parsed = parseTasks(file)
            acc.add(Task(file, parsed.first, parsed.second))
            acc
        }

        return tasks?.sorted() ?: emptyList()
    }

    private fun pInitKnapsackProblems(filename: String): List<Task> {
        val task = getFile(filename)?.let {
            val parsed = parseTasks(it)
            listOf(Task(it, parsed.first, parsed.second))
        }

        return task ?: emptyList()
    }

    private fun parseTasks(file: File): Pair<List<KnapsackProblem>, Int> {
        val problems = mutableListOf<KnapsackProblem>()
        var nItems = 0
        file.forEachLine { line ->
            val data = line.split(" ")
            val id = kotlin.math.abs(data[0].toInt())
            nItems = data[1].toInt()
            val maxWeight = data[2].toInt()
            val items = mutableListOf<Item>()
            for(i in 3 until data.size step 2) {
                items.add(Item(data[i].toInt(), data[i+1].toInt()))
            }
            problems.add(KnapsackProblem(id, maxWeight, items))
        }
        return Pair(problems, nItems)
    }

    fun prepareSolutions(filename: String?): List<Reference> {
        return if(filename != null) pPrepareSolutions(filename)
        else pPrepareSolutions()
    }

    private fun pPrepareSolutions(): List<Reference> {
        val pattern = when(base) {
            Configuration.DATA_BASE_FOLDER_NK -> Regex("NK\\d{1,2}_sol.dat")
            Configuration.DATA_BASE_FOLDER_ZKC -> Regex("ZKC\\d{1,2}_sol.dat")
            Configuration.DATA_BASE_FOLDER_ZKW -> Regex("ZKW\\d{1,2}_sol.dat")
            else -> throw IllegalArgumentException("base of this name doesn't exist")
        }
        val files = getFiles(pattern)

        val references = files?.fold(mutableListOf<Reference>()) { acc, file ->
            val parsed = getSolution(file)
            acc.add(Reference(file, parsed.first, parsed.second))
            acc
        }

        return references?.sorted() ?: emptyList()
    }

    private fun pPrepareSolutions(filename: String): List<Reference> {
        val reference = getFile(filename)?.let {
            val parsed = getSolution(it)
            listOf(Reference(it, parsed.first, parsed.second))
        }

        return reference?: emptyList()
    }


    private fun getSolution(file: File): Pair<List<KnapsackReference>, Int> {
        val solutions = mutableListOf<KnapsackReference>()
        var nItems = 0
        file.forEachLine { line ->
            val data = line.split(" ")
            val id = kotlin.math.abs(data[0].toInt())
            nItems = data[1].toInt()
            val bestPrice = data[2].toInt()
            solutions.add(KnapsackReference(id, bestPrice))
        }
        return Pair(solutions.distinctBy { it.id }, nItems)
    }

    private fun getFiles(pattern: Regex): Array<File>? {
        return File(base).listFiles { file ->
            file.name.matches(pattern)
        }
    }

    private fun getFile(name: String): File? {
        return File(base).listFiles()?.find { it.name == name }
    }

}