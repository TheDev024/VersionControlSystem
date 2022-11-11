package svcs

import java.io.File
import java.security.MessageDigest
import java.time.Instant

class VersionControlSystem {

    private val commands: Map<String, String> = mapOf(
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file.",
    )
    private val commits = "vcs/commits"
    private val config = "vcs/config.txt"
    private val index = "vcs/index.txt"
    private val log = "vcs/log.txt"

    /**
     * Initializing git directory structure
     */
    init {
        val vcs = File("vcs")
        if (!vcs.exists()) vcs.mkdir()
        val commits = File(this.commits)
        if (!commits.exists()) commits.mkdir()
        val config = File(this.config)
        if (!config.exists()) config.createNewFile()
        val index = File(this.index)
        if (!index.exists()) index.createNewFile()
        val log = File(this.log)
        if (!log.exists()) log.createNewFile()
    }

    fun cmd(input: Array<String>) {
        if (input.isEmpty() || input[0] == "--help") help()
        else when {
            input[0] == "config" -> {
                val path = "vcs/config.txt"
                val file = File(path)
                if (input.size == 1) {
                    if (file.readText() != "") {
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
                val file = File(index)
                if (input.size == 1) {
                    if (file.readText() != "") {
                        println("Tracked files:")
                        file.readLines().forEach { println(it) }
                    } else println("Add a file to the index.")
                } else {
                    val newPath = input[1]
                    val newFile = File(newPath)
                    if (newFile.exists()) {
                        file.appendText(newPath + '\n')
                        println("The file '$newPath' is tracked.")
                    } else println("Can't find '$newPath'.")
                }
            }

            input[0] == "log" -> {
                val logInfo = File(log).readText()
                if (logInfo == "") println("No commits yet.") else println(logInfo)
            }

            input[0] == "commit" -> if (input.size == 1) println("Message was not passed.") else {
                val commitMessage = input.slice(1..input.lastIndex).joinToString("")
                val trackedFiles = File(index).readLines().map { File(it) }
                val commits = File(this.commits).listFiles()
                if (trackedFiles.isEmpty()) println("Nothing to commit.")
                else if (commits!!.isEmpty()) commit(commitMessage)
                else {
                    val lastCommit = commits[0].listFiles()!!
                    if (trackedFiles.sortedBy { it.name }
                            .map { it.readText().hashCode() } == lastCommit.sortedBy { it.name }
                            .map { it.readText().hashCode() }) println("Nothing to commit.") else commit(commitMessage)
                }
            }

            input[0] == "checkout" -> if (input.size == 1) println("Commit id was not passed.") else {
                if (input[1] !in File(commits).listFiles()!!.map { it.name }) println("Commit does not exist.")
                else {
                    val checkoutRepo = File("vcs/commits/${input[1]}").listFiles()
                    checkoutRepo!!.forEach {
                        File(it.name).writeText(it.readText())
                    }
                    println("Switched to commit ${input[1]}.")
                }
            }

            else -> if (commands.containsKey(input[0])) println(commands[input[0]]) else println("'${input[0]}' is not a SVCS command.")
        }
    }

    private fun commit(commitMessage: String) {
        println("Changes are committed.")
        val commitHash = Instant.now().hashCode()
        val recentFiles = File(index).readLines().map { File(it) }
        val username = File(config).readText()
        val newLog = "commit $commitHash\nAuthor: $username\n$commitMessage\n\n"
        val logFile = File(log)
        val logHistory = logFile.readText()
        logFile.writeText(newLog + logHistory)
        recentFiles.forEach {
            val newPath = "vcs/commits/$commitHash/${it.name}"
            val newFile = File(newPath)
            it.copyTo(newFile)
        }
    }

    private fun help() {
        println("These are SVCS commands:")
        for ((command, description) in commands) println(
            String.format("%-11s", command) + description
        )
    }
}

fun main(args: Array<String>) {
    val control = VersionControlSystem()
    control.cmd(args)
}