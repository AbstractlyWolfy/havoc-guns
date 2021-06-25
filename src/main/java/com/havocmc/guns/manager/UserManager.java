package com.havocmc.guns.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.havocmc.guns.gun.Gun;
import com.havocmc.guns.gun.GunType;
import com.havocmc.guns.gun.user.GunUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserManager {

    // Users
    private static final LoadingCache<UUID, GunUser> users;

    static {

        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS);

        // Create a removal listener to handle saving data upon removal of user.
        builder.removalListener(notification -> {
            GunUser user = (GunUser) notification.getValue();
            user.getGuns().forEach((slot, gun) -> gun.clean());
        });

        // Build the cache loader to handle loading user data.
        users = builder.build(new CacheLoader<UUID, GunUser>() {

            @Override
            public GunUser load(@Nonnull UUID uuid) {
                GunUser user = new GunUser(uuid);
                user.load();
                return user;
            }
        });
    }

    /**
     * Get a {@link GunUser} from the cache.
     * @param uuid - uuid.
     * @return SpellUser
     */
    public static GunUser get(@Nonnull UUID uuid) {

        try {
            return users.get(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Remove a user from the cache and save the appropriate data.
     * @param uuid - uuid.
     */
    public static void remove(@Nonnull UUID uuid) {
        GunUser user = get(uuid);

        if (user == null) {
            return;
        }

        users.invalidate(uuid);
    }
}
