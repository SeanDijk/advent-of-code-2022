fun main() {
    fun part1(input: List<String>): Int {
        var current = 0
        var max = 0
        for (line in input) {
            if (line.isBlank()) {
                if (current > max) {
                    max = current
                }
                current = 0
            } else {
                current += line.toInt()
            }
        }
        return max
    }

    fun retrieveTotalCaloriesPerElf(input: String): Sequence<Int> {
        return input
            // Group the snacks held by a single elf to their own string
            .splitToSequence("\n\n")
            .map { singleElfSnacksString ->
                // Split to a string per snack
                singleElfSnacksString.splitToSequence("\n")
                    .filter { it.isNotBlank() }
                    // convert them to integers
                    .map { it.toInt() }
                    // And sum them to get the total calories per elf
                    .sum()
            }
    }

    fun part1v2(input: String) = retrieveTotalCaloriesPerElf(input).max()

    fun part2(input: String) = retrieveTotalCaloriesPerElf(input)
        .sortedDescending()
        .take(3)
        .sum()

    println(part1(readInput("Day01")))
    println(part1v2(readInputAsString("Day01")))
    println(part2(readInputAsString("Day01")))
}
