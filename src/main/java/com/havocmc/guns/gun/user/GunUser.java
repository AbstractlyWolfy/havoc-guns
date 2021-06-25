package com.havocmc.guns.gun.user;

import com.havocmc.guns.gun.Gun;
import com.havocmc.guns.gun.GunType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GunUser {

    private final UUID uuid;

    // Loaded guns in memory
    private Map<Integer, Gun> slots;

    public GunUser(UUID uuid) {
        this.uuid = uuid;
        this.slots = new HashMap<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void load() {

        Player online = Bukkit.getPlayer(uuid);

        if (online == null) {
            return;
        }

        Map<Integer, Gun> gunSlots = new HashMap<>();

        // Iterate through slots
        for (int i = 0; i < online.getInventory().getSize(); i++) {
            ItemStack item = online.getInventory().getItem(i);

            if (item == null || item.getItemMeta() == null) {
                continue;
            }

            GunType type = GunType.isGun(item);

            if (type == null) {
                continue;
            }

            try {
                gunSlots.put(i, type.createGun());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        slots.clear();
        slots = gunSlots;
    }

    public void addGun(int slot, GunType type) {

        try {
            slots.put(slot, type.createGun());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swapGun(int slotA, int slotB) {
        Gun gunA = slots.getOrDefault(slotA, null);
        Gun gunB = slots.getOrDefault(slotB, null);

        if (gunA == null) {
            return;
        }

        slots.put(slotB, gunA);
        slots.put(slotA, gunB);
    }

    public void removeGun(int slot) {
        slots.remove(slot);
    }

    /**
     * Get a gun by its slot
     * @param slot - {@link Integer}
     * @return gun - {@link Gun}
     */
    public Gun getGunBySlot(int slot) {
        return slots.getOrDefault(slot, null);
    }

    /**
     * Get a gun by its name
     * @param name - name
     * @return gun - {@link Gun}
     */
    public Gun getGunByName(String name) {
        return slots.values().stream().filter(gun -> gun.type().getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Get all guns in slots in the inventory
     * @return Slots and Guns
     */
    public Map<Integer, Gun> getGuns() {
        return slots;
    }
}
