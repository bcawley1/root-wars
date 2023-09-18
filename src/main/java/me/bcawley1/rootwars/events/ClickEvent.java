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
                if (player.getInventory().containsAtLeast(ShopItem.getShopItemFromItem(event.getCurrentItem()).getCostItem(), ShopItem.getShopItemFromItem(event.getCurrentItem()).getCostItem().getAmount())) {
                    if (ShopItem.getShopItemFromItem(event.getCurrentItem()).doesGiveListedItem()) {
                        player.getInventory().removeItem(ShopItem.getShopItemFromItem(event.getCurrentItem()).getCostItem());
                        player.getInventory().addItem(ShopItem.getShopItemFromItem(event.getCurrentItem()).getPurchasedItem());
                    }
                } else {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
                }
            }
            event.setCancelled(true);
        }
    }
}
