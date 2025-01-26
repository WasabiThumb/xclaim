package io.github.wasabithumb.xclaim.command.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.command.NullaryCommand;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.integration.Integrations;
import io.github.wasabithumb.xclaim.platform.user.PlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InfoCommand implements NullaryCommand {

    @Override
    public @NotNull Translatable name() {
        return I18N.CMD_INFO_NAME;
    }

    @Override
    public @NotNull Translatable description() {
        return I18N.CMD_INFO_DESCRIPTION;
    }

    @Override
    public void execute(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        if (user instanceof PlatformConsoleUser) {
            user.sendMessage("<b>XClaim</b> <gold>v2.0.0</gold>");
        } else {
            user.sendMessage("<dark_green><b>| | |</b></dark_green>  <b>XClaim</b>");
            user.sendMessage("<dark_green><b>\\|/</b></dark_green>  <gold>v2.0.0</gold>"); // TODO: Accurate version & git
        }

        // Platform
        String platformName = runtime.platform().getClass().getSimpleName();
        if (platformName.endsWith("Platform")) platformName = platformName.substring(0, platformName.length() - 8);
        user.sendMessage(runtime.lang(I18N.CMD_INFO_PLATFORM) + " " + platformName);

        // Usage
        ClaimManager claims = runtime.claims();
        int claimCount = 0;
        int chunkCount = 0;
        for (Claim c : claims.getAll()) {
            claimCount++;
            chunkCount += c.chunkCount();
        }
        String claimUsageText = (claimCount == 1) ?
                I18N.CMD_INFO_USAGE_CLAIMS_SINGLE.format(runtime) :
                I18N.CMD_INFO_USAGE_CLAIMS.with(claimCount).format(runtime);
        String chunkUsageText = (chunkCount == 1) ?
                I18N.CMD_INFO_USAGE_CHUNKS_SINGLE.format(runtime) :
                I18N.CMD_INFO_USAGE_CHUNKS.with(chunkCount).format(runtime);
        user.sendMessage(I18N.CMD_INFO_USAGE.format(runtime) + " " + claimUsageText + " - " + chunkUsageText);

        // Hooks
        StringBuilder hooksText = new StringBuilder();
        Integrations hooks = runtime.integrations();
        this.calcHooksText(
                runtime, hooksText,
                I18N.CMD_INFO_HOOKS_ECONOMY,
                I18N.CMD_INFO_HOOKS_ECONOMY_NONE,
                hooks.economy()
        );
        hooksText.append(' ');
        this.calcHooksText(
                runtime, hooksText,
                I18N.CMD_INFO_HOOKS_MAP,
                I18N.CMD_INFO_HOOKS_MAP_NONE,
                hooks.map()
        );
        hooksText.append(' ');
        this.calcHooksText(
                runtime, hooksText,
                I18N.CMD_INFO_HOOKS_PLACEHOLDER,
                I18N.CMD_INFO_HOOKS_PLACEHOLDER_NONE,
                hooks.placeholder()
        );
        hooksText.append(' ');
        this.calcHooksText(
                runtime, hooksText,
                I18N.CMD_INFO_HOOKS_PROTECTION,
                I18N.CMD_INFO_HOOKS_PROTECTION_NONE,
                hooks.protection()
        );
        user.sendMessage(runtime.lang(I18N.CMD_INFO_HOOKS) + " " + hooksText);

        // Stress
        ClaimManager.StressReport report = claims.calcStress();
        user.sendMessage(
                runtime.lang(I18N.CMD_INFO_STRESS) + " " +
                this.calcStressText(runtime, 'P', I18N.CMD_INFO_STRESS_PRIMARY, report.primary()) + " " +
                this.calcStressText(runtime, 'O', I18N.CMD_INFO_STRESS_OWNERSHIP, report.ownership()) + " " +
                this.calcStressText(runtime, 'R', I18N.CMD_INFO_STRESS_REGION, report.region())
        );
    }

    private void calcHooksText(
            @NotNull XClaim runtime,
            @NotNull StringBuilder out,
            @NotNull Translatable text,
            @NotNull Translatable emptyDesc,
            @Nullable Object object
    ) {
        out.append("<hover:show_text:'");

        if (object == null) {
            out.append(runtime.lang(emptyDesc))
                    .append("'><red>")
                    .append(runtime.lang(text))
                    .append("</red>");
        } else {
            out.append(object.getClass().getSimpleName())
                    .append("'><green>")
                    .append(runtime.lang(text))
                    .append("</green>");
        }

        out.append("</hover>");
    }

    private @NotNull String calcStressText(
            @NotNull XClaim runtime,
            char c,
            @NotNull Translatable title,
            long value
    ) {
        StringBuilder ret = new StringBuilder();
        ret.append("<hover:show_text:'")
                .append(runtime.lang(title))
                .append(" - ");

        String col;
        if (value <= 1L) {
            col = "0";
            ret.append(I18N.CMD_INFO_STRESS_MICROS_LOW.with(1L).format(runtime));
        } else if (value >= ClaimManager.StressReport.MAX) {
            col = "1";
            ret.append(I18N.CMD_INFO_STRESS_MICROS_HIGH.with(ClaimManager.StressReport.MAX).format(runtime));
        } else {
            double d = ((double) value) / ((double) ClaimManager.StressReport.MAX);
            col = "0." + (long) Math.floor(d * 1000);
            ret.append(I18N.CMD_INFO_STRESS_MICROS.with(value).format(runtime));
        }

        return ret.append("'><transition:green:yellow:red:")
                .append(col)
                .append('>')
                .append(c)
                .append("</transition></hover>")
                .toString();
    }

}
