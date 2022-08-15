package codes.wasabi.xclaim.platform.spigot_1_8;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.PlatformPersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class SpigotPlatformPersistentDataContainer_1_8 implements PlatformPersistentDataContainer {

    private final String identifier;

    public SpigotPlatformPersistentDataContainer_1_8(Entity entity) {
        identifier = entity.getUniqueId().toString();
    }

    private File getPDCFolder() {
        File dataFolder = XClaim.dataFolder;
        if (!dataFolder.exists()) {
            boolean created = false;
            try {
                created = dataFolder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!created) XClaim.logger.log(Level.WARNING, "Failed to create data folder");
        }
        File target = new File(dataFolder, "pdc");
        if (!target.exists()) {
            boolean created = false;
            try {
                created = target.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!created) XClaim.logger.log(Level.WARNING, "Failed to create PDC folder");
        }
        return target;
    }

    private File getDataFile(String name) {
        File pdcFolder = getPDCFolder();
        File target = new File(pdcFolder, name);
        if (!target.exists()) {
            boolean created = false;
            try {
                created = target.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!created) XClaim.logger.log(Level.WARNING, "Failed to create data file pdc/" + name);
        }
        return target;
    }

    private File getDataFile() {
        return getDataFile(identifier + ".dat");
    }

    private final ReentrantLock lock = new ReentrantLock();

    private static byte typeToCode(PlatformPersistentDataType platformType) {
        byte type = -1;
        switch (platformType) {
            case BYTE:
                type = 0;
                break;
            case BYTE_ARRAY:
                type = 1;
                break;
            case STRING:
                type = 2;
                break;
        }
        return type;
    }

    private static class DataEntry {
        byte dataType;
        String identifier;
        Object dataValue;

        // Data structure:
        // byte (1) : data type
        // short (2) : identifier bytes length
        // byte[] (*) : identifier bytes
        // byte[] (*) : data

        boolean read(InputStream is) throws IOException {
            int n = is.read();
            if (n < 0) return false;
            dataType = (byte) n;
            byte[] idLengthBytes = is.readNBytes(Short.BYTES);
            short idLength = ByteBuffer.wrap(idLengthBytes).getShort();
            identifier = new String(is.readNBytes(idLength), StandardCharsets.UTF_8);
            if (dataType == 0) {
                dataValue = (byte) is.read();
            } else if (dataType == 1 || dataType == 2) {
                byte[] dataLengthBytes = is.readNBytes(Integer.BYTES);
                int dataLength = ByteBuffer.wrap(dataLengthBytes).getInt();
                byte[] data = is.readNBytes(dataLength);
                if (dataType == 2) {
                    dataValue = new String(data, StandardCharsets.UTF_8);
                } else {
                    dataValue = data;
                }
            }
            return true;
        }

        void write(OutputStream os) throws IOException {
            os.write(dataType);
            byte[] idBytes = identifier.getBytes(StandardCharsets.UTF_8);
            byte[] idLengthBytes = ByteBuffer.allocate(Short.BYTES).putShort((short) idBytes.length).array();
            os.write(idLengthBytes);
            os.write(idBytes);
            if (dataType == 0) {
                os.write((Byte) dataValue);
            } else if (dataType == 1 || dataType == 2) {
                byte[] data;
                if (dataType == 2) {
                    data = ((String) dataValue).getBytes(StandardCharsets.UTF_8);
                } else {
                    data = ((byte[]) dataValue);
                }
                os.write(ByteBuffer.allocate(Integer.BYTES).putInt(data.length).array());
                os.write(data);
            }
        }
    }

    @Override
    public void set(NamespacedKey key, PlatformPersistentDataType type, Object value) {
        String ks = key.toString();
        byte tc = typeToCode(type);
        lock.lock();
        try {
            File src = getDataFile();
            byte[] bytes = new byte[0];
            try (FileInputStream fis = new FileInputStream(src)) {
                bytes = fis.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            try (FileOutputStream fos = new FileOutputStream(src, false)) {
                DataEntry de = new DataEntry();
                while (de.read(bis)) {
                    if (de.identifier.equals(ks) && de.dataType == tc) continue;
                    de.write(fos);
                }
                if (value != null) {
                    de.identifier = ks;
                    de.dataValue = value;
                    de.dataType = tc;
                    de.write(fos);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object get(NamespacedKey key, PlatformPersistentDataType type) {
        String ks = key.toString();
        byte tc = typeToCode(type);
        Object ret = null;
        lock.lock();
        try {
            File src = getDataFile();
            try (FileInputStream fis = new FileInputStream(src)) {
                DataEntry de = new DataEntry();
                while (de.read(fis)) {
                    if (de.identifier.equals(ks) && de.dataType == tc) {
                        ret = de.dataValue;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
        return ret;
    }

    @Override
    public boolean has(NamespacedKey key, PlatformPersistentDataType type) {
        return get(key, type) != null;
    }

}
