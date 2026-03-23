package de.joriax.gtm.weapons.guns;

import java.util.Random;

public class SprayPattern {

    private static final Random RANDOM = new Random();

    private final double spread;

    public SprayPattern(double spread) {
        this.spread = spread;
    }

    /**
     * Returns a random direction offset based on the spread value.
     */
    public double[] getOffset() {
        double x = (RANDOM.nextDouble() - 0.5) * spread;
        double y = (RANDOM.nextDouble() - 0.5) * spread;
        double z = (RANDOM.nextDouble() - 0.5) * spread;
        return new double[]{x, y, z};
    }

    public double getSpread() {
        return spread;
    }
}
