import java.io.File

@ExperimentalUnsignedTypes
data class Task(val file: File, val instances: List<KnapsackProblem>, val nItems: Int) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return compareBy<Task> {
            it.nItems
        }.compare(this, other)
    }
}

data class Reference(val file: File, val solutions: List<KnapsackReference>, val nItems: Int): Comparable<Reference> {
    override fun compareTo(other: Reference): Int {
        return compareBy<Reference> {
            it.nItems
        }.compare(this, other)
    }
}

data class KnapsackReference(val id: Int, val bestPrice: Int)

@ExperimentalUnsignedTypes
data class KnapsackSolution<out T>(val solution: T, val iterations: ULong)
