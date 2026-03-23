package de.joriax.gtm.weapons.guns;

public enum AmmoType {
    PISTOL("Pistol Ammo"),
    SMG("SMG Ammo"),
    AR("AR Ammo"),
    SNIPER("Sniper Ammo");

    private final String displayName;

    AmmoType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AmmoType fromString(String s) {
        for (AmmoType t : values()) {
            if (t.name().equalsIgnoreCase(s)) return t;
        }
        return null;
    }
}
