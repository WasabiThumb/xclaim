package io.github.wasabithumb.xclaim.util;

import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class InventorySerializer {

    public static byte @NotNull [] serialize(@NotNull PlatformInventory inventory) {
        return serialize(inventory.getContents());
    }

    private static byte @NotNull [] serialize(@Nullable PlatformItem @NotNull [] items) {
        final int len = items.length;
        byte[][] data = new byte[len][];
        int dataLen = Integer.BYTES * (len + 1);

        PlatformItem item;
        byte[] itemData;
        for (int i=0; i < len; i++) {
            item = items[i];
            itemData = (item == null) ? new byte[0] : item.toBytes();
            data[i] = itemData;
            dataLen += itemData.length;
        }

        ByteBuffer buf = ByteBuffer.allocate(dataLen);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(len);
        for (int i=0; i < len; i++) {
            itemData = data[i];
            buf.putInt(itemData.length);
            buf.put(itemData);
        }
        return buf.array();
    }

    public static void deserialize(@NotNull PlatformInventory inventory, byte @NotNull [] bytes) {
        inventory.setContents(deserialize(inventory.platform(), bytes));
    }

    private static @Nullable PlatformItem @NotNull [] deserialize(@NotNull Platform platform, byte @NotNull [] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        int count = -1;
        try {
            count = buf.getInt();
            PlatformItem[] ret = new PlatformItem[count];
            int len;
            for (int i=0; i < count; i++) {
                len = buf.getInt();
                if (len < 0) {
                    throw new IllegalArgumentException("Inventory bytes has negative entry length (" + len +
                            ") at index " + i);
                } else if (len == 0) {
                    ret[i] = null;
                } else {
                    byte[] tmp = new byte[len];
                    buf.get(tmp, 0, len);
                    ret[i] = platform.createItem(tmp);
                }
            }
            return ret;
        } catch (BufferUnderflowException e) {
            throw new IllegalArgumentException(
                    "Inventory bytes violates size expectations (item count: " + count +
                    ", byte length: " + bytes.length + ")",
                    e
            );
        }
    }

}
