package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.Shop;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityInteractEvent implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof Villager){
            event.getPlayer().openInventory(Shop.getInventoryTab(event.getPlayer(), "Blocks"));
        }
    }
}
