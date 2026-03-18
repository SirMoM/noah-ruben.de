package de.noah_ruben

import de.noah_ruben.misc.Commands
import de.noah_ruben.misc.parseCommand
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandLineEmulationKtTest {

    @Test
    fun testParseCommand() {
        var input = "command=noahruben%20projects"

        var result = parseCommand(input)
        assertEquals(Commands.projects, result.first)
        assertEquals(emptyList<String>(), result.second)

        input = "command=noahruben%20cv"

        result = parseCommand(input)
        assertEquals(Commands.cv, result.first)
        assertEquals(emptyList<String>(), result.second)

        input = "command=noahruben%20error"

        result = parseCommand(input)
        assertEquals(Commands.unknownSubpage, result.first)
        assertEquals(emptyList<String>(), result.second)

        input = "command=noahruben"
        result = parseCommand(input)
        assertEquals(Commands.landingPage, result.first)
        assertEquals(emptyList<String>(), result.second)

        input = "error"
        result = parseCommand(input)
        assertEquals(Commands.unknownCommand, result.first)
        assertEquals(emptyList<String>(), result.second)

        input = "command=noahruben%20cv ger"
        result = parseCommand(input)
        assertEquals(Commands.cv, result.first)
        assertEquals(listOf("ger"), result.second)
    }
}
