import java.io.File

data class Task(val file: File, val instances: List<KnapsackProblem>, val nItems: Int) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return compareBy<Task> {
            it.nItems
        }.compare(this, other)
    }
}

data class Solution(val file: File, val solutions: List<KnapsackSolution>, val nItems: Int): Comparable<Solution> {
    override fun compareTo(other: Solution): Int {
        return compareBy<Solution> {
            it.nItems
        }.compare(this, other)
    }
}

data class KnapsackSolution(val id: Int, val bestPrice: Int)

