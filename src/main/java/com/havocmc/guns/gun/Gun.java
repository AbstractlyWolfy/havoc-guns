package com.havocmc.guns.gun;

import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Gun {

    /**
     * Get gun type
     * @return GunType
     */
    GunType type();

    /**
     * Get the damage the weapon should deal when hitting an entity
     * @return damage
     */
    int damage();

    /**
     * Get the range the weapon will shoot, this is determined in blocks
     * @return range
     */
    default int range() { return 30; }

    /**
     * Get the colour of the trail that follows the bullets
     * @return Color
     */
    Color trailColour();

    /**
     * Handle what happens when you hit an entity
     * @param entity - {@link Entity}
     */
    void onHit(Player shooter, Entity entity);

    /**
     * Shoot the bullet
     */
    void shoot(Player shooter);

    /**
     * Handle clean up process
     */
    void clean();
}
