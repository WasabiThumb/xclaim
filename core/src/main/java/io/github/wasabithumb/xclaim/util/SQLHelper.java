package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A one-file convenience class to reduce boilerplate related to PreparedStatement and connection pools.
 */
public abstract class SQLHelper<S extends SQLHelper.Statements> implements AutoCloseable {

    protected final Logger logger;
    private final Set<Context<S>> contextSet;
    private transient ThreadLocal<Context<S>> contextLocal;
    private final ExecutorService executor;

    protected SQLHelper(@NotNull Logger logger) {
        this.logger = logger;
        this.contextSet = Collections.synchronizedSet(new HashSet<>());
        this.contextLocal = new ThreadLocal<>();
        this.executor = this.newExecutor();
    }

    protected abstract @NotNull S newStatements();

    protected @NotNull ExecutorService newExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    protected abstract @NotNull Connection openConnection() throws SQLException;

    protected void raise(@NotNull SQLException e) {
        this.logger.log(Level.WARNING, "Unexpected database error", e);
    }

    @Contract("_ -> fail")
    protected void raiseFatal(@NotNull Throwable t) {
        throw new AssertionError("Fatal error while executing database operation", t);
    }

    private @NotNull S use() throws SQLException {
        Context<S> ctx = this.contextLocal.get();
        if (ctx == null || ctx.connection.isClosed()) {
            Connection c = this.openConnection();
            S s = this.newStatements();
            s.prepare(c);
            ctx = new Context<>(c, s);
            this.contextLocal.set(ctx);
            this.contextSet.add(ctx);
        }
        return ctx.statements;
    }

    protected <R> @UnknownNullability R use(final @NotNull Fn1<S, R> fn, @NotNull Supplier<R> fallback) {
        Future<R> future = this.executor.submit(() -> fn.execute(this.use()));
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException s) {
                this.raise(s);
                return fallback.get();
            }
            this.raiseFatal(e);
        } catch (InterruptedException e) {
            this.raiseFatal(e);
        }
        return null;
    }

    protected void use(final @NotNull Fn2<S> fn) {
        this.use((S statements) -> {
            fn.execute(statements);
            return (Void) null;
        }, () -> null);
    }

    protected void submit(final @NotNull Fn2<S> fn) {
        this.executor.submit(() -> {
            fn.execute(this.use());
            return (Void) null;
        });
    }

    @Override
    public void close() throws SQLException {
        synchronized (this.contextSet) {
            for (Context<S> ctx : this.contextSet) {
                ctx.close();
            }
            this.contextSet.clear();
        }
        this.contextLocal = null;
        this.executor.shutdown();
        if (this.executor.isTerminated()) return;

        boolean terminated = false;
        Throwable cause = null;
        try {
            terminated = this.executor.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            cause = e;
        }
        if (!terminated)
            this.logger.log(Level.WARNING, "Timed out waiting for database operations to finish", cause);
    }

    //

    protected interface Statements {
        void prepare(@NotNull Connection connection) throws SQLException;
        void close() throws SQLException;
    }

    private record Context<S extends SQLHelper.Statements>(
            @NotNull Connection connection,
            @NotNull S statements
    ) implements AutoCloseable {
        @Override
        public void close() throws SQLException {
            try {
                this.statements.close();
            } finally {
                this.connection.close();
            }
        }
    }

    @FunctionalInterface
    protected interface Fn1<S extends SQLHelper.Statements, R> {
        R execute(@NotNull S statements) throws SQLException;
    }

    @FunctionalInterface
    protected interface Fn2<S extends SQLHelper.Statements> {
        void execute(@NotNull S statements) throws SQLException;
    }

}
