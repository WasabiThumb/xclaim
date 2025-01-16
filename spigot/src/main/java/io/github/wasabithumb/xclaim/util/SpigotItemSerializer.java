package io.github.wasabithumb.xclaim.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class SpigotItemSerializer {

    public static byte @NotNull [] serialize(@NotNull ItemStack item) {
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(bos)
        ) {
            oos.writeObject(item);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            throw new AssertionError("Unexpected error in serialization", e);
        }
        return bytes;
    }

    public static @UnknownNullability ItemStack deserialize(byte @NotNull [] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             BukkitObjectInputStream ois = new BukkitObjectInputStream(bis)
        ) {
            return (ItemStack) ois.readObject();
        } catch (IOException | ReflectiveOperationException e) {
            throw new AssertionError("Unexpected error in deserialization", e);
        }
    }

}
