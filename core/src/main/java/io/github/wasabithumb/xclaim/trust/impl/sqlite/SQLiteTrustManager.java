package io.github.wasabithumb.xclaim.trust.impl.sqlite;

import io.github.wasabithumb.xclaim.trust.impl.sql.SQLTrustManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SQLiteTrustManager extends SQLTrustManager {

    static {
        try {
            Class.forName("org.sqlite.JDBC", true, Thread.currentThread().getContextClassLoader());
        } catch (ReflectiveOperationException ignored) { }
    }

    private final File file;
    public SQLiteTrustManager(@NotNull File file, @NotNull Logger logger) {
        super(logger);
        this.file = file;
    }

    @Override
    protected @NotNull Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
    }

}
