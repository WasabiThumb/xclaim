package codes.wasabi.xclaim.platform.paper_1_17;

import codes.wasabi.xclaim.platform.PlatformChatListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.event.player.AsyncChatEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PaperPlatformChatListener implements PlatformChatListener {

    private final List<Consumer<Data>> callbacks = new ArrayList<>();

    @Override
    public void onChat(Consumer<Data> cb) {
        callbacks.add(cb);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
        callbacks.clear();
    }

    @EventHandler
    public void onMessage(@NotNull AsyncChatEvent event) {
        Object nativeComponent;
        try {
            Class<?> eventClass = event.getClass();
            Method m = eventClass.getMethod("originalMessage");
            nativeComponent = m.invoke(event);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String serialized = nativeComponentToPlainText(nativeComponent);
        if (serialized == null) serialized = "";

        Data data = new Data(
                event.getPlayer(),
                serialized,
                () -> event.setCancelled(true)
        );
        for (Consumer<Data> consumer : callbacks) consumer.accept(data);
    }

    private static final String nativePackage = new String(new char[]{ 'n', 'e', 't', '.', 'k', 'y', 'o', 'r', 'i', '.', 'a', 'd', 'v', 'e', 'n', 't', 'u', 'r', 'e' });
    private boolean nativeComponentInit = false;
    private Object nativeComponentSerializer;
    private Method nativeComponentSerialize;

    private String nativeComponentToPlainText(Object nativeComponent) {
        if (!this.nativeComponentInit) {
            try {
                Class<?> clazz = nativeComponent.getClass();
                ClassLoader cl = clazz.getClassLoader();
                Class<?> serializerClass = Class.forName(
                        getNativeClassName("text.serializer.plain.PlainTextComponentSerializer"),
                        true,
                        cl
                );
                Class<?> componentClass = Class.forName(
                        getNativeClassName("text.Component"),
                        true,
                        cl
                );

                Method m = serializerClass.getDeclaredMethod("plainText");

                Object serializer = m.invoke(null);
                Method serializeMethod = serializerClass.getMethod("serialize", componentClass);

                this.nativeComponentSerializer = serializer;
                this.nativeComponentSerialize = serializeMethod;
                this.nativeComponentInit = true;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        String ret = null;
        try {
            ret = (String) this.nativeComponentSerialize.invoke(this.nativeComponentSerializer, nativeComponent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String getNativeClassName(String path) {
        return nativePackage + "." + path;
    }

}
