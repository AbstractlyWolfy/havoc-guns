package com.havocmc.guns;

import com.havocmc.guns.command.impl.GunCommand;
import com.havocmc.guns.gun.GunType;
import com.havocmc.guns.gun.user.GunUser;
import com.havocmc.guns.listener.GunListener;
import com.havocmc.guns.listener.UserListener;
import com.havocmc.guns.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HavocGunsPlugin extends JavaPlugin {

    // Instance of this plugin
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        super.onEnable();

        // Initalise instance
        plugin = this;

        Bukkit.getOnlinePlayers().forEach(online -> UserManager.get(online.getUniqueId()));

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new UserListener(), this);
        Bukkit.getPluginManager().registerEvents(new GunListener(), this);

        // Register new command per gun to meet the specification
        for (GunType type : GunType.values()) {
            String name = type.name().toLowerCase().replace("_", "");
            new GunCommand(name, type).register();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Bukkit.getOnlinePlayers().forEach(online -> {
            GunUser user = UserManager.get(online.getUniqueId());

            if (user == null) {
                return;
            }

            user.getGuns().forEach((slot, gun) -> gun.clean());
            UserManager.remove(online.getUniqueId());
        });
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
