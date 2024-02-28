package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.util.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class RootCheckEvent implements Listener {
    private final GameTeam team;
    private boolean isRegistered;

    public RootCheckEvent(GameTeam team) {
        this.team = team;
        isRegistered = false;
    }

    private void breakRoot(Location loc) {
        if (loc.equals(team.getTeamData().getRootLocation())) {
            team.breakRoot();
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        breakRoot(event.getBlock().getLocation());
    }

    @EventHandler
    public void blockBurn(BlockBurnEvent event) {
        breakRoot(event.getBlock().getLocation());
    }

    @EventHandler
    public void blockExplode(BlockExplodeEvent event) {
        breakRoot(event.getBlock().getLocation());
    }

    @EventHandler
    public void blockPushByPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            breakRoot(block.getLocation());
        }
    }

    public void register(){
        if(!isRegistered) {
            Bukkit.getPluginManager().registerEvents(this, RootWars.getPlugin());
            isRegistered = true;
        }
    }
    public void cancel(){
        if(isRegistered) {
            HandlerList.unregisterAll(this);
            isRegistered = false;
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }
}