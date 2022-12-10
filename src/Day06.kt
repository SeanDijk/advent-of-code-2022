fun main() {

    fun findMarker(input: String, markerSize: Int): Int {
        val deque = ArrayDeque<Char>()
        val iterator = input.iterator()

        // Load the first marker chunk
        for (i in 0 until markerSize) {
            deque.addLast(iterator.nextChar())
        }
        // and then start the counter at markerSize to match.
        var count = markerSize

        // Define a simple check, to see if all 4 chars are unique
        fun isMarker() = deque.toSet().size == markerSize

        // Check the initial state.
        if (isMarker()) {
            return count
        }

        // Now loop over the iterator, adjust the deque and increment the counter.
        // As soon as a marker is detected return the counter.
        while (iterator.hasNext()) {
            deque.removeFirst()
            deque.addLast(iterator.nextChar())
            count++

            if (isMarker()) {
                return count
            }
        }
        return -1
    }


    fun part1(input: String): Int {
        return findMarker(input, 4)
    }

    fun part2(input: String): Int {
        return findMarker(input, 14)
    }

    val test = "mjqjpqmgbljsphdztnvjfqwrcgsmlb"


    println(part1(test))
    println(part1(readInputAsString("Day06")))
    println(part2(test))
    println(part2(readInputAsString("Day06")))
}




