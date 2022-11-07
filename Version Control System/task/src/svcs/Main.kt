package svcs

import java.io.File

class VersionControlSystem {
    private val commands: Map<String, String> = mapOf(
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file.",
    )

    fun cmd(input: Array<String>) {
        if (input.isEmpty() || input[0] == "--help") help()
        else when {
            input[0] == "config" -> {
                val path = "vcs/config.txt"
                val file = File(path)
                if (input.size == 1) {
                    if (file.exists()) {
                        val username = file.readText()
                        println("The username is $username.")
                    } else println("Please, tell me who you are.")
                } else {
                    val username = input[1]
                    if (!file.exists()) {
                        if (!File("vcs").exists()) File("vcs").mkdir()
                        File(path).createNewFile()
                    }
                    file.writeText(username)
                    println("The username is $username.")
                }
            }

            input[0] == "add" -> {
                val path = "vcs/index.txt"
                val file = File(path)
                if (input.size == 1) {
                    if (file.exists()) {
                        println("Tracked files:")
                        file.readLines().forEach { println(it) }
                    } else println("Add a file to the index.")
                } else {
                    val newPath = input[1]
                    val newFile = File(newPath)
                    if (newFile.exists()) {
                        if (!file.exists()) {
                            if (!File("vcs").exists()) File("vcs").mkdir()
                            File(path).createNewFile()
                        }
                        file.appendText(newPath + '\n')
                        println("The file '$newPath' is tracked.")
                    } else println("Can't find '$newPath'.")
                }
            }

            else -> if (commands.containsKey(input[0])) println(commands[input[0]]) else println("'${input[0]}' is not a SVCS command.")
        }
    }

    private fun help() {
        println("These are SVCS commands:")
        for ((command, description) in commands)
            println(
                String.format("%-11s", command) + description
            )
    }
}

fun main(args: Array<String>) {
    val control = VersionControlSystem()
    control.cmd(args)
}