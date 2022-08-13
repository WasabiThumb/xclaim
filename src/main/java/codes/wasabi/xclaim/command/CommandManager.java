package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager {

    public static class Handler implements CommandExecutor, TabCompleter {

        private static class Resolution {
            private final codes.wasabi.xclaim.command.Command cmd;
            private final String[] args;

            Resolution(@NotNull codes.wasabi.xclaim.command.Command cmd, @NotNull String[] args) {
                this.cmd = cmd;
                this.args = args;
            }

            @NotNull codes.wasabi.xclaim.command.Command cmd() {
                return cmd;
            }

            @NotNull String[] args() {
                return args;
            }
        }

        private final codes.wasabi.xclaim.command.Command cmd;
        private final PluginCommand bukkitCmd;
        private Handler(@NotNull codes.wasabi.xclaim.command.Command command) {
            cmd = command;
            bukkitCmd = XClaim.instance.getCommand(command.getName());
        }

        private Resolution resolveSubcommands(@NotNull codes.wasabi.xclaim.command.Command root, @NotNull String[] args) {
            codes.wasabi.xclaim.command.Command cmd = root;
            while (true) {
                boolean fullyResolved = true;
                if (args.length >= 1) {
                    String n = args[0];
                    Collection<codes.wasabi.xclaim.command.Command> sub = cmd.getSubCommands();
                    if (sub != null) {
                        for (codes.wasabi.xclaim.command.Command candidate : sub) {
                            if (n.equalsIgnoreCase(candidate.getName())) {
                                cmd = candidate;
                                String[] newArgs = new String[args.length - 1];
                                if (newArgs.length > 0) System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                                args = newArgs;
                                fullyResolved = false;
                                break;
                            }
                        }
                    }
                }
                if (fullyResolved) break;
            }
            return new Resolution(cmd, args);
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (!Objects.equals(command, bukkitCmd)) return false;
            Audience audience = Platform.getAdventure().sender(sender);
            Resolution res = resolveSubcommands(this.cmd, args);
            codes.wasabi.xclaim.command.Command cmd = res.cmd();
            args = res.args();
            if (cmd.requiresPlayerExecutor()) {
                if (!(sender instanceof Player)) {
                    audience.sendMessage(XClaim.lang.getComponent("cmdmgr-err-player"));
                    return true;
                }
            }
            Argument[] argDefs = cmd.getArguments();
            int len = args.length;
            int required = cmd.getNumRequiredArguments();
            if (len < required) {
                audience.sendMessage(XClaim.lang.getComponent("cmdmgr-err-min-args", required));
                return true;
            }
            if (len > argDefs.length) {
                audience.sendMessage(XClaim.lang.getComponent("cmdmgr-err-max-args", argDefs.length));
                return true;
            }
            Object[] obs = new Object[argDefs.length];
            try {
                for (int i = 0; i < len; i++) {
                    Argument def = argDefs[i];
                    String arg = args[i];
                    obs[i] = def.type().parse(arg);
                }
            } catch (IllegalArgumentException e) {
                audience.sendMessage(XClaim.lang.getComponent("cmdmgr-err-malformed"));
                return true;
            }
            for (int i=len; i < argDefs.length; i++) {
                obs[i] = null;
            }
            try {
                cmd.execute(sender, label, obs);
            } catch (Exception e) {
                audience.sendMessage(XClaim.lang.getComponent("cmdmgr-err-unexpected", e.getClass().getName()));
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            if (!Objects.equals(command, bukkitCmd)) return null;
            Resolution res = resolveSubcommands(this.cmd, args);
            codes.wasabi.xclaim.command.Command cmd = res.cmd();
            args = res.args();
            List<String> entries = new ArrayList<>();
            int idx = Math.max(args.length - 1, 0);
            Argument[] argDefs = cmd.getArguments();
            String tail = "";
            LevenshteinDistance sd = LevenshteinDistance.getDefaultInstance();
            if (idx < argDefs.length) {
                Argument arg = argDefs[idx];
                tail = (args.length == 0 ? "" : args[idx]);
                entries.addAll(arg.type().getSampleValues());
            }
            if (idx == 0) {
                Collection<codes.wasabi.xclaim.command.Command> sub = cmd.getSubCommands();
                if (sub != null) {
                    for (codes.wasabi.xclaim.command.Command c : sub) entries.add(c.getName());
                }
            }
            String finalTail = tail;
            return entries.stream().sorted(Comparator.comparingInt((String s) -> sd.apply(finalTail, s)).reversed()).collect(Collectors.toList());
        }

        public @NotNull codes.wasabi.xclaim.command.Command getCommand() {
            return cmd;
        }

        @SuppressWarnings("unused")
        public @Nullable PluginCommand getBukkitCommand() {
            return bukkitCmd;
        }

    }

    private final Map<String, Handler> map = new HashMap<>();
    public CommandManager() {

    }

    public void register(@NotNull codes.wasabi.xclaim.command.Command command) {
        String name = command.getName();
        if (map.containsKey(name)) {
            unregister(command);
        }
        Handler handler = new Handler(command);
        PluginCommand bukkitCmd = handler.bukkitCmd;
        if (bukkitCmd == null) {
            XClaim.logger.warning(XClaim.lang.get("cmdmgr-err-undefined", name));
            return;
        }
        bukkitCmd.setExecutor(handler);
        bukkitCmd.setTabCompleter(handler);
        bukkitCmd.setDescription(command.getDescription());
        map.put(name, new Handler(command));
    }

    public void unregister(@NotNull codes.wasabi.xclaim.command.Command command) {
        Handler handler = map.remove(command.getName());
        if (handler != null) {
            PluginCommand bukkitCmd = handler.bukkitCmd;
            if (bukkitCmd == null) return;
            bukkitCmd.setExecutor(null);
            bukkitCmd.setTabCompleter(null);
        }
    }

    // Remember, when you see this, that's when you know jank is around the corner!
    @SuppressWarnings("unchecked")
    private void unregister(Collection<Command> commands) {
        // This could easily be cleaned up 500x if Spigot added a simple method for exposing the command map
        try {
            Server server = Bukkit.getServer();
            Class<? extends Server> clazz = server.getClass();
            Field f = null;
            try {
                f = clazz.getDeclaredField("commandMap");
            } catch (NoSuchFieldException e) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (CommandMap.class.isAssignableFrom(field.getDeclaringClass())) {
                        f = field;
                        break;
                    }
                }
            }
            Objects.requireNonNull(f);
            f.setAccessible(true);
            CommandMap cm = (CommandMap) f.get(server);
            Class<? extends CommandMap> clazz1 = cm.getClass();
            Map<String, Command> known;
            try {
                // I hope I get fired for this trick...
                // Oh wait, I'm not getting paid ;__;
                Method m = clazz1.getMethod("getKnownCommands");
                known = (Map<String, Command>) m.invoke(cm);
            } catch (ReflectiveOperationException | NullPointerException | SecurityException | ClassCastException e) {
                Field f1 = clazz1.getField("knownCommands");
                f1.setAccessible(true);
                known = (Map<String, Command>) f1.get(cm);
            }
            for (Map.Entry<String, Command> entry : new HashSet<>(known.entrySet())) {
                String key = entry.getKey();
                Command value = entry.getValue();
                for (Command cmd : commands) {
                    if (Objects.equals(cmd, value)) {
                        known.remove(key);
                        break;
                    }
                }
            }
        } catch (InaccessibleObjectException | NoSuchFieldException | IllegalAccessException | SecurityException | NullPointerException | ClassCastException | ExceptionInInitializerError e) {
            e.printStackTrace();
        }
    }

    public void unregisterAll() {
        List<Command> commands = new ArrayList<>();
        for (String cmd : new HashSet<>(map.keySet())) {
            Handler handler = map.remove(cmd);
            if (handler != null) {
                PluginCommand bukkitCmd = handler.bukkitCmd;
                if (bukkitCmd == null) return;
                bukkitCmd.setExecutor(null);
                bukkitCmd.setTabCompleter(null);
                commands.add(bukkitCmd);
            }
        }
        unregister(commands);
    }

    public void registerDefaults() {
        Reflections reflections = new Reflections("codes.wasabi.xclaim.command");
        Set<Class<? extends codes.wasabi.xclaim.command.Command>> set = reflections.getSubTypesOf(codes.wasabi.xclaim.command.Command.class);
        for (Class<? extends codes.wasabi.xclaim.command.Command> clazz : set) {
            int mod = clazz.getModifiers();
            if (Modifier.isAbstract(mod)) continue;
            if (Modifier.isInterface(mod)) continue;
            if (clazz.getPackageName().contains("sub")) continue;
            Constructor<? extends codes.wasabi.xclaim.command.Command> con;
            try {
                con = clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                continue;
            }
            con.trySetAccessible();
            codes.wasabi.xclaim.command.Command com;
            try {
                com = con.newInstance();
            } catch (IllegalAccessException e) {
                XClaim.logger.warning(XClaim.lang.get("cmdmgr-err-reflect", clazz.getName()));
                e.printStackTrace();
                continue;
            } catch (ReflectiveOperationException e) {
                continue;
            }
            register(com);
        }
    }

}
