package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.GameMap;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceEvent implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        GameMap currentMap = RootWars.getCurrentMap();
        Location blockLocation = event.getBlock().getLocation();
        event.getPlayer().sendMessage("map: %s, blockpos: %s, mapBorders: %s".formatted(currentMap.getMapName(), event.getBlock().getLocation(), currentMap.getMapBorder().toString()));
        if(!(blockLocation.getX()>currentMap.getMapBorder().get("negX")&&blockLocation.getX()<currentMap.getMapBorder().get("posX")&&
                blockLocation.getY()>currentMap.getMapBorder().get("negY")&&blockLocation.getY()<currentMap.getMapBorder().get("posY")&&
                blockLocation.getZ()>currentMap.getMapBorder().get("negZ")&&blockLocation.getZ()<currentMap.getMapBorder().get("posZ"))){
            event.setCancelled(true);
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks outside of the map.");
        }
    }
}
