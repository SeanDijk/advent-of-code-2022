// Not the most efficient code, but it works
interface FileSystemObject {
    fun size(): Int
}

sealed interface Command {
    data class Cd(val dir: String) : Command
    data class Ls(val output: List<String>) : Command
}

fun main() {
    fun parseCommands(input: String): List<Command> {
        return input
            // Split into strings of a command with outputs.
            // To achieve this, look at a new line (\n) followed by the start of a command ($).
            // Then because we do not want to 'delete' this 'separator', use a zero-width positive lookahead: https://stackoverflow.com/questions/4416425/how-to-split-string-with-some-separator-but-without-removing-that-separator-in-j
            .splitToSequence(Regex("(?=\\n\\\$)"))
            // Map to the typed command
            .map { commandWithOutput ->
                val trimmed = commandWithOutput.trim()
                when {
                    trimmed.startsWith("$ cd") -> Command.Cd(commandWithOutput.split(' ').last())
                    trimmed.startsWith("$ ls") -> {
                        // Now ls commands with their outputs are still a single string. Split it in lines.
                        // Also remove what would be the first line, since we only want the output.
                        Command.Ls(trimmed.removePrefix("$ ls\n").split('\n'))
                    }

                    else -> throw IllegalStateException("Unknown command: $commandWithOutput")
                }
            }
            .toList()
    }

    data class Dir(
        val name: String,
        val parent: Dir?,
        val fileSystemObjects: MutableMap<String, FileSystemObject> = HashMap(),
    ) : FileSystemObject {
        override fun size() = fileSystemObjects.values.sumOf { it.size() }
        override fun toString() = "$fileSystemObjects"
    }

    data class File(val name: String, val size: Int) : FileSystemObject {
        override fun size() = size
    }

    fun handleCd(command: Command.Cd, root: Dir, currentDir: Dir): Dir {
        return when (command.dir) {
            "/" -> root
            ".." -> currentDir.parent ?: throw IllegalStateException()
            else -> {
                // If the dir already exists return it, otherwise create a new one
                (currentDir.fileSystemObjects.computeIfAbsent(command.dir) { Dir(command.dir, currentDir) } as Dir) // Trust the advent of code input is good, and assume we do not have dirs and files with the same name.
                // Add it to the objects of the currentDir. If we overwrite it with itself it is not a big deal.
                    .also { currentDir.fileSystemObjects[command.dir] = it }
            }
        }
    }

    fun handleLs(command: Command.Ls, currentDir: Dir) {
        command.output
            // We honestly could not care less about dirs that are listed and not traversed, so we ignore them.
            .filter { !it.startsWith("dir ") }
            .forEach { fileDescriptor ->
                val (size, name) = fileDescriptor.split(' ')
                currentDir.fileSystemObjects[name] = File(name, size.toInt())
            }
    }

    fun traverseDir(root: Dir, handle: (Dir) -> Unit) {
        val toTraverse = ArrayDeque<Dir>()
        toTraverse.addFirst(root)

        while (toTraverse.isNotEmpty()) {
            val current = toTraverse.removeLast()
            toTraverse.addAll(current.fileSystemObjects.values.filterIsInstance<Dir>())
            handle(current)
        }
    }

    fun constructFileSystem(commands: List<Command>): Dir {
        val root = Dir("/", null)
        var currentDir = root

        commands.forEach { command ->
            when (command) {
                is Command.Cd -> currentDir = handleCd(command, root, currentDir)
                is Command.Ls -> handleLs(command, currentDir)
            }
        }
        return root
    }

    fun part1(input: String): Int {
        val commands = parseCommands(input)
        val root = constructFileSystem(commands)

        val dirsWithSizeLessThan100000 = mutableListOf<Dir>()
        traverseDir(root) {
            if (it.size() <= 100_000) {
                dirsWithSizeLessThan100000.add(it)
            }
        }
        return dirsWithSizeLessThan100000.sumOf { it.size() }
    }

    fun part2(input: String): Int {
        val diskSize = 70_000_000
        val neededRequired = 30_000_000

        val commands = parseCommands(input)
        val root = constructFileSystem(commands)
        val spaceToFree = neededRequired - (diskSize - root.size())

        val dirs = ArrayList<Dir>()
        traverseDir(root) { dirs.add(it) }


        return dirs.asSequence()
            .map { it.size() }
            .filter { it >= spaceToFree }
            .min()
    }

    val test = """
        $ cd /
        $ ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        $ cd a
        $ ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        $ cd e
        $ ls
        584 i
        $ cd ..
        $ cd ..
        $ cd d
        $ ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent()

    println(part1(test))
    println(part1(readInputAsString("Day07")))
    println(part2(test))
    println(part2(readInputAsString("Day07")))
}





