package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Generator {
    private JavaPlugin plugin;
    private static Map<Location, Integer> taskIDs = new HashMap<>();
    private ItemStack item;

    public Generator(JavaPlugin plugin, int x, int y, int z, ItemStack item, long delay) {
        this.plugin = plugin;
        this.item = item;
        World world = Bukkit.getServer().getWorld("world");
        Location location = new Location(world, x, y, z);
        Location locationoffset = new Location(world, x+0.5, y, z+0.5);
        taskIDs.put(location, Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
            world.dropItem(locationoffset, item);
        }, 0, 20).getTaskId());
    }

    public static boolean containsGenerator(Location location){
        return taskIDs.containsKey(location);
    }

    public static void removeGenerator(Location location){
        if(taskIDs.containsKey(location)) {
            int taskID = taskIDs.get(location);
            Bukkit.getServer().getScheduler().cancelTask(taskID);
            taskIDs.remove(location);
        }
    }
}
