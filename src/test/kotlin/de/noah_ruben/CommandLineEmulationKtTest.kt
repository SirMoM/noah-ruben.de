package de.noah_ruben

import de.noah_ruben.misc.Commands
import de.noah_ruben.misc.parseCommand
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class CommandLineEmulationKtTest {

    @Test
    fun testParseCommand() {
        var input = "command=noahruben%20projects"

        var result = parseCommand(input)
        assertEquals(result, Commands.projects)

        input = "command=noahruben%20cv"

        result = parseCommand(input)
        assertEquals(result, Commands.cv)

        input = "command=noahruben%20error"

        result = parseCommand(input)
        assertEquals(result, Commands.unknownSubpage)

        input = "command=noahruben"
        result = parseCommand(input)
        assertEquals(result, Commands.landingPage)

        input = "error"
        result = parseCommand(input)
        assertEquals(result, Commands.unknownCommand)
    }
}
