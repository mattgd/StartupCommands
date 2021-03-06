package me.mattgd.startupcommands.utils;

import me.mattgd.startupcommands.utils.TestLogFormatter;

import java.io.File;
import java.util.logging.*;

public class Util {

    private Util() {}

    public static final Logger logger = Logger.getLogger("SC-Test");

    static {
        logger.setUseParentHandlers(false);

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new TestLogFormatter());
        Handler[] handlers = logger.getHandlers();

        for (Handler h : handlers)
            logger.removeHandler(h);

        logger.addHandler(handler);
    }

    public static void log(Throwable t) {
        log(Level.WARNING, t.getLocalizedMessage(), t);
    }

    public static void log(Level level, Throwable t) {
        log(level, t.getLocalizedMessage(), t);
    }

    public static void log(String message, Throwable t) {
        log(Level.WARNING, message, t);
    }

    public static void log(Level level, String message, Throwable t) {
        LogRecord record = new LogRecord(level, message);
        record.setThrown(t);
        logger.log(record);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }

        return directory.delete();
    }

}