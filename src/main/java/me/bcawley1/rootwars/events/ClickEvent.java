package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.PlayerCooldown;
import me.bcawley1.rootwars.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent implements Listener {

    PlayerCooldown buyCooldown = new PlayerCooldown();

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getOriginalTitle().equalsIgnoreCase("Quick Buy")) {
            if (ShopItem.hasShopItem(event.getCurrentItem())) {
                ShopItem.getShopItemFromItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
    }
}
