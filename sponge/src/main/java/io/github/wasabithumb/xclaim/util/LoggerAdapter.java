package io.github.wasabithumb.xclaim.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public final class LoggerAdapter extends Handler {

    public static java.util.logging.Logger wrap(@NotNull Logger log4j) {
        String name = log4j.getName();
        java.util.logging.Logger ret = name == null ? java.util.logging.Logger.getAnonymousLogger() :
                java.util.logging.Logger.getLogger(name);

        for (Handler h : ret.getHandlers()) ret.removeHandler(h);
        ret.addHandler(new LoggerAdapter(log4j));

        return ret;
    }

    //

    private final Logger out;
    LoggerAdapter(@NotNull Logger out) {
        this.out = out;
    }

    //

    @Override
    public void publish(@NotNull LogRecord logRecord) {
        Level level = this.adaptLevel(logRecord.getLevel());
        String message = logRecord.getMessage();
        Throwable thrown = logRecord.getThrown();
        if (thrown == null) {
            this.out.log(level, message);
        } else {
            this.out.log(level, message, thrown);
        }
    }

    @Override
    public void flush() { }

    @Override
    public void close() { }

    //

    private @NotNull Level adaptLevel(@NotNull java.util.logging.Level level) {
        int value = level.intValue();
        switch (value / 100) {
            case 3: // FINEST
            case 4: // FINER
                return Level.TRACE;
            case 5: // FINE
            case 7: // CONFIG
                return Level.DEBUG;
            case 8: // INFO
                return Level.INFO;
            case 9: // WARNING
                return Level.WARN;
            case 10: // SEVERE
                return Level.FATAL;
        }
        if (value > 800) return Level.OFF;
        return Level.ALL;
    }

}
