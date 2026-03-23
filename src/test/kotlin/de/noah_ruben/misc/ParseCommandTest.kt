package de.noah_ruben.misc

import kotlin.test.Test
import kotlin.test.assertEquals

class ParseCommandTest {

    @Test
    fun parsesKnownCommandsAndArguments() {
        var input = "command=noahruben%20projects"

        var result = parseCommand(input)
        assertEquals(Commands.projects, result.first)
        assertEquals(emptyList<String>(), result.second)

        input = "command=noahruben%20cv"
        result = parseCommand(input)
        assertEquals(Commands.cv, result.first)
        assertEquals(emptyList<String>(), result.second)

        input = "command=noahruben%20cv ger"
        result = parseCommand(input)
        assertEquals(Commands.cv, result.first)
        assertEquals(listOf("ger"), result.second)

        input = "command=noahruben%20cv eng"
        result = parseCommand(input)
        assertEquals(Commands.cv, result.first)
        assertEquals(listOf("eng"), result.second)
    }

    @Test
    fun mapsUnknownInputsToFallbackCommands() {
        var input = "command=noahruben%20error"

        var result = parseCommand(input)
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
    }
}
