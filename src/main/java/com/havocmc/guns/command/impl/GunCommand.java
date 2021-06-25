package com.havocmc.guns.command.impl;

import com.havocmc.guns.command.Command;
import com.havocmc.guns.gun.Gun;
import com.havocmc.guns.gun.GunType;
import com.havocmc.guns.gun.user.GunUser;
import com.havocmc.guns.manager.UserManager;
import com.havocmc.guns.util.ItemFactory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GunCommand extends Command {

    private final GunType gunType;

    /**
     * Create a new instance of {@link GunCommand}
     * - Plugin outline also requests "lightninggun" command, which is an alias in this case.
     */
    public GunCommand(String name, GunType gunType) {
        super(name,"havoc.guns." + name);
        this.gunType = gunType;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            System.out.println("You must be a player to use this command.");
            return;
        }

        Player player = (Player) sender;
        GunUser user = UserManager.get(player.getUniqueId());

        if (user == null) {
            return;
        }

        try {

            Gun gun = gunType.createGun();

            player.getInventory().addItem(new ItemFactory(gun.type().getItem())
                    .setDisplayName("&b&l" + StringUtils.capitalize(gun.type().getName()))
                    .setLore(gun.type().getLore())
                    .appendLore(" ", "&eRight click &7to fire.")
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .build());


            user.load();
            player.sendMessage(ChatColor.GREEN + "You have been given a " + gun.type().getName());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error spawning this gun! " + e.getMessage());
        }
    }
}
