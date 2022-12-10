enum class Visible {
    YES, NO, UNKNOWN
}

fun main() {

    // top left is (0,0)
    data class Coordinate(val x: Int, val y: Int)

    data class Tree(val height: Int, var visible: Visible = Visible.UNKNOWN) {
        override fun toString() = "$height${visible.toString().first()}"
    }

    data class Field(val tree: Tree, val coordinate: Coordinate) {
        override fun toString() = tree.toString()
    }

    class Grid(trees: List<Tree>, val width: Int) {
        val fields = trees.mapIndexed { index, tree ->
            Field(tree, index.toCoordinate());
        }

        private fun Coordinate.toIndex() = x + y * width
        private fun Int.toCoordinate() = Coordinate(this % width, this / width)

        fun getRow(int: Int) = ((int * width) until (int * width + width)).map { fields[it] }
        fun getColumn(int: Int) = (int..((width * width - (width - int))) step width).map { fields[it] }

        fun getAllLeftOf(coordinate: Coordinate) = getRow(coordinate.y).subList(0, coordinate.x).reversed()
        fun getAllRightOf(coordinate: Coordinate) = getRow(coordinate.y).subList(coordinate.x + 1, width)
        fun getAllAboveOf(coordinate: Coordinate) = getColumn(coordinate.x).subList(0, coordinate.y).reversed()
        fun getAllBelowOf(coordinate: Coordinate) = getColumn(coordinate.x).subList(coordinate.y + 1, width)

        fun getNeighbourChains(coordinate: Coordinate): List<List<Field>> {
            return listOf(
                getAllLeftOf(coordinate),
                getAllRightOf(coordinate),
                getAllAboveOf(coordinate),
                getAllBelowOf(coordinate),
            )
        }

        fun get(coordinate: Coordinate) = fields[coordinate.toIndex()]


        override fun toString(): String {
            return fields.chunked(width)
                .map { it.toString() }
                .reduce { acc, trees -> acc + "\n" + trees }
        }
    }


    fun createGrid(input: String, size: Int): Grid {
        val trees = input.filter { it.isDigit() }
            .map { Tree(it.digitToInt()) }
            .toList()
        return Grid(trees, size)
    }

    fun part1(input: String, size: Int): Int {
        val grid = createGrid(input, size)

        return grid.fields.map { current ->
            val neighbourChains = grid.getNeighbourChains(current.coordinate)
            neighbourChains.forEach neighbourLoop@{ chain ->
                if (chain.isEmpty() || chain.all { it.tree.height < current.tree.height }) {
                    current.tree.visible = Visible.YES
                    return@neighbourLoop
                }
            }

            if (neighbourChains.all { chain -> chain.any { field -> field.tree.height >= current.tree.height } }) {
                current.tree.visible = Visible.NO
            }

            if (current.tree.visible == Visible.UNKNOWN) {
                println("Couldnt solve ${current.coordinate}")
            }
            current.tree.visible;
        }.count { it == Visible.YES }
    }

    fun part2(input: String, size: Int): Int {
        val grid = createGrid(input, size)

        return grid.fields.maxOf { current ->
            grid.getNeighbourChains(current.coordinate).asSequence().map { chain ->
                if (chain.isEmpty()) {
                    0
                } else {
                    val treesISeeSmallerThanMe = chain.takeWhile { current.tree.height > it.tree.height }.count()
                    if (treesISeeSmallerThanMe == chain.count()) {
                        treesISeeSmallerThanMe
                    } else {
                        // We can still see the first tree that is higher than us, so add 1.
                        treesISeeSmallerThanMe + 1
                    }
                }
            }.reduce{acc, i -> acc * i }
        }
    }


    val test = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()
    println(part1(test, 5))
    println(part1(readInputAsString("Day08"), 99))
    println(part2(test, 5))
    println(part2(readInputAsString("Day08"), 99))
}
