package com.havocmc.guns.gun;

import com.havocmc.guns.gun.impl.LightningGun;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GunType {

    LIGHTNING_GUN(
            "Lightning Gun",
            new String[] { "Shoots electric bolts which", "shock enemies" },
            Material.PRISMARINE_BRICKS,
            LightningGun.class
    ),
    ;

    private final String name;
    private final String[] lore;
    private final Material item;

    private final Class<? extends Gun> clazz;

    GunType(String name, String[] lore, Material item, Class<? extends Gun> clazz) {
        this.name = name;
        this.lore = lore;
        this.item = item;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public String[] getLore() {
        return lore;
    }

    public Material getItem() {
        return item;
    }

    public Class<? extends Gun> getClazz() {
        return clazz;
    }

    public <T> T createGun() throws IllegalAccessException, InstantiationException {
        return (T) clazz.newInstance();
    }

    public static GunType isGun(ItemStack item) {

        if (item == null || item.getItemMeta() == null) {
            return null;
        }

        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        for (GunType type : GunType.values()) {

            if (itemName.equals(type.getName())) {
                return type;
            }
        }

        return null;
    }
}
