package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.Generator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PickupEvent implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event){
        if(Generator.droppedByGenerator(event.getItem())){
            Generator.deleteGeneratorItem(event.getItem());
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                if(Math.abs(p.getLocation().getX()-event.getEntity().getLocation().getX())<1.3||Math.abs(p.getLocation().getY()-event.getEntity().getLocation().getY())<1.3){
                    p.getInventory().addItem(event.getItem().getItemStack());
                }
            }

            event.setCancelled(true);
        }
    }
}
