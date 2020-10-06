import java.io.File

@ExperimentalUnsignedTypes
data class Task(val file: File, val instances: List<KnapsackProblem>, val nItems: Int) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return compareBy<Task> {
            it.nItems
        }.compare(this, other)
    }
}

data class Solution(val file: File, val solutions: List<KnapsackReferenceSolution>, val nItems: Int): Comparable<Solution> {
    override fun compareTo(other: Solution): Int {
        return compareBy<Solution> {
            it.nItems
        }.compare(this, other)
    }
}

data class KnapsackReferenceSolution(val id: Int, val bestPrice: Int)

