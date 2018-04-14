package me.mattgd.startupcommands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the {@link me.mattgd.startupcommands.Command} class.
 *
 * @author mattgd
 */
public class CommandTest {

    /**
     * Tests the {@link me.mattgd.startupcommands.Command} class.
     */
    @Test
    public void testCommand() {
        Command cmd = new Command("say This is a test.");

        assertEquals(cmd.getCommand(), "say This is a test.");
        assertEquals(cmd.getDelay(), 2);

        // Invalid command
        try {
            new Command(null);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Command string cannot be null or empty.", ex.getMessage());
        }
    }

}