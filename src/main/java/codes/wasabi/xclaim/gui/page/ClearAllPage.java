package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ClearAllPage extends Page {

    private static final ItemStack YES_STACK = DisplayItem.create(
            Material.GREEN_CONCRETE,
            Component.text("Yes, I am sure").color(NamedTextColor.GREEN),
            Arrays.asList(
                    Component.text("This action cannot").color(NamedTextColor.GRAY),
                    Component.text("be undone!").color(NamedTextColor.GRAY)
            )
    );

    private static final ItemStack YES_REALLY_STACK = DisplayItem.create(
            Material.GREEN_CONCRETE,
            Component.text("I'm really sure!").color(NamedTextColor.GREEN),
            Arrays.asList(
                    Component.text("This action cannot").color(NamedTextColor.GRAY),
                    Component.text("be undone!").color(NamedTextColor.GRAY)
            )
    );

    private static final ItemStack NO_STACK = DisplayItem.create(
            Material.RED_CONCRETE,
            Component.text("No, take me back").color(NamedTextColor.GREEN),
            Arrays.asList(
                    Component.text("Keep your claims the").color(NamedTextColor.GRAY),
                    Component.text("way they are").color(NamedTextColor.GRAY)
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
