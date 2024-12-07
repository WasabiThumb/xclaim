package io.github.wasabithumb.xclaim.claim.data.impl.sqlite;

import io.github.wasabithumb.xclaim.claim.data.impl.sql.SQLClaimDataManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SQLiteClaimDataManager extends SQLClaimDataManager {

    static {
        try {
            Class.forName("org.sqlite.JDBC", true, Thread.currentThread().getContextClassLoader());
        } catch (ReflectiveOperationException ignored) { }
    }

    private final File file;
    public SQLiteClaimDataManager(@NotNull File file, @NotNull Logger logger) {
        super(logger);
        this.file = file;
    }

    @Override
    protected @NotNull Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
    }

}
