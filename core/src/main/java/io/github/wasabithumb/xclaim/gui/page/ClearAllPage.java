package io.github.wasabithumb.xclaim.gui.page;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.api.Claim;
import io.github.wasabithumb.xclaim.gui.GUIHandler;
import io.github.wasabithumb.xclaim.gui.Page;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ClearAllPage extends Page {

    private static final ItemStack YES_STACK = DisplayItem.create(
            Platform.get().getGreenConcreteMaterial(),
            XClaim.lang.getComponent("gui-clear-yes"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-clear-yes-line1"),
                    XClaim.lang.getComponent("gui-clear-yes-line2")
            )
    );

    private static final ItemStack YES_REALLY_STACK = DisplayItem.create(
            Platform.get().getGreenConcreteMaterial(),
            XClaim.lang.getComponent("gui-clear-yes2"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-clear-yes-line1"),
                    XClaim.lang.getComponent("gui-clear-yes-line2")
            )
    );

    private static final ItemStack NO_STACK = DisplayItem.create(
            Platform.get().getRedConcreteMaterial(),
            XClaim.lang.getComponent("gui-clear-no"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-clear-no-line1"),
                    XClaim.lang.getComponent("gui-clear-no-line2")
            )
    );

    private boolean firstStage = true;
    public ClearAllPage(@NotNull GUIHandler parent) {
        super(parent);
    }

    private void populate() {
        clear();
        //   2  6
        int yesPos = (firstStage ? 11 : 15);
        int noPos = (firstStage ? 15 : 11);
        ItemStack yesStack = (firstStage ? YES_STACK : YES_REALLY_STACK);
        setItem(yesPos, yesStack);
        setItem(noPos, NO_STACK);
    }

    @Override
    public void onEnter() {
        populate();
    }

    @Override
    public void onClick(int slot) {
        int yesPos = (firstStage ? 11 : 15);
        int noPos = (firstStage ? 15 : 11);
        if (slot == yesPos) {
            if (firstStage) {
                firstStage = false;
                populate();
            } else {
                Claim.getByOwner(getTarget()).forEach(Claim::unclaim);
                switchPage(new MainPage(getParent()));
            }
        } else if (slot == noPos) {
            switchPage(new MainPage(getParent()));
        }
    }

}
