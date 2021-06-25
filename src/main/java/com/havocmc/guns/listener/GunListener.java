package com.havocmc.guns.listener;

import com.havocmc.guns.gun.Gun;
import com.havocmc.guns.gun.user.GunUser;
import com.havocmc.guns.manager.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GunListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        
        if (meta == null) {
            return;
        }

        GunUser user = UserManager.get(player.getUniqueId());

        if (user == null) {
            return;
        }

        String name = item.getItemMeta().getDisplayName();
        Gun gun = user.getGunByName(ChatColor.stripColor(name));

        if (gun == null) {
            return;
        }
        
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getMaterial() == gun.type().getItem()) {
            event.setCancelled(true);
            gun.shoot(player);
        }
    }

    @EventHandler
    public void onBulletInteract(ProjectileHitEvent event) {

        Entity entity = event.getEntity();

        // Ensure projectile is a snowball.
        if (entity instanceof Snowball) {

            Snowball snowball = (Snowball) entity;

            // Ensure the snowball shooter was a player and not another entity, and that the snowball is silent to stop
            // Normal players using hand held snowballs as bullets.
            if (snowball.getShooter() instanceof Player && snowball.isSilent()) {

                Player shooter = (Player) snowball.getShooter();

                GunUser user = UserManager.get(shooter.getUniqueId());

                if (user == null) {
                    return;
                }

                String name = snowball.getCustomName();
                Gun gun = user.getGunByName(ChatColor.stripColor(name));

                if (gun != null) {

                    Entity hitEntity = event.getHitEntity();

                    // Handles onHit for bullets when colliding with entities.
                    if (hitEntity != null) {
                        gun.onHit(shooter, hitEntity);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageOverride(EntityDamageByEntityEvent event) {

        // Ensure the projectile/damager was a snowball.
        if (event.getDamager() instanceof Snowball) {

            Snowball snowball = (Snowball) event.getDamager();

            if (snowball.getShooter() instanceof Player) {

                GunUser user = UserManager.get(((Player) snowball.getShooter()).getUniqueId());

                if (user == null) {
                    return;
                }

                String name = snowball.getCustomName();
                Gun gun = user.getGunByName(ChatColor.stripColor(name));

                if (gun != null) {

                    if (gun.damage() <= 0) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setDamage(gun.damage());
                }
            }
        }
    }
}
