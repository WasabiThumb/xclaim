package io.github.wasabithumb.xclaim.gui.spec.helper;

import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiPagination;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.util.ColorTag;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;

public abstract class PaginatedGuiSpec<T> implements GuiSpec {

    @Contract("_ -> new")
    private static @NotNull PlatformItem createPreviousItem(@NotNull GuiInstance instance) {
        return DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.ARROW),
                instance.runtime().lang(I18N.GUI_COMB_PREVIOUS)
        );
    }

    @Contract("_ -> new")
    private static @NotNull PlatformItem createNextItem(@NotNull GuiInstance instance) {
        return DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.ARROW),
                instance.runtime().lang(I18N.GUI_COMB_NEXT)
        );
    }

    @Contract("_ -> new")
    private static @NotNull PlatformItem createBackItem(@NotNull GuiInstance instance) {
        return DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang(I18N.GUI_COMB_BACK),
                ColorTag.RED
        );
    }

    //

    protected final GuiPagination<T> pagination = new GuiPagination<>();
    protected GuiPagination.State paginationState = GuiPagination.State.ONLY_PAGE;
    protected boolean queuedForce = false;

    //

    @Override
    public void populate(@NotNull GuiInstance instance) {
        final GuiPagination.State state = this.pagination.setSlot(this.getContentSlot())
                .setEntries(this.getEntries(instance), this.queuedForce)
                .setSort(this.getSort(instance))
                .populate(instance, (T entry) -> this.populateEntry(instance, entry));

        this.queuedForce = false;

        instance.set(this.getBackSlot(), createBackItem(instance));
        instance.set(
                this.getPreviousSlot(),
                state.hasPrevious() ? createPreviousItem(instance) : this.getPreviousExtra(instance)
        );
        instance.set(
                this.getNextSlot(),
                state.hasNext() ? createNextItem(instance) : this.getNextExtra(instance)
        );

        this.paginationState = state;
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        final int sel = slot.index();
        if (sel == this.getContentSlot()) {
            T entry = this.pagination.click(instance, slot, index);
            if (entry != null) return this.onClickEntry(instance, entry);
        } else if (sel == this.getBackSlot()) {
            return GuiAction.transfer(this.getReturn());
        } else if (sel == this.getPreviousSlot()) {
            if (this.paginationState.hasPrevious()) {
                this.pagination.previousPage();
                return GuiAction.repopulate();
            }
        } else if (sel == this.getNextSlot()) {
            if (this.paginationState.hasNext()) {
                this.pagination.nextPage();
                return GuiAction.repopulate();
            }
        }
        return this.onClickExtra(instance, slot, index);
    }

    //

    /** Provides the content to paginate; there is no need to order this. It is recollected and memoized. */
    protected abstract @NotNull Collection<T> getEntries(@NotNull GuiInstance instance);

    /** The sorting to use for entries, if any. */
    protected @Nullable Comparator<T> getSort(@NotNull GuiInstance instance) {
        return null;
    }

    /** Provides the slot ID for the paginated content. */
    protected abstract int getContentSlot();

    /** Provides the slot ID for the "previous page" item. */
    protected abstract int getPreviousSlot();

    /** Provides the item to use where "previous page" would go when no previous page exists. */
    protected @Nullable PlatformItem getPreviousExtra(@NotNull GuiInstance instance) {
        return null;
    }

    /** Provides the slot ID for the "next page" item. */
    protected abstract int getNextSlot();

    /** Provides the item to use where "next page" would go when no next page exists. */
    protected @Nullable PlatformItem getNextExtra(@NotNull GuiInstance instance) {
        return null;
    }

    /** Provides the slot ID for the "back" item. */
    protected abstract int getBackSlot();

    /** Provides the item to represent the given paginated entry. */
    protected abstract @Nullable PlatformItem populateEntry(@NotNull GuiInstance instance, @NotNull T entry);

    /** Specifies the action to run when a paginated entry is clicked. */
    protected abstract @NotNull GuiAction onClickEntry(@NotNull GuiInstance instance, @NotNull T entry);

    /** Specifies the action to run when any slot not mapped by this class is clicked, including the PREVIOUS and
     * NEXT slots when they would have no effect. */
    protected @NotNull GuiAction onClickExtra(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        return GuiAction.nothing();
    }

    /** Specifies the spec to switch to when using the BACK item. */
    protected @NotNull GuiSpec getReturn() {
        return GuiSpecs.main();
    }

    protected final void markForceUpdate() {
        this.queuedForce = true;
    }

}
