package me.bcawley1.rootwars.gamemodes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class NoBuild extends GameMode{
    public NoBuild() {
        super();
    }
    public static void registerGameMode(){
        GameMode.registerGameMode(NoBuild.class);
    }

    @Override
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.getPlayer().sendMessage("No building in this game mode!");
        event.setCancelled(true);
    }
}
