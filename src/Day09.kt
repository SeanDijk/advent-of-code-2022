import java.util.*
import kotlin.math.abs
import kotlin.math.sign

fun main() {

    data class Coordinate(val x: Int, val y: Int) {
        fun moveX(int: Int) = copy(x = x + int)
        fun moveY(int: Int) = copy(y = y + int)
        fun left() = moveX(-1)
        fun right() = moveX(1)
        fun up() = moveY(1)
        fun down() = moveY(-1)

        fun distanceX(other: Coordinate) = abs(x - other.x)
        fun distanceY(other: Coordinate) = abs(y - other.y)
        fun delta(other: Coordinate) = Coordinate(x - other.x, y - other.y)
    }

    data class Command(val direction: String, val times: Int)

    val commandBindings = mapOf(
        "R" to Coordinate::right,
        "U" to Coordinate::up,
        "L" to Coordinate::left,
        "D" to Coordinate::down
    )

    fun parseCommands(input: List<String>): Sequence<Command> {
        return input.asSequence().map {
            val (direction, times) = it.split(' ')
            Command(direction, times.toInt())
        }
    }

    fun part1(input: List<String>): Int {

        class GameState {
            var head = Coordinate(0, 0)
            var tail = Coordinate(0, 0)
                set(value) {
                    tailSpots.add(value)
                    field = value
                }

            val tailSpots = mutableSetOf(tail)
        }


        val gameState = GameState()

        fun move(command: Command) {
            val moveFunction = commandBindings[command.direction]!!
            with(gameState) {
                for (i in 0 until command.times) {
                    head = moveFunction(head)
                    val delta = head.delta(tail)
                    val distanceX = head.distanceX(tail)
                    val distanceY = head.distanceY(tail)

                    if (distanceX + distanceY == 3) {
                        tail = tail.moveX(delta.x.sign).moveY(delta.y.sign)
                    } else if (distanceX == 2) {
                        tail = tail.moveX(delta.x.sign)
                    } else if (distanceY == 2) {
                        tail = tail.moveY(delta.y.sign)
                    }
                }
            }
        }

        parseCommands(input).forEach { move(it) }

        return gameState.tailSpots.size
    }

    fun part2(input: List<String>): Int {
        class GameState(val debug: Boolean = false) {
            var head: Coordinate = Coordinate(0, 0)
            val tails: LinkedList<Coordinate> = LinkedList(generateSequence { Coordinate(0, 0) }.take(9).toList())
            val tracker = mutableSetOf(Coordinate(0, 0))
            val headTracker = mutableSetOf(Coordinate(0, 0))

            fun stateToString(size: Int): String {
                val yStart = size / 2
                var yCount = yStart
                val xStart = 1 - yCount
                var xCount = xStart
                var currentCoord = Coordinate(xCount, yCount)

                fun generateField(): String {

                    if (currentCoord == head) {
                        return "[H]"
                    }
                    val tailNumber = tails.indexOfFirst { it == currentCoord }
                    if (tailNumber != -1) {
                        return "[${tailNumber + 1}]"
                    }

//                    if (tracker.contains(currentCoord)) {
//                    if (headTracker.contains(currentCoord)) {
//                        return "[#]"
//                    }

                    if (currentCoord == Coordinate(0, 0)) {
                        return "[s]"
                    }

                    return "[ ]"
                }

                fun advanceField() {
                    currentCoord = if (currentCoord.x == yStart) {
                        currentCoord.copy(x = 1 - yStart, y = currentCoord.y - 1)
                    } else {
                        currentCoord.right()
                    }
                }


                return generateSequence {
                    generateSequence { generateField() }
                        .onEach { advanceField() }
                        .take(size)
                        .reduce() { acc, s -> "$acc $s" } + " ${yCount--}\n"
                }
                    .take(size)
                    .reduce { acc, sequence -> acc + sequence } +
                        generateSequence { "${xCount++}".padEnd(4) }
                            .take(size)
                            .reduce { acc, sequence -> acc + sequence }
            }

        }


        val gameState = GameState()

        fun move(command: Command) {
            val moveFunction = commandBindings[command.direction]!!
            with(gameState) {
                for (i in 0 until command.times) {
                    head = moveFunction(head)
                    headTracker.add(head)

                    val iterator: MutableListIterator<Coordinate> = tails.listIterator()
                    var last = head

                    while (iterator.hasNext()) {
                        val current = iterator.next()

                        val delta = last.delta(current)
                        val distanceX = last.distanceX(current)
                        val distanceY = last.distanceY(current)


                        /*
                            Welp this whole if statement was a roller coaster to fix...
                            It used to be `if (distanceX + distanceY == 3)`. Leftover from part 1
                            But the distance can be more than 3 now as well with the extra tails!
                            This is because the tails are able to move diagonal, so if it moves away diagonally,
                            the distance becomes 4.

                            At least I got to write a nifty way of printing the board state and add a debug mode :')
                         */
                        val newLocation = if (distanceX + distanceY >= 3) {
                            current.moveX(delta.x.sign).moveY(delta.y.sign)
                        } else if (distanceX == 2) {
                            current.moveX(delta.x.sign)
                        } else if (distanceY == 2) {
                            current.moveY(delta.y.sign)
                        } else {
                            current
                        }

                        last = newLocation
                        iterator.set(newLocation)
                    }

                    tracker.add(tails.last)

                    if (debug) {
                        println("\n\n\n")
                        println(gameState.stateToString(10))
                        println("Waiting for user input...")
                        Scanner(System.`in`).nextLine()
                    }
                }
            }
        }

        parseCommands(input).forEach {
            move(it)
        }


//        println(gameState.stateToString(150))
        return gameState.tracker.size
    }

    val test = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent().lines()

    val test2 = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent().lines()

    println(part1(test))
    println(part1(readInput("Day09")))
    println(part2(test))
    println(part2(test2))
    println(part2(readInput("Day09")))
}
