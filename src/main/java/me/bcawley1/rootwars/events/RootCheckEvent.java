package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.util.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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
        if (team.equals(GameTeam.getTeam(event.getPlayer().getUniqueId())) && GameTeam.getTeam(event.getPlayer().getUniqueId()).getTeamData().getRootLocation().equals(event.getBlock().getLocation()) && !RootWars.getPlugin().getConfig().getBoolean("can-break-own-root")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break your own root anymore \uD83D\uDE14");
        } else {
            breakRoot(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void blockBurn(BlockBurnEvent event) {
        breakRoot(event.getBlock().getLocation());
    }

    @EventHandler
    public void blockExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            breakRoot(block.getLocation());
        }
    }

    @EventHandler
    public void blockPushByPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            breakRoot(block.getLocation());
        }
    }

    public void register() {
        if (!isRegistered) {
            Bukkit.getPluginManager().registerEvents(this, RootWars.getPlugin());
            isRegistered = true;
        }
    }

    public void cancel() {
        if (isRegistered) {
            HandlerList.unregisterAll(this);
            isRegistered = false;
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }
}