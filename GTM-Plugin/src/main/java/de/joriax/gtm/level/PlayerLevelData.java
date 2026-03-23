package de.joriax.gtm.level;

import java.util.UUID;

public class PlayerLevelData {

    private final UUID uuid;
    private int xp;
    private int level;

    public PlayerLevelData(UUID uuid, int xp, int level) {
        this.uuid = uuid;
        this.xp = xp;
        this.level = level;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
