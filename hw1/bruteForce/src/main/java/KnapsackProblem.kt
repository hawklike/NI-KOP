import kotlin.math.max

@ExperimentalUnsignedTypes
data class KnapsackProblem(
        val id: Int,
        val maxWeight: Int,
        var items: List<Item>
) {
    private var iterations: ULong = 0U

    fun compute(method: Method): KnapsackSolution<*> {
        val price: Int
        when(method) {
            Method.BRUTEFORCE -> {
                price = bruteForceSolver(0, 0, items.size - 1)
            }
            Method.SMART_BRUTEFORCE -> {
                price = smartBruteForceSolver(0, 0, items.size - 1)
            }
            Method.BRANCH_AND_BOUND -> {
                items = items.filter { it.weight <= maxWeight }
                price = branchAndBoundSolver(0, 0, items.size - 1)
            }
            Method.GREEDY -> {
                items = items.sortedByDescending { it.price / it.weight.toDouble() }
                price = greedy()
            }
        }
        return KnapsackSolution(price, iterations)
    }

    private fun bruteForceSolver(actualWeight: Int, actualPrice: Int, n: Int): Int {
        iterations++

        if(n == -1) {
            return if(actualWeight <= maxWeight) actualPrice
            else 0
        }

        return max(
                bruteForceSolver(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1),
                bruteForceSolver(actualWeight, actualPrice, n - 1)
        )
    }

    private fun smartBruteForceSolver(actualWeight: Int, actualPrice: Int, n: Int): Int {
        iterations++

        if(n == -1) {
            return if(actualWeight <= maxWeight) actualPrice
            else 0
        }

        if(actualWeight + items[n].weight > maxWeight) return smartBruteForceSolver(actualWeight, actualPrice, n - 1)

        return max(
                smartBruteForceSolver(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1),
                smartBruteForceSolver(actualWeight, actualPrice, n - 1)
        )
    }

    private var branchAndBoundMaximum = 0

    private fun branchAndBoundSolver(actualWeight: Int, actualPrice: Int, n: Int): Int {
        iterations++

        branchAndBoundMaximum = max(branchAndBoundMaximum, actualPrice)

        //all items are tried
        if(n == -1) return actualPrice

        //rest items are still below minimal price
        if(bound(actualPrice + items[n].price, n)) return actualPrice

        //current item is too heavy with previous items, do not add this item into the bag
        if(actualWeight + items[n].weight > maxWeight) return branchAndBoundSolver(actualWeight, actualPrice, n - 1)

        //try it with and without current item
        return max(
                branchAndBoundSolver(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1),
                branchAndBoundSolver(actualWeight, actualPrice, n - 1)
        )
    }

    private fun bound(actualPrice: Int, n: Int): Boolean {
        return (actualPrice + items.take(n).sumBy { it.price }) <= branchAndBoundMaximum
    }

    private fun greedy(): Int {
        var weight = 0
        return items.fold(0) { price, item ->
            if(weight + item.weight <= maxWeight) {
                weight += item.weight
                iterations++
                item.price
            } else price
        }
    }

    enum class Method {
        BRUTEFORCE,
        SMART_BRUTEFORCE, BRANCH_AND_BOUND, GREEDY
    }
}


