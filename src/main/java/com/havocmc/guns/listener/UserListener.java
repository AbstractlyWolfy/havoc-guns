package com.havocmc.guns.listener;

import com.havocmc.guns.HavocGunsPlugin;
import com.havocmc.guns.gun.GunType;
import com.havocmc.guns.gun.user.GunUser;
import com.havocmc.guns.manager.UserManager;
import com.mojang.authlib.yggdrasil.response.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class UserListener implements Listener {

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserManager.get(player.getUniqueId());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        GunUser user = UserManager.get(player.getUniqueId());

        if (user == null) {
            return;
        }

        user.getGuns().forEach((slot, gun) -> gun.clean());
        UserManager.remove(player.getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        GunUser user = UserManager.get(player.getUniqueId());

        if (user == null) {
            return;
        }

        user.getGuns().forEach((slot, gun) -> gun.clean());
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        GunType type = GunType.isGun(item);

        if (type == null) {
            return;
        }

        GunUser user = UserManager.get(player.getUniqueId());

        if (user == null) {
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                user.load();
                System.out.println("RAN LOAD");
            }
        }.runTaskLater(HavocGunsPlugin.getPlugin(), 10L);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        GunType type = GunType.isGun(item);

        if (type == null) {
            return;
        }

        GunUser user = UserManager.get(player.getUniqueId());

        if (user == null) {
            return;
        }

        user.removeGun(player.getInventory().getHeldItemSlot());
    }
}
