package me.mattgd.startupcommands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link Command} class.
 *
 * @author mattgd
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { Bukkit.class, Server.class })
public class CommandTest {

    private static Logger logger = Logger.getLogger(Command.class.getName()); // matches the logger in the affected class
    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;

    /**
     * Attaches the test log capturer.
     */
    @Before
    public void attachLogCapturer() {
        logCapturingStream = new ByteArrayOutputStream();
        Handler[] handlers = logger.getParent().getHandlers();
        customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());
        logger.addHandler(customLogHandler);
    }

    /**
     * Gets the test log data and returns it.
     * @return a String representing the captured log data.
     */
    public String getTestCapturedLog() {
        customLogHandler.flush();
        return logCapturingStream.toString();
    }

    /**
     * Tests the {@link Command} class getters and settings.
     */
    @Test
    public void testCommand() {
        Command cmd = new Command("say This is a test.");

        assertEquals(cmd.getCommand(), "say This is a test.");
        assertEquals(cmd.getDelay(), 2);

        cmd.setDelay(5);
        assertEquals(5, cmd.getDelay());

        cmd = new Command("say This is a test.", 7);
        assertEquals("say This is a test.", cmd.getCommand());
        assertEquals(7, cmd.getDelay());

        // Invalid command
        try {
            new Command(null);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Command string cannot be null or empty.", ex.getMessage());
        }
    }

    /**
     * Tests the {@link Command#run()} method.
     */
    @Test
    public void testRun() {
        Command cmd = new Command("say This is a test.");
        PowerMockito.mockStatic(Bukkit.class);
        final Server server = Mockito.mock(Server.class);

        when(Bukkit.getServer()).thenReturn(server);
        when(server.getLogger()).thenReturn(logger);

        cmd.run();

        final String expectedLogPart = "[StartupCommands] Executing command: say This is a test.";
        String capturedLog = getTestCapturedLog();
        assertTrue(capturedLog.contains(expectedLogPart));
    }

}