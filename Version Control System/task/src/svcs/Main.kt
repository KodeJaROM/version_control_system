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
        val track = args[1]
        if (!File(track).exists()) {
            println("Can't find '$track'.")
        } else {
            if (!indexFile.exists()) {
                indexFile.createNewFile()
            }
            indexFile.appendText("$track\n")
            println("The file '$track' is tracked.")
        }

//  Implementing the log command: If log text exists print it, otherwise print "No commits yet."
    } else if (input == "log") {
        if (logFile.exists()) {
            val log = logFile.readText()
            println(log)
        } else {
            println("No commits yet.")
        }
// Implementing the commit command
    } else if () {

    } else {
        println("'$input' is not a SVCS command.")
    }
}
