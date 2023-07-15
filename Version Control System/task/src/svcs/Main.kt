package svcs

import java.io.File


//Creating a map with the SVCS commands
val helpList = mapOf(
//    "config" to "Get and set a username.",
    "add" to "Add a file to the index.",
    "log" to "Show commit logs.",
    "commit" to "Save changes.",
    "checkout" to "Restore a file.",
)


fun main(args: Array<String>) {
    val input = args.firstOrNull()?.toString() ?: ""
    val splitInput = input.split(" ")
    val vcsDir = File(System.getProperty("user.dir"), "vcs")
    vcsDir.mkdir()
    val usernameFile = File(vcsDir, "config.txt")

    if (input.isEmpty() || input == "--help") {
        println("These are SVCS commands:")
        for ((key, value) in helpList)
            println("$key $value")
    } else if (helpList.containsKey(input)) {
        println(helpList[input])

//  Implementing the config command and creating a vcs folder to store username and index
    } else if (splitInput[0] == "config" && splitInput.size == 1) {
        when {// TODO:  

        }






    } else {
        println("'$input' is not a SVCS command.")
    }
}