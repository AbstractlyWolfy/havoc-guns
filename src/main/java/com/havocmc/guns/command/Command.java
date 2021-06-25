package com.havocmc.guns.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class Command extends BukkitCommand {

    /** Actual command. */
    private final String command;

    /** Command required permission. */
    private final String permission;

    /**
     * Create a new command without permission.
     * @param command - command.
     */
    public Command(String command) {
        this(command, null, null);
    }

    /**
     * Create a new command with permission.
     * @param command - command.
     * @param aliases - aliases.
     */
    public Command(String command, String[] aliases) {
        this(command, aliases, null);
    }

    /**
     * Create a new command with permission.
     * @param command - command.
     * @param permission - permissions.
     */
    public Command(String command, String permission) {
        this(command, null, permission);
    }

    /**
     * Create a new command with permission.
     * @param command - command.
     * @param permission - permission.
     */
    public Command(String command, String[] aliases, String permission) {
        super(command);

        this.command = command;
        this.permission = permission;

        if (aliases != null) {
            setAliases(Arrays.asList(aliases));
        }

        register();
    }

    /**
     * Execute a command.
     * @param sender - sender.
     * @param args - args.
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Register the command in Minecraft.
     */
    public void register() {

        try {

            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            final CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());
            setPermission(permission);
            commandMap.register(this.command, this);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(@Nonnull CommandSender sender, @Nonnull String commandLabel, @Nonnull String[] args) {

        if (!(sender instanceof Player)) {
            this.execute(sender, args);
            return true;
        }

        Player player = (Player) sender;

        if (permission != null && !player.hasPermission(permission)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        this.execute(sender, args);
        return true;
    }

    public String getCommand() {
        return command;
    }
}
