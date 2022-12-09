enum class Tool(val score: Int) {
    ROCK(1), PAPER(2), SCISSORS(3)
}
enum class MatchResult(val score: Int) {
    WIN(6), DRAW(3), LOSS(0)
}
data class Line(val abc: Char, val xyz: Char)

val WIN_TABLE = mapOf(
    // x wins against y
    Tool.ROCK to Tool.SCISSORS,
    Tool.PAPER to Tool.ROCK,
    Tool.SCISSORS to Tool.PAPER
)
val LOSE_TABLE = WIN_TABLE.asSequence().map { Pair(it.value, it.key) }.toMap()

fun main() {

    fun transformLines(input: List<String>) = input.asSequence().map { Line(it.first(), it.last()) }

    fun toTool(char: Char) = when (char) {
        'A', 'X' -> Tool.ROCK
        'B', 'Y' -> Tool.PAPER
        'C', 'Z' -> Tool.SCISSORS
        else -> throw IllegalStateException()
    }

    fun part1(input: List<String>): Int {
        fun runMatch(elfTool: Tool, myTool: Tool): MatchResult {
            return when (elfTool) {
                myTool -> MatchResult.DRAW
                WIN_TABLE[myTool] -> MatchResult.WIN
                else -> MatchResult.LOSS
            }
        }

        return transformLines(input)
            .map {
                val elfTool = toTool(it.abc)
                val myTool = toTool(it.xyz)
                runMatch(elfTool, myTool).score + myTool.score
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        fun toolToLoseAgainst(tool: Tool) = WIN_TABLE[tool]!!
        fun toolToWinAgainst(tool: Tool) = LOSE_TABLE[tool]!!

        return transformLines(input)
            .map {
                val elfTool = toTool(it.abc)
                when (it.xyz) {
                    'X' -> toolToLoseAgainst(elfTool).score + MatchResult.LOSS.score
                    'Y' -> elfTool.score + MatchResult.DRAW.score
                    'Z' -> toolToWinAgainst(elfTool).score + MatchResult.WIN.score
                    else -> throw IllegalStateException()
                }
            }
            .sum()
    }

    val test = """
    A Y
    B X
    C Z
    """.trimIndent().lines()

    println(part1(test))
    println(part1(readInput("Day02")))
    println(part2(test))
    println(part2(readInput("Day02")))
}
