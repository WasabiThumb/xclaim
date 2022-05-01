package codes.wasabi.xclaim.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class InventorySerializer {

    private static final byte[] zeroBytes = ByteBuffer.allocate(Integer.BYTES).putInt(0).array();

    public static byte @NotNull [] serialize(@NotNull Inventory inventory) {
        return serialize(inventory.getContents());
    }

    public static byte @NotNull [] serialize(@Nullable ItemStack @NotNull [] inventory) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.writeBytes(ByteBuffer.allocate(Integer.BYTES).putInt(inventory.length).array());
        for (ItemStack is : inventory) {
            if (is == null) {
                bos.writeBytes(zeroBytes);
            } else {
                byte[] bytes = is.serializeAsBytes();
                bos.writeBytes(ByteBuffer.allocate(Integer.BYTES).putInt(bytes.length).array());
                bos.writeBytes(bytes);
            }
        }
        return bos.toByteArray();
    }

    public static void deserialize(byte @NotNull [] bytes, @NotNull Inventory inventory) throws IllegalArgumentException {
        inventory.setContents(deserialize(bytes));
    }

    public static @Nullable ItemStack @NotNull [] deserialize(byte @NotNull [] bytes) throws IllegalArgumentException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            int length = ByteBuffer.wrap(bis.readNBytes(Integer.BYTES)).getInt();
            ItemStack[] ret = new ItemStack[length];
            for (int i=0; i < ret.length; i++) {
                int len = ByteBuffer.wrap(bis.readNBytes(Integer.BYTES)).getInt();
                if (len == 0) {
                    ret[i] = null;
                } else {
                    byte[] bs = bis.readNBytes(len);
                    ret[i] = ItemStack.deserializeBytes(bs);
                }
            }
            return ret;
        } catch (IOException e) {
            throw new IllegalArgumentException("Malformed bytes", e);
        }
    }

}
