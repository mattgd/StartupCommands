package me.mattgd.startupcommands;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the {@link me.mattgd.startupcommands.MessageManager} class.
 *
 * @author mattgd
 */
public class MessageManagerTest {

    /**
     * Tests the {@link me.mattgd.startupcommands.MessageManager#assembleMessage(String[], int, int)} method.
     */
    @Test
    public void assembleMessage() {
        MessageManager msgManager = MessageManager.getInstance();

        String[] args = { "add", "5", "say", "This", "is", "a", "test."};
        String assembledMsg = msgManager.assembleMessage(args, 2, args.length);
        assertEquals("say This is a test.", assembledMsg);

        args = new String[] { "add", "say", "This", "is", "a", "test."};
        assembledMsg = msgManager.assembleMessage(args, 1, args.length);
        assertEquals("say This is a test.", assembledMsg);
    }
}