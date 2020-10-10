@ExperimentalUnsignedTypes
data class KnapsackProblem(
        val id: Int,
        val maxWeight: Int,
        val minPrice: Int,
        var items: List<Item>
) {

    fun compute(method: Method): KnapsackSolution<*> {
        return when(method) {
            Method.BRUTEFORCE -> {
                with(bruteForceSolver(0, 0, items.size - 1)) {
                    KnapsackSolution(price >= minPrice, iterations)
                }
            }
            Method.SMART_BRUTEFORCE -> {
                with(smartBruteForceSolver(0, 0, items.size - 1)) {
                    KnapsackSolution(price >= minPrice, iterations)
                }
            }
            Method.BRANCH_AND_BOUND -> {
                items = items.filter { it.weight <= maxWeight }
                with(branchAndBoundSolver(0, 0, items.size - 1)) {
                    KnapsackSolution(price >= minPrice, iterations)
                }
            }
        }
    }

    private fun bruteForceSolver(actualWeight: Int, actualPrice: Int, n: Int): Result {
        if(n == -1) {
            return if(actualWeight <= maxWeight) Result(actualPrice, 1u)
            else Result(0, 1u)
        }

        return max(
                bruteForceSolver(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1),
                bruteForceSolver(actualWeight, actualPrice, n - 1)
        )
    }

    private fun smartBruteForceSolver(actualWeight: Int, actualPrice: Int, n: Int): Result {
        if(n == -1) {
            return if(actualWeight <= maxWeight) Result(actualPrice, 1u)
            else Result(0, 1u)
        }

        if(actualWeight + items[n].weight > maxWeight) return branchAndBoundSolver(actualWeight, actualPrice, n - 1)

        return max(
                smartBruteForceSolver(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1),
                smartBruteForceSolver(actualWeight, actualPrice, n - 1)
        )
    }

    private var branchAndBoundFound = false

    private fun branchAndBoundSolver(actualWeight: Int, actualPrice: Int, n: Int): Result {
        if(actualPrice >= minPrice) branchAndBoundFound = true
        if(branchAndBoundFound) return Result(actualPrice, 1u)

        //all items are tried
        if(n == -1) return Result(actualPrice, 1u)

        //rest items are still below minimal price
        if(bound(actualPrice + items[n].price, n)) return Result(actualPrice, 1u)

        //current item is too heavy with previous items, do not add this item into the bag
        if(actualWeight + items[n].weight > maxWeight) return branchAndBoundSolver(actualWeight, actualPrice, n - 1)

        //try it with and without current item
        return max(
                branchAndBoundSolver(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1),
                branchAndBoundSolver(actualWeight, actualPrice, n - 1)
        )
    }

    private fun bound(actualPrice: Int, n: Int): Boolean {
        return (actualPrice + items.take(n).sumBy { it.price }) < minPrice
    }

    private fun max(solver: Result, solver1: Result): Result {
        return if(solver.price >= solver1.price) solver.apply { iterations += solver1.iterations }
        else solver1.apply { iterations += solver.iterations }
    }

    inner class Result(val price: Int, var iterations: ULong)

    enum class Method {
        BRUTEFORCE,
        SMART_BRUTEFORCE,
        BRANCH_AND_BOUND
    }
}


