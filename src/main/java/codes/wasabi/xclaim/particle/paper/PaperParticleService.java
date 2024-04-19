package codes.wasabi.xclaim.particle.paper;

import codes.wasabi.xclaim.particle.ParticleService;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.awt.Color;

public class PaperParticleService extends ParticleService {

    @Override
    public void displayRedstoneParticle(Color color, Location pos, int amount, float offsetX, float offsetY, float offsetZ, Player player) {
        ParticleBuilder pb = new ParticleBuilder(Particle.REDSTONE);
        pb.location(pos)
                .color(color.getRed(), color.getGreen(), color.getBlue())
                .count(amount)
                .offset(offsetX, offsetY, offsetZ)
                .receivers(player)
                .spawn();
    }

}
