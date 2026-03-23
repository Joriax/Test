package de.joriax.gtm.weapons.guns;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RecoilHandler {

    private final double recoilStrength;

    public RecoilHandler(double recoilStrength) {
        this.recoilStrength = recoilStrength;
    }

    public void applyRecoil(Player player) {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        // Apply upward recoil effect
        double recoilY = recoilStrength * 0.05;
        Vector direction = player.getLocation().getDirection().normalize();
        direction.setY(direction.getY() + recoilY);
        direction.normalize();

        // Teleport with slight pitch change to simulate recoil
        org.bukkit.Location loc = player.getLocation().clone();
        loc.setPitch(Math.max(-90, pitch - (float)(recoilStrength * 2)));
        player.teleport(loc);
    }

    public double getRecoilStrength() {
        return recoilStrength;
    }
}
