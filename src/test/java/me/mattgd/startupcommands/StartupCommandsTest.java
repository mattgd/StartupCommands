package me.mattgd.startupcommands;

import me.mattgd.startupcommands.utils.Util;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Tests the {@link me.mattgd.startupcommands.StartupCommands} class.
 *
 * @author mattgd
 */
@RunWith(PowerMockRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PrepareForTest({ Bukkit.class, CommandSender.class, PluginDescriptionFile.class, PluginCommand.class, JavaPlugin.class, JavaPluginLoader.class, StartupCommands.class })
public class StartupCommandsTest {

    private StartupCommands plugin;
    private Server mockServer;
    private CommandSender commandSender;
    private PluginCommand mockCommand;

    private static Logger logger = Logger.getLogger(StartupCommands.class.getName());
    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;

    private static final File pluginDirectory = new File("bin/test/server/plugins/sctest");
    private static final File serverDirectory = new File("bin/test/server");

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

    @Before
    public void setUp() {
        try {
            pluginDirectory.mkdirs();
            assertTrue(pluginDirectory.exists());

            MockGateway.MOCK_STANDARD_METHODS = false;

            // Initialize the Mock server.
            mockServer = mock(Server.class);
            JavaPluginLoader mockPluginLoader = mock(JavaPluginLoader.class);
            Whitebox.setInternalState(mockPluginLoader, "server", mockServer);
            when(mockServer.getName()).thenReturn("TestBukkit");
            Logger.getLogger("Minecraft").setParent(Util.logger);
            when(mockServer.getLogger()).thenReturn(Util.logger);

            // Return a fake PDF file.
            PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile(
                    "StartupCommands", "0.0.6-Test",
                    "me.mattgd.startupcommands.StartupCommands"));
            when(pdf.getAuthors()).thenReturn(new ArrayList<String>());
            plugin = PowerMockito.spy(new StartupCommands(mockPluginLoader, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));

            // Make all StartupCommands files go to bin/test
            doReturn(pluginDirectory).when(plugin).getDataFolder();

            doReturn(true).when(plugin).isEnabled();
            doReturn(Util.logger).when(plugin).getLogger();

            // Add StartupCommands to the list of loaded plugins
            JavaPlugin[] plugins = new JavaPlugin[] { plugin };

            // Mock the Plugin Manager
            PluginManager mockPluginManager = mock(PluginManager.class);
            when(mockPluginManager.getPlugins()).thenReturn(plugins);
            when(mockPluginManager.getPlugin("StartupCommands")).thenReturn(plugin);
            when(mockPluginManager.getPermission(anyString())).thenReturn(null);

            when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

            // add mock scheduler
            BukkitScheduler mockScheduler = mock(BukkitScheduler.class);
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class), anyLong())).
                    thenAnswer(new Answer<Integer>() {
                        public Integer answer(InvocationOnMock invocation) {
                            Runnable arg;

                            try {
                                arg = (Runnable) invocation.getArguments()[1];
                            } catch (Exception e) {
                                return null;
                            }

                            arg.run();
                            return null;
                        }});

            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class))).
                    thenAnswer(new Answer<Integer>() {
                        public Integer answer(InvocationOnMock invocation) {
                            Runnable arg;

                            try {
                                arg = (Runnable) invocation.getArguments()[1];
                            } catch (Exception e) {
                                return null;
                            }

                            arg.run();
                            return null;
                        }});
            when(mockServer.getScheduler()).thenReturn(mockScheduler);

            // Set server
            Field serverField = JavaPlugin.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(plugin, mockServer);

            // Init our command sender
            final Logger commandSenderLogger = Logger.getLogger("CommandSender");
            commandSenderLogger.setParent(Util.logger);
            commandSender = mock(CommandSender.class);
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) {
                    commandSenderLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
                    return null;
                }}).when(commandSender).sendMessage(anyString());
            when(commandSender.getServer()).thenReturn(mockServer);
            when(commandSender.getName()).thenReturn("MockCommandSender");
            when(commandSender.isPermissionSet(anyString())).thenReturn(true);
            when(commandSender.isPermissionSet(ArgumentMatchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.hasPermission(anyString())).thenReturn(true);
            when(commandSender.hasPermission(ArgumentMatchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.addAttachment(plugin)).thenReturn(null);
            when(commandSender.isOp()).thenReturn(true);

            Bukkit.setServer(mockServer);

            // Initialize a fake command
            mockCommand = mock(PluginCommand.class);
            when(mockCommand.getName()).thenReturn("startup");
            doReturn(mockCommand).when(plugin).getCommand(ArgumentMatchers.anyString());

            plugin.onEnable(); // Enable the plugin
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregisters the server and disables the plugin.
     */
    @After
    public void tearDown() {
        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        } catch (Exception e) {
            Util.log(Level.SEVERE, "An error occurred while trying to unregister the server from Bukkit.");
            e.printStackTrace();
            fail(e.getMessage());
        }

        plugin.onDisable();

        Util.deleteDirectory(serverDirectory);
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for the view sub-command.
     */
    @Test
    public void testOnCommand1View() {
        String[] args = new String[] { "view" };
        String msgStr = "§e§e------------------[§bStartup Commands§e]------------------\n" +
                "§e1 §7- §agamemode 1 TestPlayer §7(10s delay)\n" +
                "§e2 §7- §aban TestPlayer §7(0s delay)\n" +
                "§e3 §7- §asay This is a test message with StartupCommands §7(3s delay)\n" +
                "§e-----------------------------------------------------";

        plugin.onCommand(commandSender, mockCommand, "startup", args);
        verify(commandSender).sendMessage(msgStr);
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for the view sub-command.
     */
    @Test
    public void testOnCommand2Add() {
        String[] args = new String[] { "add", "ban", "APlayer" };
        String msgStr = "§eAdded startup command with delay §72s§e: §aban APlayer";

        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "create", "ban", "APlayer" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        verify(commandSender, times(2)).sendMessage(msgStr);

        args = new String[] { "add", "17", "ban", "AnotherPlayer" };
        msgStr = "§eAdded startup command with delay §717s§e: §aban AnotherPlayer";
        plugin.onCommand(commandSender, mockCommand, "startup", args);
        verify(commandSender).sendMessage(msgStr);
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for the view sub-command.
     */
    @Test
    public void testOnCommand2Remove() {
        // Add the commands to remove
        String[] args = new String[] { "add", "say", "Test1" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "add", "say", "Test2" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        // Run remove commands
        args = new String[] { "remove", "say", "Test1" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "delete", "say", "Test2" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "remove", "2" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "remove", "tp", "notacommand" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "remove", "-5" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(commandSender, times(7)).sendMessage(captor.capture());

        // Check to see if sendMessage parameters were correct
        List<String> paramsPassed = captor.getAllValues();
        assertEquals("§eRemoved startup command: §asay Test1", paramsPassed.get(2));
        assertEquals("§eRemoved startup command: §asay Test2", paramsPassed.get(3));
        assertEquals("§eRemoved startup command: §aban TestPlayer", paramsPassed.get(4));
        assertEquals("§cNo command found matching tp notacommand.", paramsPassed.get(5));
        assertEquals("§cInvalid command index.", paramsPassed.get(6));
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for the view sub-command.
     */
    @Test
    public void testOnCommand3SetDelay() {
        String[] args = new String[] { "setdelay", "1", "14" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "setdelay", "6" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "setdelay", "6", "Test2" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "setdelay", "Test", "6" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "setdelay", "Test1", "Test2" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "setdelay", "-5", "5" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(commandSender, times(6)).sendMessage(captor.capture());

        // Check to see if sendMessage parameters were correct
        List<String> paramsPassed = captor.getAllValues();
        assertEquals("§eSet the delay for command §7#1 §eto §714s§e.", paramsPassed.get(0));
        assertEquals("§cUsage: /sc setdelay <command ID> <delay in seconds>", paramsPassed.get(1));
        assertEquals("§cUsage: /sc setdelay <command ID> <delay in seconds>", paramsPassed.get(2));
        assertEquals("§cUsage: /sc setdelay <command ID> <delay in seconds>", paramsPassed.get(3));
        assertEquals("§cUsage: /sc setdelay <command ID> <delay in seconds>", paramsPassed.get(4));
        assertEquals("§cInvalid command index.", paramsPassed.get(5));
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for the run sub-command.
     */
    @Test
    public void testOnCommand3Run() {
        when(mockServer.getLogger()).thenReturn(logger);

        String[] args = new String[] { "run" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        final String expectedLogPart = "Queuing 3 startup commands.";
        String capturedLog = getTestCapturedLog();

        assertTrue(capturedLog.contains(expectedLogPart));
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for an invalid sub-command.
     */
    @Test
    public void testOnCommand3Invalid() {
        String[] args = new String[] { "notarealcommand" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "notarealcommand", "nothing", "5" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        String msgStr = "§cInvalid command usage. Type /startup help for proper usage information.";
        verify(commandSender, times(2)).sendMessage(msgStr);
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for the view sub-command.
     */
    @Test
    public void testOnCommand4ViewEmpty() {
        // Remove all commands
        String[] args = new String[]{ "remove", "3" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[]{ "remove", "2" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[]{ "remove", "1" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[]{ "view" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(commandSender, times(4)).sendMessage(captor.capture());

        // Check to see if sendMessage parameters were correct
        List<String> paramsPassed = captor.getAllValues();
        assertEquals("§eThere are currently no startup commands configured.", paramsPassed.get(3));
    }

    /**
     * Tests the {@link StartupCommands#onCommand(CommandSender, Command, String, String[])}
     * method for the /startup (empty) sub-command.
     */
    @Test
    public void testOnCommand4Help() {
        String[] args = {};
        String msgStr = "§a§e----------------[§bStartupCommands Help§e]----------------"
                + "\n§a/sc view §7- §aview the active startup commands and their delay"
                + "\n§a/sc add <command string> <delay> §7- §aadd a startup command"
                + "\n§a/sc remove <command ID or exact command string> §7- §aremove a startup command"
                + "\n§a/sc setdelay <command ID> <delay in seconds> §7- §aset a startup command's delay"
                + "\n§e-----------------------------------------------------";

        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "help" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        args = new String[] { "?" };
        plugin.onCommand(commandSender, mockCommand, "startup", args);

        verify(commandSender, times(3)).sendMessage(msgStr);
    }

    /**
     * Tests the {@link me.mattgd.startupcommands.StartupCommands#isInteger(String)} method.
     */
    @Test
    public void testIsInteger() {
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