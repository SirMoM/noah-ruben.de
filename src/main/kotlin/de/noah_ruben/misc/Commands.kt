package de.noah_ruben.misc

import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.Collections.emptyList

@Suppress("EnumEntryName")
enum class Commands {
    projects,
    landingPage,
    cv,
    unknownSubpage,
    unknownCommand,
    help,
}

@Throws(IllegalArgumentException::class)
fun parseCommand(rawCommand: String): Pair<Commands, List<String>> {
    var commandStr: String = URLDecoder.decode(rawCommand, Charset.defaultCharset())

    if (commandStr == "command=noahruben") {
        return Commands.landingPage to emptyList()
    }

    commandStr = commandStr.replace("command=noahruben", "").trim()

    if (!rawCommand.contains("noahruben")) {
        return Commands.unknownCommand to emptyList()
    }

    return try {
        val (command, agruments) = if (commandStr.contains(" ")) {
            commandStr.split(" ").let { it[0] to it.drop(1) }
        } else {
            commandStr to emptyList<String>()
        }
        Commands.valueOf(command) to agruments
    } catch (err: IllegalArgumentException) {
        Commands.unknownSubpage to emptyList<String>()
    }
}
