package codes.wasabi.xclaim.gui2.spec;

import codes.wasabi.xclaim.gui2.spec.impl.*;
import org.jetbrains.annotations.NotNull;

public final class GuiSpecs {

    // Non-stateful specs

    private static final GuiSpec MAIN = new MainGuiSpec();
    private static final GuiSpec VERSION_INFO = new VersionInfoGuiSpec();
    private static final GuiSpec NEW_CLAIM = new NewClaimGuiSpec();

    @SuppressWarnings("ConfusingMainMethod")
    public static @NotNull GuiSpec main() {
        return MAIN;
    }

    public static @NotNull GuiSpec versionInfo() {
        return VERSION_INFO;
    }

    public static @NotNull GuiSpec newClaim() {
        return NEW_CLAIM;
    }

    // Stateful specs

    public static @NotNull GuiSpec editTrust() {
        return new EditTrustGuiSpec();
    }

    public static @NotNull GuiSpec editChunks() {
        return new EditChunksGuiSpec();
    }

    public static @NotNull GuiSpec renameClaim() {
        return new RenameClaimGuiSpec();
    }

}
