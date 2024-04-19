package codes.wasabi.xclaim.particle.spigot;

import codes.wasabi.xclaim.particle.ParticleService;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.awt.Color;

public class SpigotParticleService extends ParticleService {

    @Override
    public void displayRedstoneParticle(Color color, Location pos, int amount, float offsetX, float offsetY, float offsetZ, Player player) {
        Particle.DustOptions opts = new Particle.DustOptions(org.bukkit.Color.fromRGB(color.getRGB()), 1);
        player.spawnParticle(Particle.REDSTONE, pos, amount, offsetX, offsetY, offsetZ, opts);
    }

}
