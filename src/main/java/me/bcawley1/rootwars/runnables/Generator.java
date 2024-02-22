package me.bcawley1.rootwars.runnables;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.util.GeneratorData;
import me.bcawley1.rootwars.util.GeneratorItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Generator {
    private static List<UUID> droppedItems = new ArrayList<>();
    private final GeneratorData[] generatorData;
    private int stage;
    private final Runnable runnable;
    private BukkitTask task;

    public Generator(Location location, GeneratorData... generatorData) {
        this.stage = 0;
        this.generatorData = generatorData;
        runnable = () -> {
            float randomNumber = new Random().nextFloat(0, 100);
            int itemChance = 0;
            for (GeneratorItem item : generatorData[stage].items()) {
                itemChance += item.chance();
                if (randomNumber <= itemChance) {
                    droppedItems.add(RootWars.getWorld().dropItem(location, new ItemStack(item.item())).getUniqueId());
                    break;
                }
            }
        };
    }

    public static boolean droppedByGenerator(Item item) {
        return droppedItems.contains(item.getUniqueId());
    }

    public static void deleteGeneratorItem(Item item) {
        if (droppedItems.contains(item.getUniqueId())) {
            droppedItems.remove(item.getUniqueId());
            item.remove();
        }
    }

    public void upgradeGenerator(){
        stage++;
        task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), runnable, 0, generatorData[stage].delay());
    }

    public void startGenerator() {
        if(task==null) {
            task = Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), runnable, 0, generatorData[stage].delay());
        }
    }
    public void stopGenerator(){
        task.cancel();
    }
}
