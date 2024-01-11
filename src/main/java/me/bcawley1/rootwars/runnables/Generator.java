package me.bcawley1.rootwars.runnables;

import me.bcawley1.rootwars.util.GeneratorData;
import me.bcawley1.rootwars.util.GeneratorItem;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Generator extends BukkitRunnable {
    private static List<UUID> droppedItems = new ArrayList<>();
    private final Location location;
    private final GeneratorData generatorData;

    public Generator(Location location, GeneratorData generatorData) {
        this.generatorData = generatorData;
        this.location = location;
        this.location.add(0.5,0,0.5);
        this.runTaskTimer(RootWars.getPlugin(), 0, generatorData.delay());
    }

    public void removeGenerator() {
        this.cancel();
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

    @Override
    public void run() {
        float randomNumber = new Random().nextFloat(0, 100);
        int itemChance = 0;
        for (GeneratorItem item : generatorData.items()) {
            itemChance += item.chance();
            if (randomNumber <= itemChance) {
                droppedItems.add(RootWars.getWorld().dropItem(location, item.item()).getUniqueId());
                break;
            }
        }
    }
}
