package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class EntityInteractEvent implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof Villager){
            Map<String, Inventory> shopMenus = RootWars.getTeamFromPlayer(event.getPlayer()).getItemShop(event.getPlayer());
            event.getPlayer().openInventory(shopMenus.get("quickBuy"));
        }
    }
}
