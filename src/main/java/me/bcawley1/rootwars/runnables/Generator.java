package me.bcawley1.rootwars.runnables;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.util.GeneratorData;
import me.bcawley1.rootwars.util.GeneratorItem;
import me.bcawley1.rootwars.util.Upgradable;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Generator extends BukkitRunnable implements Upgradable{
    private static List<UUID> droppedItems = new ArrayList<>();
    private final Location location;
    private final GeneratorData generatorData;
    private int upgradeValue;

    public Generator(Location location, GeneratorData generatorData) {
        upgradeValue=0;
        this.generatorData = generatorData;
        this.location = location;
        this.location.add(0.5,0,0.5);
    }

    public GeneratorData getGeneratorData() {
        return generatorData;
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
    public void startGenerator(){
        this.runTaskTimer(RootWars.getPlugin(), 0, generatorData.delay());
    }

    @Override
    public void run() {
        float randomNumber = new Random().nextFloat(0, 100);
        int itemChance = 0;
        for (GeneratorItem item : generatorData.items()) {
            itemChance += item.chance();
            if (randomNumber <= itemChance) {
                droppedItems.add(RootWars.getWorld().dropItem(location, new ItemStack(item.item())).getUniqueId());
                break;
            }
        }
    }

    @Override
    public void upgrade() {
        RootWars.getCurrentGameMode().get
    }
}
