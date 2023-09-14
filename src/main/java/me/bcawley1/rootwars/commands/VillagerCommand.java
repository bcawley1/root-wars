package me.bcawley1.rootwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VillagerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("villager")) {
            if (commandSender instanceof Player) {
                int x, y, z;
                World world = Bukkit.getWorld("world");
                try {
                    x = Integer.parseInt(strings[0]);
                    y = Integer.parseInt(strings[1]);
                    z = Integer.parseInt(strings[2]);
                } catch (Exception e) {
                    return false;
                }
                world.spawnEntity(new Location(world, x, y, z), EntityType.VILLAGER);
                return true;
            }
        }
        return false;
    }
}
