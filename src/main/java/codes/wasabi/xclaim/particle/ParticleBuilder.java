package codes.wasabi.xclaim.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.Color;

public class ParticleBuilder {

    // color, location, amount, offset, player
    private final ParticleEffect effect;
    private Color color = Color.WHITE;
    private Location loc = null;
    private int amount = 1;
    private float offsetX = 0f;
    private float offsetY = 0f;
    private float offsetZ = 0f;
    public ParticleBuilder(ParticleEffect effect) {
        this.effect = effect;
    }

    public ParticleBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public ParticleBuilder setLocation(Location loc) {
        this.loc = loc;
        return this;
    }

    public ParticleBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ParticleBuilder setOffset(float offsetX, float offsetY, float offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        return this;
    }

    public void display(Player player) {
        if (this.effect != ParticleEffect.REDSTONE) return;
        ParticleService.get()
                .displayRedstoneParticle(this.color, this.loc == null ? player.getLocation() : loc, this.amount, this.offsetX, this.offsetY, this.offsetZ, player);
    }

}
