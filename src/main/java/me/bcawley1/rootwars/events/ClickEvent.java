package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.PlayerCooldown;
import me.bcawley1.rootwars.Shop;
import me.bcawley1.rootwars.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent implements Listener {

    PlayerCooldown buyCooldown = new PlayerCooldown();

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(Shop.containsTab(event.getView().getOriginalTitle())) {
            if(Shop.isTopBar(event.getCurrentItem())){
                Shop.getTopBarAction(event.getCurrentItem()).getAction().accept((Player) event.getWhoClicked(), new ShopItem(Material.IRON_GOLEM_SPAWN_EGG, 1, Material.IRON_INGOT, 1, "not null"));
            }
            if (ShopItem.hasShopItem(event.getCurrentItem())) {
                ShopItem.getShopItemFromItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
    }
}
