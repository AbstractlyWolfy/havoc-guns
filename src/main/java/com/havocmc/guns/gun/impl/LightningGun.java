package com.havocmc.guns.gun.impl;

import com.havocmc.guns.HavocGunsPlugin;
import com.havocmc.guns.gun.Gun;
import com.havocmc.guns.gun.GunType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class LightningGun implements Gun {

    // Bullet been fired
    private Snowball bullet;

    // Armor stand for effects
    private final List<ArmorStand> stands;

    public LightningGun() {
        this.stands = new ArrayList<>();
    }

    @Override
    public GunType type() {
        return GunType.LIGHTNING_GUN;
    }

    @Override
    public int damage() {
        return 2;
    }

    @Override
    public int range() {
        return 15;
    }

    @Override
    public Color trailColour() {
        return Color.fromRGB(135, 237, 255);
    }

    @Override
    public void shoot(Player shooter) {

        // Stops shooting the gun if they previously shot
        if (bullet != null) {
            return;
        }

        if (shooter == null || !shooter.isOnline()) {
            return;
        }

        Location location = shooter.getLocation();
        Vector direction = location.getDirection().normalize();

        bullet = shooter.getWorld().spawn(shooter.getEyeLocation().subtract(0, 0.1, 0), Snowball.class);

        // Handle 1.17 packets
        Bukkit.getOnlinePlayers().stream().filter(nearby -> nearby.getLocation().distance(bullet.getLocation()) < 50).forEach(online -> {

            try {

                // Send a packet to remove the snowball visually.
                Class<?> packetPlayOutEntityDestroy = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
                Constructor<?> packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroy.getConstructor(int.class);

                Object packetPlayOutEntityDestroyObj = packetPlayOutEntityDestroyConstructor.newInstance(bullet.getEntityId());

                Object handle = online.getClass().getMethod("getHandle").invoke(online);
                Object playerConnection = handle.getClass().getField("b").get(handle);
                playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.network.protocol.Packet")).invoke(playerConnection, packetPlayOutEntityDestroyObj);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | InstantiationException e) {
                e.printStackTrace();
            }
        });

        bullet.setCustomNameVisible(false);
        bullet.setCustomName(type().getName());

        bullet.setShooter(shooter);
        bullet.setBounce(false);
        bullet.setSilent(true);

        new BukkitRunnable() {

            public void run() {

                bullet.setVelocity(direction);

                Particle.DustOptions dust = new Particle.DustOptions(trailColour(), 2);
                shooter.getWorld().spawnParticle(Particle.REDSTONE, bullet.getLocation().getX(), bullet.getLocation().getY() + 0.1, bullet.getLocation().getZ(), 0, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), 2.5 , dust);

                double distance = bullet.getLocation().distance(shooter.getLocation());

                if ((bullet.isOnGround() || bullet.isDead()) || distance > range()) {
                    bullet.remove();
                    bullet = null;

                    this.cancel();
                }
            }
        }.runTaskTimer(HavocGunsPlugin.getPlugin(), 0, 1);
    }

    @Override
    public void onHit(Player shooter, Entity entity) {

        // Skip if not a player or a zombie
        if (!(entity instanceof Player || entity instanceof Zombie)) {
            return;
        }

        Location location = entity.getLocation();

        ArmorStand stand = entity.getWorld().spawn(location, ArmorStand.class);
        stand.setVisible(false);
        stand.setInvulnerable(true);

        if (stand.getEquipment() != null) {
            stand.getEquipment().setHelmet(new ItemStack(Material.SOUL_SAND));
        }

        // Add armour stand to the players head to play the relevant effects
        entity.addPassenger(stand);
        stands.add(stand);

        // Remove armour stand effects after 5 seconds
        new BukkitRunnable() {

            @Override
            public void run() {
                stand.remove();

                // Extra precaution in case a glitch happens to remove redundant stands
                stands.forEach(ArmorStand::remove);
            }
        }.runTaskLater(HavocGunsPlugin.getPlugin(), 5 * 20L);
    }

    @Override
    public void clean() {
        // Handle removal of bullet
        if (bullet != null) {
            bullet.remove();
        }
        // Handle removal of armour stand for effects
        if (stands.size() > 0) {
            stands.forEach(ArmorStand::remove);
            stands.clear();
        }
    }
}
