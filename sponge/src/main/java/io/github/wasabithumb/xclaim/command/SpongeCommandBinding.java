package io.github.wasabithumb.xclaim.command;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.collections.ProxyList;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.Subject;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

public record SpongeCommandBinding(
        Supplier<XClaim> runtimeSupplier
) implements Command.Raw {

    private @NotNull XClaim runtime() {
        return this.runtimeSupplier.get();
    }

    private @NotNull PlatformUser user(@NotNull CommandCause cause) {
        PlatformUser user;
        Subject subject = cause.subject();
        if (subject instanceof Player ply) {
            user = this.runtime().platform().adapter().player(ply);
        } else if (subject instanceof User usr) {
            user = this.runtime().platform().adapter().user(usr);
        } else {
            user = this.runtime().platform().users().console();
        }
        return user;
    }

    private @NotNull Queue<String> args(@NotNull ArgumentReader.Mutable arguments) throws CommandException {
        Queue<String> args = new LinkedList<>();
        while (arguments.canRead()) {
            args.add(arguments.parseString());
        }
        return args;
    }

    @Override
    public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        this.runtime().commands().execute(this.user(cause), this.args(arguments));
        return CommandResult.success();
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        List<String> ret = this.runtime().commands().suggest(this.user(cause), this.args(arguments));
        return new ProxyList<>(ret, CommandCompletion::of);
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return true;
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Component usage(CommandCause cause) {
        return Component.text("/xclaim");
    }

}
