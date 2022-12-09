fun main() {

    fun splitBackpackInCompartments(input: List<String>) = input.asSequence().map {
        val half = it.length / 2
        Pair(it.substring(0, half), it.substring(half, it.length))
    }


    fun score(char: Char): Int {
        // Use the char code so we don't have to make a score table.
        // A to Z is 65 to 90, so use 'A' as start point to adjust to our scores
        val startPoint = 'A'.code

        // Determine score as if it were an uppercase char. Add 1 as baseline, since our scoring for 'A' is 1 and not 0
        val lowerCaseScore = char.uppercaseChar().code - startPoint + 1

        // Adjust the score if the original input was lowercase by adding 26 to it.
        return if (char.isLowerCase())
            lowerCaseScore
        else
            lowerCaseScore + 26
    }

    fun part1(input: List<String>): Int {
        return splitBackpackInCompartments(input)
            // Find the common letters between the two compartments
            .map { (x, y) -> x.toSet().intersect(y.toSet()) }
            // score them
            .flatMap { letters -> letters.asSequence().map { score(it) } }
            // and sum to get the total
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            // Process as groups of 3
            .chunked(3) { list ->
                list.asSequence()
                    .map { it.toSet() }
                    // Find the common item
                    .reduce { acc, chars -> acc.intersect(chars) }
                    // Map to the score
                    .sumOf { score(it) }
            }
            // Sum all the scores
            .sum()
    }

    val test = """
    vJrwpWtwJgWrhcsFMMfFFhFp
    jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
    PmmdzqPrVvPwwTWBwg
    wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
    ttgJtRGJQctTZtZT
    CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent().lines()


    println(part1(test))
    println(part1(readInput("Day03")))
    println(part2(test))
    println(part2(readInput("Day03")))
}
