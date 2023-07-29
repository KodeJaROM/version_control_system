package svcs

import java.io.File


//Creating a map with the SVCS commands
val helpList = mapOf(
    "config" to "Get and set a username.",
    "add" to "Add a file to the index.",
    "log" to "Show commit logs.",
    "commit" to "Save changes.",
    "checkout" to "Restore a file.",
)


fun main(args: Array<String>) {
    val input = args.firstOrNull()?.toString() ?: ""
    val vcsDir = File(System.getProperty("user.dir"), "vcs")
    vcsDir.mkdir()

    val usernameFile = File(vcsDir, "config.txt")
    val indexFile = File(vcsDir, "index.txt")
    val logFile = File(vcsDir, "log.txt")
    val commitDir = File(vcsDir, "commits")

    if (input.isEmpty() || input == "--help") {
        println("These are SVCS commands:")
        for ((key, value) in helpList)
            println("$key $value")
//        This else if will need to be removed after implementing the last command
    } else if (helpList.containsKey(input) && input == "checkout") {
        println(helpList[input])

//  Implementing the config command and creating a vcs folder to store username
    } else if (input == "config" && args.size == 1) {
        if (usernameFile.exists()) {
            val username = usernameFile.readText()
            println("The username is $username.")
        } else {
            println("Please, tell me who you are.")
        }
    } else if (args[0] == "config" && args.size == 2) {
        val username = args[1]
        usernameFile.writeText(username)
        println("The username is $username.")

//        Implementing the add command and creating an index
    } else if (input == "add" && args.size == 1) {
        if (indexFile.exists()) {
            val index = indexFile.readText()
            println("Tracked files:\n$index")
        } else {
            println("Add a file to the index.")
        }
    } else if (args[0] == "add" && args.size == 2) {
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

//  Implementing the log command: If log text exists print it, otherwise print "No commits yet."
    } else if (input == "log") {
        if (logFile.exists()) {
            val log = logFile.readText()
            println(log)
        } else {
            println("No commits yet.")
        }
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
                logFile.writeText("commit $commitId\nAuthor: $username\n$commitMessage")
                logFile.appendText(oldText + '\n')
                println("Changes are committed.")
            }
        }



    } else {
        println("'$input' is not a SVCS command.")
    }
}
