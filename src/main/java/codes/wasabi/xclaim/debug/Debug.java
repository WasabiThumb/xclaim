package codes.wasabi.xclaim.debug;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.debug.goal.DebugGoalInstance;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@ApiStatus.Internal
public final class Debug {

    private static final String PACKAGE_PREFIX_SLASH = "codes/wasabi/xclaim";
    private static final String PACKAGE_PREFIX_DOT = "codes.wasabi.xclaim";
    private static final int PACKAGE_PREFIX_LENGTH = 19;

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
                getGoals0(getPackageList(), ret);
            } catch (IOException | ReflectiveOperationException e) {
                XClaim.logger.log(Level.WARNING, "Failed to perform reflection for debug goals", e);
            }
            return GOALS = Collections.unmodifiableList(ret);
        }
    }

    private static void getGoals0(@NotNull String[] packageList, @NotNull List<DebugGoalInstance> list) throws IOException, ReflectiveOperationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packageList)
                .setScanners(Scanners.TypesAnnotated)
        );

        for (Class<?> cls : reflections.getTypesAnnotatedWith(Debuggable.class)) {
            list.addAll(DebugGoalInstance.findInClass(cls));
        }
    }

    private static String[] getPackageList() throws IOException {
        Set<String> packages = new LinkedHashSet<>();
        //

        try (FileInputStream fis = new FileInputStream(CODE_SOURCE);
             ZipInputStream zis = new ZipInputStream(fis)
        ) {
            ZipEntry ze;
            String name;
            StringBuilder sb = new StringBuilder(PACKAGE_PREFIX_DOT);

            while ((ze = zis.getNextEntry()) != null) {
                if (!ze.isDirectory()) continue;
                name = ze.getName();
                if (!name.startsWith(PACKAGE_PREFIX_SLASH)) continue;

                char c;
                boolean queueDot = false;
                for (int i=PACKAGE_PREFIX_LENGTH; i < name.length(); i++) {
                    c = name.charAt(i);
                    if (c == '/') {
                        queueDot = true;
                    } else {
                        if (queueDot) {
                            sb.append('.');
                            queueDot = false;
                        }
                        sb.append(c);
                    }
                }
                packages.add(sb.toString());
                sb.setLength(PACKAGE_PREFIX_LENGTH);
            }
        }

        //
        final int count = packages.size();
        String[] ret = new String[count];
        int head = 0;
        for (String pkg : packages) ret[head++] = pkg;
        return ret;
    }

    public static @Nullable DebugGoalInstance getGoalByLabel(@NotNull String label) {
        for (DebugGoalInstance instance : getGoals()) {
            if (instance.label().equalsIgnoreCase(label)) return instance;
        }
        return null;
    }

}
