package codes.wasabi.xclaim.debug.goal;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.debug.writer.DebugWriter;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.metric.Metric;
import codes.wasabi.xclaim.util.metric.MetricFormatter;
import codes.wasabi.xclaim.util.metric.MetricSet;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@ApiStatus.Internal
public class DebugGoalInstance {

    private static final int F_ASYNC          = 1;
    private static final int F_ACCEPTS_WRITER = 2;
    private static final int F_RETURNS_VOID   = 4;

    private static final MetricFormatter TIMESTAMP_FORMAT = new MetricFormatter(
            MetricSet.builder().upTo(Metric.UNIT).build()
    );

    public static @NotNull @Unmodifiable List<DebugGoalInstance> findInClass(@NotNull Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        Method tmp;

        int staticCount = 0;
        for (int i=0; i < methods.length; i++) {
            tmp = methods[i];
            if (!Modifier.isStatic(tmp.getModifiers())) continue;
            if (i != staticCount) methods[staticCount] = tmp;
            staticCount++;
        }
        if (staticCount == 0) return Collections.emptyList();

        final String labelBase = clazz.getSimpleName();
        DebugGoalInstance[] instances = new DebugGoalInstance[staticCount];
        int instanceCount = 0;

        DebugGoal annotation;
        String label;
        Class<?>[] params;
        boolean acceptsWriter;
        for (int i=0; i < staticCount; i++) {
            tmp = methods[i];
            annotation = tmp.getAnnotation(DebugGoal.class);
            if (annotation == null) continue;

            try {
                tmp.setAccessible(true);
            } catch (Exception ignored) { }

            label = labelBase + "." + tmp.getName();

            params = tmp.getParameterTypes();
            acceptsWriter = false;
            if (params.length != 0) {
                if (params.length == 1 && params[0].isAssignableFrom(DebugWriter.class)) {
                    acceptsWriter = true;
                } else {
                    XClaim.logger.log(Level.WARNING, "DebugGoal (" + label + ") has malformed signature");
                    continue;
                }
            }

            instances[instanceCount++] = new DebugGoalInstance(
                    label,
                    tmp,
                    annotation.async(),
                    acceptsWriter,
                    tmp.getReturnType().equals(Void.TYPE)
            );
        }

        if (instanceCount == 0) return Collections.emptyList();

        List<DebugGoalInstance> ret;
        if (instanceCount < Math.floorDiv(staticCount * 3, 4)) { // Shrink if > 25% inefficient
            DebugGoalInstance[] cpy = new DebugGoalInstance[instanceCount];
            System.arraycopy(instances, 0, cpy, 0, instanceCount);
            ret = Arrays.asList(cpy);
        } else {
            ret = Arrays.asList(instances).subList(0, instanceCount);
        }
        return ret;
    }

    //

    private final String label;
    private final Method method;
    private final byte flags;
    DebugGoalInstance(@NotNull String label, @NotNull Method method, boolean async, boolean acceptsWriter, boolean returnsVoid) {
        this.label = label;
        this.method = method;
        int flags = 0;
        if (async) flags |= F_ASYNC;
        if (acceptsWriter) flags |= F_ACCEPTS_WRITER;
        if (returnsVoid) flags |= F_RETURNS_VOID;
        this.flags = (byte) flags;
    }

    public @NotNull String label() {
        return this.label;
    }

    public @NotNull Method method() {
        return this.method;
    }

    public boolean async() {
        return (Byte.toUnsignedInt(this.flags) & F_ASYNC) != 0;
    }

    public boolean acceptsWriter() {
        return (Byte.toUnsignedInt(this.flags) & F_ACCEPTS_WRITER) != 0;
    }

    public boolean returnsVoid() {
        return (Byte.toUnsignedInt(this.flags) & F_RETURNS_VOID) != 0;
    }

    public void execute(final @NotNull DebugWriter writer) {
        if (this.async()) {
            Platform.get().getScheduler().runTaskAsynchronously(XClaim.instance, () -> {
                this.execute0(writer);
            });
        } else {
            this.execute0(writer);
        }
    }

    private void execute0(@NotNull DebugWriter writer) {
        writer.color(NamedTextColor.WHITE);

        final long start = System.nanoTime();
        Object out;
        try {
            out = this.execute00(writer);
        } catch (InvocationTargetException e1) {
            Throwable cause = e1.getCause();
            writer.raise(cause != null ? cause : e1);
            return;
        } catch (Throwable t) {
            writer.raise(t);
            return;
        } finally {
            final long elapsed = System.nanoTime() - start;
            this.reportElapsed(writer, elapsed);
        }

        if (this.returnsVoid()) return;
        writer.color(NamedTextColor.GRAY);
        writer.println("> " + out);
    }

    private Object execute00(@NotNull DebugWriter writer) throws ReflectiveOperationException {
        Object[] args = this.acceptsWriter() ? new Object[] { writer } : new Object[0];
        return this.method.invoke(null, args);
    }

    private void reportElapsed(@NotNull DebugWriter writer, long elapsed) {
        writer.color(NamedTextColor.DARK_GRAY);
        writer.println("> Done in " + TIMESTAMP_FORMAT.format(elapsed * 1e-9d, "s"));
    }

}
