package me.bcawley1.rootwars.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EntityInteractEvent implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof Villager){
            Inventory shop = Bukkit.createInventory(event.getPlayer(), 54, "Store");
            shop.addItem(new ItemStack(Material.BLUE_WOOL,16));
            shop.addItem(new ItemStack(Material.RED_WOOL,16));
            event.getPlayer().openInventory(shop);
        }
    }
}
