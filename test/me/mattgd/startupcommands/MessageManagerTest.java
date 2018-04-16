package me.mattgd.startupcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@link me.mattgd.startupcommands.MessageManager} class.
 *
 * @author mattgd
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { CommandSender.class })
public class MessageManagerTest {

    private static MessageManager msgManager = MessageManager.getInstance();

    /**
     * Tests the {@link MessageManager#info(CommandSender, String)} method.
     */
    @Test
    public void testInfo() {
        CommandSender sender = Mockito.mock(CommandSender.class);

        String msg = "This is a test.";
        msgManager.info(sender, msg);
        verify(sender).sendMessage("§e" + msg);

        msg = "This is a &dtest.";
        msgManager.info(sender, msg);
        verify(sender).sendMessage("§eThis is a §dtest.");
    }

    /**
     * Tests the {@link MessageManager#good(CommandSender, String)} method.
     */
    @Test
    public void testGood() {
        CommandSender sender = Mockito.mock(CommandSender.class);

        String msg = "This is a test.";
        msgManager.good(sender, msg);
        verify(sender).sendMessage("§a" + msg);

        msg = "This is a &btest.";
        msgManager.good(sender, msg);
        verify(sender).sendMessage("§aThis is a §btest.");
    }

    /**
     * Tests the {@link MessageManager#severe(CommandSender, String)} method.
     */
    @Test
    public void testSevere() {
        CommandSender sender = Mockito.mock(CommandSender.class);

        String msg = "This is a test.";
        msgManager.severe(sender, msg);
        verify(sender).sendMessage("§c" + msg);

        msg = "This is a &atest.";
        msgManager.severe(sender, msg);
        verify(sender).sendMessage("§cThis is a §atest.");
    }

    /**
     * Tests the {@link me.mattgd.startupcommands.MessageManager#assembleMessage(String[], int, int)} method.
     */
    @Test
    public void assembleMessage() {
        String[] args = { "add", "5", "say", "This", "is", "a", "test."};
        String assembledMsg = msgManager.assembleMessage(args, 2, args.length);
        assertEquals("say This is a test.", assembledMsg);

        args = new String[] { "add", "say", "This", "is", "a", "test."};
        assembledMsg = msgManager.assembleMessage(args, 1, args.length);
        assertEquals("say This is a test.", assembledMsg);
    }

    /**
     * Tests the {@link me.mattgd.startupcommands.MessageManager#messageTitle(String, ChatColor, ChatColor)} method.
     */
    @Test
    public void messageTitle() {
        String title = msgManager.messageTitle("Test Title", ChatColor.RED, ChatColor.GREEN);
        assertEquals("§a---------------------[§cTest Title§a]---------------------", title);
    }

    /**
     * Tests the {@link me.mattgd.startupcommands.MessageManager#messageTrail(ChatColor)} method.
     */
    @Test
    public void messageTrail() {
        String title = msgManager.messageTrail(ChatColor.GREEN);
        assertEquals("\n§a-----------------------------------------------------", title);
    }

}