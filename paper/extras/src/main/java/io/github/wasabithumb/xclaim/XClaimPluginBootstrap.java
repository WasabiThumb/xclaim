package io.github.wasabithumb.xclaim;

import io.github.wasabithumb.xclaim.command.BukkitCommandBinding;
import io.github.wasabithumb.xclaim.util.identity.Identities;
import io.github.wasabithumb.xclaim.util.identity.Identity;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class XClaimPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext ctx) {
        LifecycleEventManager<BootstrapContext> manager = ctx.getLifecycleManager();
        manager.registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> this.registerCommands(event.registrar())
        );
    }

    private void registerCommands(@NotNull Commands commands) {
        commands.register(
                "xclaim",
                "XClaim main command",
                Collections.singletonList("xc"),
                new BindingCommand(AbstractXClaimPlugin.MAIN_COMMAND)
        );
        commands.register(
                "xci",
                "Change your identity as seen by XClaim (EXPERIMENTAL)",
                new IdentityCommand()
        );
    }

    //

    private static final class BindingCommand implements BasicCommand {

        private final BukkitCommandBinding binding;
        public BindingCommand(@NotNull BukkitCommandBinding binding) {
            this.binding = binding;
            this.binding.markHandled();
        }

        @Override
        public void execute(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] strings) {
            this.binding.onCommand(
                    stack.getSender(),
                    null,
                    null,
                    strings
            );
        }

        @Override
        public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] strings) {
            List<String> ret = this.binding.onTabComplete(
                    stack.getSender(),
                    null,
                    null,
                    strings
            );
            if (ret == null) return Collections.emptyList();
            return ret;
        }

    }

    //

    private static final class IdentityCommand implements BasicCommand {

        @Override
        public void execute(CommandSourceStack stack, String[] args) {
            CommandSender sender = stack.getSender();
            if (!(sender instanceof Player ply)) {
                sender.sendMessage(
                        Component.text("* You must be a player to run this command!")
                                .color(NamedTextColor.RED)
                );
                return;
            }

            String name;
            if (args.length < 1 || (name = args[0]).isEmpty()) {
                Identities.set(ply, null);
                sender.sendMessage(Component.text("* Identity cleared").color(NamedTextColor.GRAY));
                return;
            }

            Identity.Fake.Builder builder = Identity.fake()
                    .name(name);

            switch (args.length) {
                case 1:
                    break;
                case 3:
                    if (this.acceptBoolArg(sender, args[2], builder::admin)) return;
                case 2:
                    if (this.acceptBoolArg(sender, args[1], builder::op)) return;
                    break;
                default:
                    sender.sendMessage(Component.text("* Too many arguments!").color(NamedTextColor.RED));
                    break;
            }

            Identities.set(ply, builder.build());
            sender.sendMessage(Component.text("* Identity updated").color(NamedTextColor.GREEN));
        }

        private boolean acceptBoolArg(
                @NotNull CommandSender sender,
                @NotNull String text,
                @NotNull Consumer<Boolean> cb
        ) {
            if ("true".equalsIgnoreCase(text)) {
                cb.accept(Boolean.TRUE);
                return false;
            }
            if ("false".equalsIgnoreCase(text)) {
                cb.accept(Boolean.FALSE);
                return false;
            }
            sender.sendMessage(Component.text("* Expected boolean, got " + text).color(NamedTextColor.RED));
            return true;
        }

        @Override
        public boolean canUse(@NotNull CommandSender sender) {
            return sender.isOp();
        }

    }

}
