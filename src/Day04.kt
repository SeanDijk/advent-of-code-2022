fun main() {

    data class Assignment(val start: Int, val end: Int) {
        fun size() = end - start
    }

    fun parseAssignment(assignmentString: String): Assignment {
        val (x, y) = assignmentString.split('-')
        return Assignment(x.toInt(), y.toInt())
    }

    fun part1(input: List<String>): Int {
        return input.asSequence()
            .filter { line ->
                val (biggestAssignment, smallestAssignment) = line.split(',')
                    .map { parseAssignment(it) }
                    .sortedByDescending { it.size() }
                biggestAssignment.start <= smallestAssignment.start && biggestAssignment.end >= smallestAssignment.end
            }
            .count()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .filter { line ->
                val (assignment1, assignment2) = line.split(',').map { parseAssignment(it) }
                (assignment1.start <= assignment2.start && assignment1.end >= assignment2.start) ||
                        (assignment2.start <= assignment1.start && assignment2.end >= assignment1.start)
            }
            .count()
    }

    val test = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent().lines()


    println(part1(test))
    println(part1(readInput("Day04")))
    println(part2(test))
    println(part2(readInput("Day04")))
}
