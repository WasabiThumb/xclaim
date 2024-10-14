package codes.wasabi.xclaim.gui2.layout.memory;

import codes.wasabi.xclaim.gui2.layout.GuiLayout;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MemoryGuiLayout implements GuiLayout {

    private static final int INITIAL_CAPACITY = 8;
    private static final double LOAD_FACTOR   = 0.75d;

    protected final int height;
    private GuiSlot[] arr = new GuiSlot[INITIAL_CAPACITY];
    private int capacity  = INITIAL_CAPACITY;
    private int maxSlot   = -1;

    public MemoryGuiLayout(int height) {
        if (height < 3) throw new IllegalArgumentException("Height must be at least 3");
        this.height = height;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public @NotNull List<GuiSlot> getSlots() {
        if (this.maxSlot == -1) return Collections.emptyList();

        GuiSlot[] view = new GuiSlot[this.maxSlot + 1];
        int count = 0;
        GuiSlot val;
        for (int i=0; i <= this.maxSlot; i++) {
            val = this.arr[i];
            if (val == null) continue;
            view[count++] = val;
        }

        if (count == 0) return Collections.emptyList();
        return Collections.unmodifiableList(Arrays.asList(view).subList(0, count));
    }

    @Override
    public @Nullable GuiSlot getSlot(int index) {
        if (index < 0 || index > this.maxSlot) return null;
        return this.arr[index];
    }

    public void clear() {
        this.maxSlot = -1;
    }

    @Override
    public void addSlot(@NotNull GuiSlot slot) {
        final int index = slot.index();
        if (index < 0 || index > 254) throw new IllegalArgumentException("Illegal slot index: " + index);
        if (index > this.maxSlot) {
            this.maxSlot = index;
            if (index >= this.capacity) {
                final int required = Math.min(
                        (int) Math.ceil(
                                ((double) (index + 1)) / LOAD_FACTOR
                        ),
                        256
                );

                GuiSlot[] cpy = new GuiSlot[required];
                System.arraycopy(this.arr, 0, cpy, 0, this.capacity);

                this.arr = cpy;
                this.capacity = required;
            }
        }
        this.arr[index] = slot;
    }

    @Override
    public int getMaxSlot() {
        return this.maxSlot;
    }

}
