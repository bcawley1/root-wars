package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.PlayerCooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClickEvent implements Listener {

    PlayerCooldown buyCooldown = new PlayerCooldown();

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getOriginalTitle().equalsIgnoreCase("Store")){
            switch (event.getCurrentItem().getType()){
                case BLUE_WOOL:
                    if(buyCooldown.getCooldown(player.getUniqueId())==0){
                        player.getInventory().addItem(new ItemStack(Material.BLUE_WOOL, 16));
                        buyCooldown.setCooldown(player.getUniqueId(), 200);
                    } else {
                        player.sendMessage("You are currently on cooldown for %s seconds.".formatted(buyCooldown.getCooldown(player.getUniqueId())/1000.0));
                    }

                    break;
                case RED_WOOL:
                    player.getInventory().addItem(new ItemStack(Material.RED_WOOL, 16));
                    break;
            }
            event.setCancelled(true);
        }
    }
}
