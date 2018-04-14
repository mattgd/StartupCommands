package me.mattgd.startupcommands;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the {@link me.mattgd.startupcommands.StartupCommands} class.
 *
 * @author mattgd
 */
public class StartupCommandsTest {

    /**
     * Tests the {@link me.mattgd.startupcommands.StartupCommands#isInteger(String)} method.
     */
    @Test
    public void isInteger() {
        String notInt = "not an int";
        assertFalse(StartupCommands.isInteger(notInt));

        String anInt = "1452";
        assertTrue(StartupCommands.isInteger(anInt));

        String doubleNotInt = "15.23";
        assertFalse(StartupCommands.isInteger(doubleNotInt));

        String notQuiteInt = "15.00";
        assertFalse(StartupCommands.isInteger(notQuiteInt));
    }
}