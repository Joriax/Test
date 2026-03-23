/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Listener
 */
package de.joriax.spigotAdminSystem.Utils.TPA;

import de.joriax.spigotAdminSystem.Utils.TPA.TPAManager;
import org.bukkit.event.Listener;

public class MovementListener
implements Listener {
    private final TPAManager tpaManager;

    public MovementListener(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }
}

