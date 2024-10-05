package codes.wasabi.xclaim.gui2.layout;

import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.util.WeakLink;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class GuiPagination<T> {

    private static final int F_ENTRIES_SET = 1;
    private static final int F_ENTRIES_SORTED = 2;
    private static final int F_SLOT_SET = 4;

    // Parameters
    protected int page = 0;
    protected List<T> entries;
    protected int slot;

    // Technical stuff used to facilitate setting & validating parameters
    private int flags = 0;
    private final WeakLink<Collection<T>> entriesSource = new WeakLink<>();
    private Comparator<T> sort = null;

    // Pagination

    public synchronized void previousPage() {
        this.page--;
    }

    public synchronized void nextPage() {
        this.page++;
    }

    public synchronized @NotNull State populate(@NotNull GuiInstance instance, @NotNull Function<T, ItemStack> mapper) {
        Data<T> data = this.getData(instance);
        if (data == null) return State.ONLY_PAGE;

        final int count = data.values.size();
        final int width = data.slot.width();
        final int startSparse = Math.floorDiv(count, width) * width;

        for (int i=0; i < startSparse; i++) {
            instance.set(data.slot, i, mapper.apply(data.values.get(i)));
        }

        final GuiBasis basis = data.slot.basis();
        final int innerSize = count - startSparse;
        int head = 0;
        for (int i=startSparse; i < count; i++) {
            instance.set(
                    data.slot,
                    startSparse + basis.organize(head++, innerSize, width),
                    mapper.apply(data.values.get(i))
            );
        }

        return data.getState();
    }

    public synchronized @Nullable T click(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        Data<T> data = this.getData(instance);
        if (data == null) return null;
        if (data.slot.index() != slot.index()) return null;

        final int count = data.values.size();
        final int width = data.slot.width();
        final int startSparse = Math.floorDiv(count, width) * width;

        if (index >= startSparse) {
            index = startSparse + data.slot.basis().unorganize(
                    index - startSparse,
                    count - startSparse,
                    width
            );
        }
        return data.values.get(index);
    }

    // Setters

    @Contract("_, _ -> this")
    public synchronized @NotNull GuiPagination<T> setEntries(@NotNull Collection<T> entries, boolean force) {
        boolean move = true;
        if ((this.flags & F_ENTRIES_SET) == 0) {
            this.flags |= F_ENTRIES_SET;
        } else if (!force) {
            move = !this.entriesSource.refersTo(entries);
        }

        if (move) {
            this.entriesSource.set(entries);
            this.entries = new ArrayList<>(entries);
        }
        return this;
    }

    @Contract("_ -> this")
    public @NotNull GuiPagination<T> setEntries(@NotNull Collection<T> entries) {
        return this.setEntries(entries, false);
    }

    @Contract("_ -> this")
    public synchronized @NotNull GuiPagination<T> setSlot(int slot) {
        this.slot = slot;
        this.flags |= F_SLOT_SET;
        return this;
    }

    @Contract("_ -> this")
    public synchronized @NotNull GuiPagination<T> setSort(@Nullable Comparator<T> sort) {
        if (Objects.equals(this.sort, sort)) return this;
        this.sort = sort;
        this.flags &= (~F_ENTRIES_SORTED);
        return this;
    }

    // Utilities

    private synchronized @Nullable Data<T> getData(@NotNull GuiInstance instance) {
        this.validate();

        final GuiSlot slot = instance.getSlot(this.slot);
        if (slot == null) return null;

        final int capacity = slot.capacity();
        final boolean empty = this.entries.isEmpty();
        final int pageCount = empty ? 1 : (Math.floorDiv(this.entries.size() - 1, capacity) + 1);
        int page = this.page;
        if (page < 0) {
            this.page = page = 0;
        } else if (page >= pageCount) {
            this.page = page = pageCount - 1;
        }

        final int pageStart = page * capacity;
        final int pageEnd = Math.min(pageStart + capacity, this.entries.size());

        return new Data<>(
                slot,
                capacity,
                pageCount,
                page,
                empty ? Collections.emptyList() : this.entries.subList(pageStart, pageEnd)
        );
    }

    private synchronized void validate() throws IllegalStateException {
        if ((this.flags & F_ENTRIES_SET) == 0)
            throw new IllegalStateException("Entries must be set before using GuiPagination");
        if ((this.flags & F_ENTRIES_SORTED) == 0) {
            if (this.sort != null) this.entries.sort(this.sort);
            this.flags |= F_ENTRIES_SORTED;
        }
        if ((this.flags & F_SLOT_SET) == 0)
            throw new IllegalStateException("Slot must be set before using GuiPagination");
    }

    // State

    public enum State {
        ONLY_PAGE(0b00),
        FIRST_PAGE(0b01),
        LAST_PAGE(0b10),
        SOME_PAGE(0b11);

        static @NotNull State of(int pageIndex, int pageCount) {
            if (pageIndex == 0) {
                return ((pageIndex + 1) < pageCount) ? FIRST_PAGE : ONLY_PAGE;
            } else if (pageIndex == (pageCount - 1)) {
                return LAST_PAGE;
            } else {
                return SOME_PAGE;
            }
        }

        private final byte value;
        State(int value) {
            this.value = (byte) value;
        }

        public boolean hasPrevious() {
            return (this.value & ((byte) 0b10)) != ((byte) 0);
        }

        public boolean hasNext() {
            return (this.value & ((byte) 0b01)) != ((byte) 0);
        }

    }

    // Data

    protected static class Data<Q> {

        GuiSlot slot;
        int capacity;
        int pageCount;
        int page;
        List<Q> values;

        Data(@NotNull GuiSlot slot, int capacity, int pageCount, int page, List<Q> values) {
            this.slot = slot;
            this.capacity = capacity;
            this.pageCount = pageCount;
            this.page = page;
            this.values = values;
        }

        @NotNull State getState() {
            return State.of(this.page, this.pageCount);
        }

    }

}
