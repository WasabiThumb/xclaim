package codes.wasabi.xclaim.debug;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.enums.EntityGroup;
import codes.wasabi.xclaim.debug.goal.DebugGoalInstance;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@ApiStatus.Internal
public final class Debug {

    // Maybe un-hardcode this if the debug system sees more use
    private static final Class<?>[] DEBUGGABLE = new Class<?>[] {
            EntityGroup.class
    };

    private static final File CODE_SOURCE;
    private static final boolean ENABLED;
    private static List<DebugGoalInstance> GOALS = null;
    static {
        CODE_SOURCE = new File(Debug.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        boolean enabled = false;
        final Pattern regex = Pattern.compile("^Enable-Debug:\\s*(true|false)$", Pattern.CASE_INSENSITIVE);
        try (ZipFile zf = new ZipFile(CODE_SOURCE)) {
            ZipEntry ze = zf.getEntry("META-INF/MANIFEST.MF");
            if (ze != null) {
                try (InputStream is = zf.getInputStream(ze);
                     InputStreamReader reader1 = new InputStreamReader(is, StandardCharsets.UTF_8);
                     BufferedReader reader2 = new BufferedReader(reader1)
                ) {
                    String line;
                    Matcher m;
                    while ((line = reader2.readLine()) != null) {
                        m = regex.matcher(line);
                        if (!m.matches()) continue;
                        enabled = m.group(1).equalsIgnoreCase("true");
                        break;
                    }
                }
            }
        } catch (IOException ignored) { }
        ENABLED = enabled;
    }

    //

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static @NotNull List<DebugGoalInstance> getGoals() {
        if (!ENABLED) return Collections.emptyList();
        synchronized (Debug.class) {
            if (GOALS != null) return GOALS;
            List<DebugGoalInstance> ret = new ArrayList<>();
            try {
                getGoals0(ret);
            } catch (IOException | ReflectiveOperationException e) {
                XClaim.logger.log(Level.WARNING, "Failed to perform reflection for debug goals", e);
            }
            return GOALS = Collections.unmodifiableList(ret);
        }
    }

    private static void getGoals0(@NotNull List<DebugGoalInstance> list) throws IOException, ReflectiveOperationException {
        for (Class<?> cls : DEBUGGABLE) {
            list.addAll(DebugGoalInstance.findInClass(cls));
        }
    }

    public static @Nullable DebugGoalInstance getGoalByLabel(@NotNull String label) {
        for (DebugGoalInstance instance : getGoals()) {
            if (instance.label().equalsIgnoreCase(label)) return instance;
        }
        return null;
    }

}
