package svcs

import java.io.File

val vcsDir = File(System.getProperty("user.dir"), "vcs")
val usernameFile = File(vcsDir, "config.txt")
val indexFile = File(vcsDir, "index.txt")
val logFile = File(vcsDir, "log.txt")
val commitDir = File(vcsDir, "commits")

//help function: This prints the available commands
fun help() {
    val helpList = mapOf(
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file.",
    )
    println("These are SVCS commands:")
    for ((key, value) in helpList)
        println("$key $value")
}

//config function: Creates a vcs folder to store username
fun config(args: Array<String>) {
    when (args.size) {
        1 -> {
            if (usernameFile.exists()) {
                val username = usernameFile.readText()
                println("The username is $username.")
            } else {
                println("Please, tell me who you are.")
            }
        }

        2 -> {
            val username = args[1]
            usernameFile.writeText(username)
            println("The username is $username.")

        }
    }
}

//add function: Adds file names to the index file to track them
fun add(args: Array<String>) {
    when (args.size) {
        1 -> {
            if (indexFile.exists()) {
                val index = indexFile.readText()
                println("Tracked files:\n$index")
            } else {
                println("Add a file to the index.")
            }
        }

        2 -> {
            val fileName = args[1]
            if (!File(fileName).exists()) {
                println("Can't find '$fileName'.")
            } else {
                if (!indexFile.exists()) {
                    indexFile.createNewFile()
                }
                indexFile.appendText("$fileName\n")
                println("The file '$fileName' is tracked.")
            }
        }
    }
}

//log function: If the commit log file exists print it, otherwise print "No commits yet."
fun log() {
    if (logFile.exists()) {
        val log = logFile.readText()
        println(log)
    } else {
        println("No commits yet.")
    }
}

fun commit(args: Array<String>) {

    fun copyFilesToCommitDir(indexLines: List<String>, subCommitDir: File) {
        for (fileName in indexLines) {
            val wrkDirFilePath = File(System.getProperty("user.dir"), fileName)
            val wrkDirFile = File(wrkDirFilePath.absolutePath)
            wrkDirFile.copyTo(File(subCommitDir, fileName), overwrite = true)
        }

    }

    fun areSomeFilesDifferent(indexLines: List<String>): Boolean {
        val prevCommitId = logFile.readLines().firstOrNull()?.substringAfter("commit ")
        val prevSubCommitDir = File(commitDir, "$prevCommitId")
        for (fileName in indexLines) {
            val wrkDirFilePath = File(System.getProperty("user.dir"), fileName)
            val commitDirFilePath = File(prevSubCommitDir, fileName)

            val wrkDirFile = File(wrkDirFilePath.absolutePath)
            val commitDirFile = File(commitDirFilePath.absolutePath)

            if (commitDirFilePath == null || !commitDirFilePath.exists()) {
                return true
            }
            if (wrkDirFile.readText() != commitDirFile.readText()) {
                return true
            }
        }
        return false
    }

    when (args.size) {
        1 -> println("Message was not passed.")
        2 -> {
            val indexLines = indexFile.readLines()
            val commitMessage = args[1]
            val commitId = "ID-${commitMessage.replace("\\s".toRegex(), "")}"
            val subCommitDir = File(commitDir, commitId)

            if (!subCommitDir.exists()) {
                subCommitDir.mkdir()
            }
            if (!logFile.exists()) {
                logFile.createNewFile()
            }

            if (logFile.readText().isEmpty()) {
                copyFilesToCommitDir(indexLines, subCommitDir)
                val username = usernameFile.readText()
                logFile.writeText("commit $commitId\nAuthor: $username\n$commitMessage")
                println("Changes are committed.")
            } else {
                if (!areSomeFilesDifferent(indexLines)) {
                    println("Nothing to commit.")
                } else {
                    copyFilesToCommitDir(indexLines, subCommitDir)
                    val oldText = logFile.readText()
                    val username = usernameFile.readText()
                    logFile.writeText("commit $commitId\nAuthor: $username\n$commitMessage")
                    logFile.appendText('\n' + oldText)
                    println("Changes are committed.")
                }
            }
        }
    }
}


//checkout function: copy files from a specific commit to working directory
fun checkout(args: Array<String>) {
    when (args.size) {
        1 -> println("Commit id was not passed.")
        2 -> {
            val commitId = args[1]
            val subCommitDir = File(commitDir, "$commitId")
            val wrkDir = File(System.getProperty("user.dir"))
            if (subCommitDir.exists()) {
                subCommitDir.copyRecursively(wrkDir, true)
                println("Switched to commit $commitId.")
            } else {
                println("Commit does not exist.")
            }
        }
    }
}

fun main(args: Array<String>) {
    val input = args.firstOrNull()?.toString() ?: ""
    vcsDir.mkdir()

    when (input) {
        "", "--help" -> help() //Also calls the help function if input is empty
        "config" -> config(args)
        "add" -> add(args)
        "log" -> log()
        "commit" -> commit(args)
        "checkout" -> checkout(args)
        else -> println("'$input' is not a SVCS command.")
    }
}



