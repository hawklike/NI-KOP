import java.lang.Integer.min
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.random.Random

@ExperimentalUnsignedTypes
data class KnapsackProblem(
    val id: Int, val maxWeight: Int, var items: List<Item>
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
                filterHeavyItems()
                price = branchAndBoundSolver(0, 0, items.size - 1)
            }
            Method.GREEDY -> {
                items = items.sortedByDescending { it.price / it.weight.toDouble() }
                price = greedy()
            }
            Method.REDUX -> {
                items = items.sortedByDescending { it.price / it.weight.toDouble() }
                price = redux(greedy())
            }
            Method.DYNAMIC_PROGRAMMING_BY_PRICE -> {
                filterHeavyItems()
                price = dynamicProgrammingByPrice()
            }
            Method.DYNAMIC_PROGRAMMING_BY_WEIGHT -> {
                filterHeavyItems()
                price = dynamicProgrammingByWeight()
            }
            Method.FTPAS -> {
                price = fptas()
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
            bruteForceSolver(actualWeight + items[n].weight, actualPrice + items[n].price, n - 1), bruteForceSolver(actualWeight, actualPrice, n - 1)
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
                price + item.price
            } else price
        }
    }

    private fun redux(greedy: Int): Int {
        return try {
            max(items.sortedByDescending { it.price }.first { it.weight <= maxWeight }.price, greedy)
        } catch(ex: NoSuchElementException) {
            greedy
        }
    }

    private fun dynamicProgrammingByPrice(): Int {
        val totalPrice = items.sumBy { it.price }
        val rows = totalPrice + 1
        val columns = items.size + 1

        val weights = Array(rows) { IntArray(columns) }

        for(n in 0 until columns) {
            for(c in 0 until rows) {
                if(c == 0) continue
                if(n == 0) {
                    weights[c][n] = Int.MAX_VALUE
                    continue
                }

                val item = items[n - 1]
                if(n == 1) {
                    weights[c][n] = if(c == item.price) item.weight else Int.MAX_VALUE
                    continue
                }

                val previousWeight = weights[c][n - 1]
                val cIndex = c - item.price

                if(cIndex < 0) weights[c][n] = previousWeight
                else {
                    if(weights[cIndex][n - 1] == Int.MAX_VALUE) weights[c][n] = min(previousWeight, weights[cIndex][n - 1])
                    else weights[c][n] = min(previousWeight, weights[cIndex][n - 1] + item.weight)
                }
            }
        }

        for(row in totalPrice downTo 0) {
            if(weights[row][columns - 1] <= maxWeight) {
                return row
            }
        }

        return 0
    }

    private fun dynamicProgrammingByWeight(): Int {
        val rows = items.size + 1
        val columns = maxWeight + 1

        val prices = Array(rows) { IntArray(columns) }

        for(i in 1 until rows) {
            for(c in 1 until columns) {
                val maxPriceWithoutCurr = prices[i - 1][c]
                var maxPriceWithCurr = 0

                val weightCurr = items[i - 1].weight
                if(c >= weightCurr) {
                    maxPriceWithCurr = items[i - 1].price
                    maxPriceWithCurr += prices[i - 1][c - weightCurr]
                }

                prices[i][c] = max(maxPriceWithCurr, maxPriceWithoutCurr)
            }
        }

        return prices[rows - 1][columns - 1]
    }

    private fun fptas(): Int {
        filterHeavyItems()
        if(items.isEmpty()) return 0
        val maxPrice = items.maxBy { it.price }!!.price
        val k = (Configuration.FPTAS_EPSILON * maxPrice) / items.size
        items.forEach {
            it.price = floor(it.price / k).toInt()
        }
        return (k * dynamicProgrammingByPrice()).toInt()
    }

    private fun filterHeavyItems() {
        items = items.filter { it.weight <= maxWeight }
    }

    enum class Method {
        BRUTEFORCE, SMART_BRUTEFORCE, BRANCH_AND_BOUND, GREEDY, REDUX, DYNAMIC_PROGRAMMING_BY_PRICE, DYNAMIC_PROGRAMMING_BY_WEIGHT, FTPAS,
        SIMULATED_ANNEALING
    }


    inner class SimulatedAnnealing(config: SimulatedAnnealingConfig) {
        private val initialTemperature = config.initialTemp
        private val minTemperature = config.minTemp
        private val coolingCoefficient = config.coolingCoefficient
        private val equilibrium = config.equilibrium

        private var state = State()
        private var bestState = State()

        fun count(): Int {
            simulateAnnealing()
            return bestState.price
        }


        private fun simulateAnnealing() {
            var innerCycle = 0
            var temperature = initialTemperature

            while(!isFrozen(temperature)) {
                while(equilibrium(innerCycle)) {
                    innerCycle++
                    state = createNewState(temperature)
                    if(state.price > bestState.price) bestState = state
                }

                temperature *= coolingCoefficient
            }
        }

        private fun createNewState(temperature: Double): State {
            var newState: State

            do {
                val position = ThreadLocalRandom.current().nextInt(0, items.size)
                newState = State(state)
                newState.changeBit(position)

            } while(newState.weight > maxWeight)

            if(newState.price > state.price) return newState

            val delta = newState.price - state.price
            val x = Random.nextDouble()
            return if(x < exp(delta / temperature)) newState
            else state
        }

        private fun isFrozen(temperature: Double) = temperature <= minTemperature

        private fun equilibrium(cycle: Int) = cycle < (equilibrium * items.size)

        inner class State() {
            private val presentItems = MutableList(items.size) { false }
            val weight: Int
                get() = presentItems.foldIndexed(0) { index: Int, acc: Int, present: Boolean ->
                    if(present) acc + items[index].weight
                    else acc
                }

            val price: Int
                get() = presentItems.foldIndexed(0) { index: Int, acc: Int, present: Boolean ->
                    if(present) acc + items[index].price
                    else acc
                }

            constructor(src: State) : this() {
                src.presentItems.forEachIndexed { index: Int, present: Boolean ->
                    presentItems[index] = present
                }
            }

            fun changeBit(where: Int) {
                presentItems[where] = !presentItems[where]
            }

        }

    }


    data class SimulatedAnnealingConfig(val initialTemp: Double, val minTemp: Double, val coolingCoefficient: Double, val equilibrium: Int)
}


