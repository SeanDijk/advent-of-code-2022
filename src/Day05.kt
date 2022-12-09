fun main() {

    data class Command(val fromIndex: Int, val toIndex: Int, val times: Int)
    data class Input(val stacks: List<ArrayDeque<String>>, val commands: List<Command>)

    fun parseInput(input: List<String>): Input {
        println("---------- Parse ----------")
        // Split at the blank line
        val splitIndex = input.indexOf("")

        val amountOfStacks = input[splitIndex - 1].split(' ')
            .asSequence()
            .filter { it.isNotBlank() }
            .map { it.toInt() }
            .last()
        val stackLines = input.subList(0, splitIndex - 1) // - 1 since we don't care about the stack indexes here
        val commandLines = input.subList(splitIndex + 1, input.size) // + 1 to skip the blank line

        val stacks: List<ArrayDeque<String>> = (0 until amountOfStacks).map { ArrayDeque() }

        println("Found $amountOfStacks stacks")

        stackLines.forEach { line ->
            // Chunks of 4, so each chunk is part of 1 stack
            line.chunked(4).forEachIndexed { index, string ->
                if (string.isNotBlank()) {
                    val trimmed = string.trim().removeSurrounding("[", "]")

                    println("Add '$trimmed' to stack ${index + 1}")
                    // Add first because we parse from top to bottom.
                    stacks[index].addFirst(trimmed)
                }
            }
        }

        val commands = commandLines.asSequence()
            .map { commandString ->
                val (times, fromIndex, toIndex) = commandString.filter { !it.isLetter() }
                    .trim()
                    .replace("  ", " ")
                    .split(' ')
                Command(fromIndex.toInt() - 1, toIndex.toInt() - 1, times.toInt())
            }
            .toList()

        println("---------- End Parse ----------")

        return Input(stacks, commands)
    }


    fun part1(lines: List<String>): String {
        val input = parseInput(lines)

        with(input) {
            commands.forEach {
                for (i in (0 until it.times)) {
                    stacks[it.toIndex].addLast(stacks[it.fromIndex].removeLast())
                }
            }

            return stacks.map { it.last() }.reduce{acc, s -> acc + s }
        }
    }

    fun part2(lines: List<String>): String {
        val input = parseInput(lines)

        with(input) {
            commands.forEach { command ->
                val from = stacks[command.fromIndex]
                val to = stacks[command.toIndex]

                (0 until command.times).asSequence()
                    .map { from.removeLast() }
                    .toList()
                    .reversed()
                    .forEach { to.addLast(it) }

            }

            return stacks.map { it.last() }.reduce{acc, s -> acc + s }
        }
    }

    val test = """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 
        
        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent().lines()


    println(part1(test))
    println(part1(readInput("Day05")))
    println(part2(test))
    println(part2(readInput("Day05")))
}




