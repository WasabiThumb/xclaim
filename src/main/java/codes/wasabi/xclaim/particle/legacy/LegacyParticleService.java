package codes.wasabi.xclaim.particle.legacy;

import codes.wasabi.xclaim.particle.ParticleService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;

public class LegacyParticleService extends ParticleService {

    private final LegacyParticleServiceReflection reflection;
    private final Object redstoneParticle;
    public LegacyParticleService(LegacyParticleServiceReflection reflection) {
        this.reflection = reflection;
        this.redstoneParticle = reflection.getEffect("REDSTONE");
    }

    @Override
    public void displayRedstoneParticle(Color color, Location pos, int amount, float offsetX, float offsetY, float offsetZ, Player player) {
        Object builder = this.reflection.createBuilder(this.redstoneParticle);
        this.reflection.builderSetColor(builder, color);
        this.reflection.builderSetLocation(builder, pos);
        this.reflection.builderSetAmount(builder, amount);
        this.reflection.builderSetOffset(builder, offsetX, offsetY, offsetZ);
        this.reflection.builderDisplay(builder, player);
    }

}
