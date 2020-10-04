import java.io.File
import java.lang.Integer.max

data class KnapsackProblem(
        val id: Int,
        val nItems: Int,
        val maxWeight: Int,
        val minPrice: Int,
        val items: List<Item>
) {
    fun getMaxPrice(): Int {
        return calculateMaxPrice(0, 0, nItems - 1)
    }

    private fun calculateMaxPrice(actualWeight: Int, actualPrice: Int, n: Int): Int {

        if(n == -1) {
            return if(actualWeight <= maxWeight) actualPrice
            else 0
        }

        return max(
                calculateMaxPrice(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1),
                calculateMaxPrice(actualWeight, actualPrice, n - 1)
        )
    }

}

data class Item(val weight: Int, val price: Int)


//todo read from file
fun main() {

//    val nInstances = listOf(4, 10, 15, 20, 22, 25, 27, 30, 32, 35, 37, 40)
//    val prices = InputReader.parseSolution("example/NR/NK32_sol.dat")
//    val problems = InputReader.parseTask("example/NR/NR32_inst.dat")
//
//    problems.forEachIndexed { index, problem ->
//        val maxPrice = problem.getMaxPrice()
//        val referencePrice = prices[index]
//        print("$maxPrice vs $referencePrice ")
//        if(maxPrice == referencePrice) println("OK") else println("FAIL")
//    }

    val tasks = InputReader("example/NR").initKnapsackProblems()
    println()
}