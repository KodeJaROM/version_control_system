package svcs


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
    if (input.isEmpty() || input == "--help") {
        println("These are SVCS commands:")
        for ((key, value) in helpList)
            println("$key $value")
    } else if (helpList.containsKey(input)) {
        println(helpList[input])
    } else {
        println("'$input' is not a SVCS command.")
    }
}