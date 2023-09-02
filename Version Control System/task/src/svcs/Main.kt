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

    //log function: If log text exists print it, otherwise print "No commits yet."
    fun log() {
        if (logFile.exists()) {
            val log = logFile.readText()
            println(log)
        } else {
            println("No commits yet.")
        }
    }

    fun main(args: Array<String>) {
        val input = args.firstOrNull()?.toString() ?: ""
        vcsDir.mkdir()

        if (input.isEmpty() || input == "--help") {
            help()

        } else if (input == "config") {
            config(args)

        } else if (input == "add") {
            add(args)

        } else if (input == "log") {
            log()
// Implementing the commit command and making sure a message is entered
        } else if (args[0] == "commit" && args.size == 1) {
            println("Message was not passed.")
        } else if (args[0] == "commit" && args.size == 2) {
            val indexLines = indexFile.readLines()
            val commitMessage = args[1]
//        val commitId = "ID-$commitMessage"
            val commitId = "ID-${commitMessage.replace("\\s".toRegex(), "")}"
            val subCommitDir = File(commitDir, "$commitId")
            if (!subCommitDir.exists()) {
                subCommitDir.mkdir()
            }
//  creating the log file
            if (!logFile.exists()) {
                logFile.createNewFile()
            }

//        Checking if the log file is empty, if it is copy the files to a new folder labeled with the commit ID
            if (logFile.readText().isEmpty()) {
//            create a folder in commits with commit id name
//            val subCommitDir = File(commitDir, "$commitId")
//            if (!subCommitDir.exists()) {
//                subCommitDir.mkdir()
//            }
                for (fileName in indexLines) {
                    val wrkDirFilePath = File(System.getProperty("user.dir"), fileName)
                    val wrkDirFile = File(wrkDirFilePath.absolutePath)
//                wrkDirFile.copyTo(subCommitDir, overwrite = true)
                    wrkDirFile.copyTo(File(subCommitDir, fileName), overwrite = true)
                }
                val username = usernameFile.readText()
                logFile.writeText("commit $commitId\nAuthor: $username\n$commitMessage")
                println("Changes are committed.")
            }
// Otherwise if the log file is not empty, check if the files have changed, if they have copy to a new folder with the commit ID and print "Changes are committed."
            else {
//        Checking if files are different
                var areSomeFilesDifferent = false

//            This reads the first line in the log file and assigns it to the value preCommitId
                val prevCommitId = logFile.readLines().firstOrNull()?.substringAfter("commit ")
                val prevSubCommitDir = File(commitDir, "$prevCommitId")


                for (fileName in indexLines) {
                    val wrkDirFilePath = File(System.getProperty("user.dir"), fileName)
                    val commitDirFilePath = File(prevSubCommitDir, fileName)

                    val wrkDirFile = File(wrkDirFilePath.absolutePath)
                    val commitDirFile = File(commitDirFilePath.absolutePath)

                    if (wrkDirFile.readText() != commitDirFile.readText()) {
                        areSomeFilesDifferent = true
                        break
                    }
                }
                if (areSomeFilesDifferent == false) {
                    println("Nothing to commit.")
                } else {
//                create a folder in commits with commit id name
                    val subCommitDir = File(commitDir, "$commitId")
//                if (!subCommitDir.exists()) {
//                    subCommitDir.mkdir()
//                }
                    for (fileName in indexLines) {
                        val wrkDirFilePath = File(System.getProperty("user.dir"), fileName)
                        val wrkDirFile = File(wrkDirFilePath.absolutePath)
//                    wrkDirFile.copyTo(subCommitDir, overwrite = true)
                        wrkDirFile.copyTo(File(subCommitDir, fileName), overwrite = true)
                    }
                    val oldText = logFile.readText()
                    val username = usernameFile.readText()
                    logFile.writeText("commit $commitId\nAuthor: $username\n$commitMessage\n")
                    logFile.appendText(oldText + '\n')
                    println("Changes are committed.")
                }
            }
        } else if (args[0] == "checkout" && args.size == 1) {
            println("Commit id was not passed.")
        } else if (args[0] == "checkout" && args.size == 2) {
            val commitId = args[1]
            val subCommitDir = File(commitDir, "$commitId")
            val wrkDir = File(System.getProperty("user.dir"))
            if (subCommitDir.exists()) {
                subCommitDir.copyRecursively(wrkDir, true)
                println("Switched to commit $commitId.")
            } else {
                println("Commit does not exist.")
            }

        } else {
            println("'$input' is not a SVCS command.")
        }
    }
}


