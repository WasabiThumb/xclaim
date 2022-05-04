package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.argument.Argument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager {

    public static class Handler implements CommandExecutor, TabCompleter {

        private record Resolution(@NotNull codes.wasabi.xclaim.command.Command cmd, @NotNull String[] args) {}

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
            Resolution res = resolveSubcommands(this.cmd, args);
            codes.wasabi.xclaim.command.Command cmd = res.cmd();
            args = res.args();
            if (cmd.requiresPlayerExecutor()) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Component.text("* You must be a player to run this command!").color(NamedTextColor.RED));
                    return true;
                }
            }
            Argument[] argDefs = cmd.getArguments();
            int len = args.length;
            int required = cmd.getNumRequiredArguments();
            if (len < required) {
                sender.sendMessage(Component.text("* Not enough arguments! This command requires at least " + required).color(NamedTextColor.RED));
                return true;
            }
            if (len > argDefs.length) {
                sender.sendMessage(Component.text("* Too many arguments! This command takes at most " + argDefs.length).color(NamedTextColor.RED));
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
                sender.sendMessage(Component.text("* Bad arguments! See help page for more info").color(NamedTextColor.RED));
                return true;
            }
            for (int i=len; i < argDefs.length; i++) {
                obs[i] = null;
            }
            try {
                cmd.execute(sender, label, obs);
            } catch (Exception e) {
                sender.sendMessage(Component.text("* An unexpected exception (" + e.getClass().getName() + ") occurred while executing this command.").color(NamedTextColor.RED));
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
            return entries.stream().sorted(Comparator.comparingInt((String s) -> sd.apply(finalTail, s))).collect(Collectors.toList());
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
            XClaim.logger.warning("Could not register command \"" + name + "\", does not exist in plugin.yml");
            return;
        }
        bukkitCmd.setExecutor(handler);
        bukkitCmd.setTabCompleter(handler);
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
                XClaim.logger.warning("Could not access constructor for class " + clazz.getName() + ", see details below");
                e.printStackTrace();
                continue;
            } catch (ReflectiveOperationException e) {
                continue;
            }
            register(com);
        }
    }

}
