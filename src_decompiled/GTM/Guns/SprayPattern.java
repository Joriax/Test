/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package Guns;

import java.util.Random;
import org.bukkit.util.Vector;

public class SprayPattern {
    private final double maxDeviation;
    private final Random random;

    public SprayPattern(double maxDeviation) {
        this.maxDeviation = maxDeviation;
        this.random = new Random();
    }

    public Vector applySpray(Vector direction) {
        if (this.maxDeviation <= 0.0) {
            return direction.clone();
        }
        double deviationX = (this.random.nextDouble() * 2.0 - 1.0) * this.maxDeviation;
        double deviationY = (this.random.nextDouble() * 2.0 - 1.0) * this.maxDeviation;
        double deviationZ = (this.random.nextDouble() * 2.0 - 1.0) * this.maxDeviation;
        Vector sprayDirection = direction.clone();
        sprayDirection.add(new Vector(deviationX, deviationY, deviationZ));
        return sprayDirection.normalize();
    }

    public double getMaxDeviation() {
        return this.maxDeviation;
    }
}

