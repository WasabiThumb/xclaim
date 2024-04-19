package codes.wasabi.xclaim.particle.legacy;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

public class LegacyParticleServiceReflection {

    public final Class<?> builderClass;
    public final Class<?> effectClass;
    public final Constructor<?> builderConstructor;
    public final Method builderSetColor;
    public final Method builderSetLocation;
    public final Method builderSetAmount;
    public final Method builderSetOffset;
    public final Method builderDisplay;

    public LegacyParticleServiceReflection(Class<?> builderClass, Class<?> effectClass, Constructor<?> builderConstructor, Method builderSetColor, Method builderSetLocation, Method builderSetAmount, Method builderSetOffset, Method builderDisplay) {
        this.builderClass = builderClass;
        this.effectClass = effectClass;
        this.builderConstructor = builderConstructor;
        this.builderSetColor = builderSetColor;
        this.builderSetLocation = builderSetLocation;
        this.builderSetAmount = builderSetAmount;
        this.builderSetOffset = builderSetOffset;
        this.builderDisplay = builderDisplay;
    }

    Object getEffect(String name) {
        Enum<?> e;
        for (Object constant : this.effectClass.getEnumConstants()) {
            e = (Enum<?>) constant;
            if (Objects.equals(name, e.name())) return constant;
        }
        return null;
    }

    Object createBuilder(Object effect) {
        try {
            return this.builderConstructor.newInstance(effect);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    void builderSetColor(Object builder, Color color) {
        try {
            this.builderSetColor.invoke(builder, color);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    void builderSetLocation(Object builder, Location loc) {
        try {
            this.builderSetLocation.invoke(builder, loc);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    void builderSetAmount(Object builder, int amount) {
        try {
            this.builderSetAmount.invoke(builder, amount);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    void builderSetOffset(Object builder, float offsetX, float offsetY, float offsetZ) {
        try {
            this.builderSetOffset.invoke(builder, offsetX, offsetY, offsetZ);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    void builderDisplay(Object builder, Player ply) {
        try {
            this.builderDisplay.invoke(builder, (Object) (new Player[] { ply }));
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

}
