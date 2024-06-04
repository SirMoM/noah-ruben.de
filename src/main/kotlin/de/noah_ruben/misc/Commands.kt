package de.noah_ruben.misc

import java.net.URLDecoder
import java.nio.charset.Charset

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
fun parseCommand(rawCommand: String): Commands {
    var commandStr: String = URLDecoder.decode(rawCommand, Charset.defaultCharset())

    if (commandStr == "command=noahruben") {
        return Commands.landingPage
    }

    commandStr = commandStr.replace("command=noahruben", "").trim()

    if (!rawCommand.contains("noahruben")) {
        return Commands.unknownCommand
    }

    return try {
        Commands.valueOf(commandStr)
    } catch (err: IllegalArgumentException) {
        Commands.unknownSubpage
    }
}
