package io.github.wasabithumb.xclaim;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
                Collections.singletonList("xc"),
                new BasicCommand() {
                    @Override
                    public void execute(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] strings) {
                        AbstractXClaimPlugin.MAIN_COMMAND.onCommand(
                                stack.getSender(),
                                null,
                                null,
                                strings
                        );
                    }

                    @Override
                    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] strings) {
                        List<String> ret = AbstractXClaimPlugin.MAIN_COMMAND.onTabComplete(
                                stack.getSender(),
                                null,
                                null,
                                strings
                        );
                        if (ret == null) return Collections.emptyList();
                        return ret;
                    }
                }
        );
    }

}
