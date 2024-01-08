package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;

import java.util.*;

public class Generator {
    private static Map<Location, Integer> taskIDs = new HashMap<>();
    private static List<UUID> droppedItems = new ArrayList<>();

    public Generator(int x, int y, int z, long delay, List<GeneratorItem> items) {
        items.sort(new GeneratorItem.GeneratorItemComparator());
        World world = Bukkit.getServer().getWorld("world");
        Location location = new Location(world, x, y, z);
        Location locationoffset = new Location(world, x + 0.5, y, z + 0.5);

        taskIDs.put(location, Bukkit.getServer().getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            float randomNumber = new Random().nextFloat(0, 100);
            int itemChance = 0;
            for (GeneratorItem item : items) {
                itemChance += item.chance();
                if (randomNumber <= itemChance) {
                    droppedItems.add(world.dropItem(locationoffset, item.item()).getUniqueId());
                    break;
                }
            }
        }, 0, delay).getTaskId());
    }

    public static boolean containsGenerator(Location location) {
        return taskIDs.containsKey(location);
    }

    public static void removeGenerator(Location location) {
        if (taskIDs.containsKey(location)) {
            int taskID = taskIDs.get(location);
            Bukkit.getServer().getScheduler().cancelTask(taskID);
            taskIDs.remove(location);
        }
    }
    public static boolean droppedByGenerator(Item item){
        return droppedItems.contains(item.getUniqueId());
    }

    public static void deleteGeneratorItem(Item item){
        if(droppedItems.contains(item.getUniqueId())){
            droppedItems.remove(item.getUniqueId());
            item.remove();
        }
    }
}
