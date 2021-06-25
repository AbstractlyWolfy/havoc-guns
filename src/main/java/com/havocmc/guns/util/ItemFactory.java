package com.havocmc.guns.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory extends Factory<ItemStack> {

    /** {@link ItemMeta} for the {@link ItemStack}. */
    private ItemMeta itemMeta;

    /**
     * Construct a new ItemStack from a material.
     * @param material - the material.
     */
    public ItemFactory(Material material) {
        this(material, 1, (byte) 0);
    }

    /**
     * Construct a new ItemStack from a material id.
     * @param materialID - the material id.
     */
    public ItemFactory(int materialID) {
        this(materialID, 1, (byte) 0);
    }

    /**
     * Construct a new ItemStack from a material with an amount.
     * @param material - the material.
     * @param amount - the amount.
     */
    public ItemFactory(Material material, int amount) {
        this(material, amount, (byte) 0);
    }

    /**
     * Construct a new ItemStack from an id with an amount.
     * @param materialID - the material.
     * @param amount - the amount.
     */
    public ItemFactory(int materialID, int amount) {
        this(materialID, amount, (byte) 0);
    }


    /**
     * Construct a new ItemStack from a material with an amount and byte data..
     * @param material - the material.
     * @param amount - the amount.
     * @param data - the data.
     */
    public ItemFactory(Material material, int amount, byte data) {
        object = new ItemStack(material, amount, data);
        itemMeta = object.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    /**
     * Construct a new ItemStack from an id with an amount and byte data..
     * @param materialID - the material.
     * @param amount - the amount.
     * @param data - the data.
     */
    public ItemFactory(int materialID, int amount, byte data) {
        Material material = Material.getMaterial(String.valueOf(materialID));

        if (material == null) {
            return;
        }

        object = new ItemStack(material, amount);
        itemMeta = object.getItemMeta();

        if (itemMeta == null) {
            return;
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    /**
     * Set the Display name of the {@link ItemStack}.
     * @param displayName - displayname.
     */
    public ItemFactory setDisplayName(String displayName) {
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        return this;
    }

    /**
     * Set the amount of the {@link ItemStack}
     * @param amount - amount
     */
    public ItemFactory setAmount(int amount) {
        object.setAmount(amount);
        return this;
    }

    /**
     * Set the lore of an {@link ItemStack}.
     * @param lore - the lore.
     */
    public ItemFactory setLore(String... lore) {
        List<String> currentLore = new ArrayList<>();
        for (String lorePart : lore) {
            currentLore.add(ChatColor.translateAlternateColorCodes('&', lorePart));
        }
        itemMeta.setLore(currentLore);
        return this;
    }

    /**
     * Append the current lore of an {@link ItemStack}.
     * @param lore - the additions to the lore.
     */
    public ItemFactory appendLore(String... lore) {
        List<String> currentLore = (itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore());
        for (String lorePart : lore) {
            currentLore.add(ChatColor.translateAlternateColorCodes('&', lorePart));
        }
        itemMeta.setLore(currentLore);
        return this;
    }

    /**
     * Enchant the {@link ItemStack}
     * @param enchantments - the enchantments.
     */
    public ItemFactory enchant(Enchantment... enchantments) {
        for (Enchantment enchantment : enchantments) {
            itemMeta.addEnchant(enchantment, 1, false);
        }
        return this;
    }

    /**
     * Add itemflags to {@link ItemStack}
     * @param flags - flags
     */
    public ItemFactory addItemFlag(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    /**
     * Set the durability of an item.
     * @param durability - durability.
     */
    public ItemFactory setDurability(short durability) {
        object.setDurability(durability);
        return this;
    }

    /**
     * Make the item unbreakable.
     */
    public ItemFactory setUnbreakable() {
        itemMeta.setUnbreakable(true);
        return this;
    }

    /**
     * Construct the final {@link ItemStack}.
     * @return ItemStack
     */
    @Override
    public ItemStack build() {
        object.setItemMeta(itemMeta);
        return object;
    }
}
