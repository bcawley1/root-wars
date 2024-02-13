package me.bcawley1.rootwars.gamemodes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class NoBuild extends GameMode{
    public NoBuild() {
        super("nobuild");
    }

    @Override
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.getPlayer().sendMessage("Remember, no blocks silly!");
        event.setCancelled(true);
    }
}
