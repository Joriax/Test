/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package Guns;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RecoilHandler {
    private final double recoilStrength;

    public RecoilHandler(double recoilStrength) {
        this.recoilStrength = recoilStrength;
    }

    public void applyRecoil(Player player) {
        if (!player.isSneaking() && this.recoilStrength > 0.0) {
            Vector direction = player.getEyeLocation().getDirection().normalize();
            Vector recoil = direction.clone().multiply(-this.recoilStrength);
            player.setVelocity(player.getVelocity().add(recoil));
        }
    }

    public double getRecoilStrength() {
        return this.recoilStrength;
    }
}

