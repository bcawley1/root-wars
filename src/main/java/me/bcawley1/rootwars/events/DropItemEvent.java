package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.Vote;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropItemEvent implements Listener {

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event){
        if(Vote.isVoting()&&event.getItemDrop().getItemStack().getType().equals(Material.FILLED_MAP)){
            event.getItemDrop().remove();
            event.getPlayer().getInventory().clear();
            Vote.addVote(event.getItemDrop().getItemStack().getItemMeta().getDisplayName(), event.getPlayer().getUniqueId());
            Vote.updateBoard();
        }
    }
}
