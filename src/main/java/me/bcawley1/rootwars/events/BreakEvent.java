package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.GameTeam;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakEvent implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        for(GameTeam team : RootWars.getTeams().values()){
            if(event.getBlock().getLocation().equals(team.getRootLocation())){
                if(RootWars.getTeamFromPlayer(event.getPlayer()).equals(team)){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot break your own root anymore \uD83D\uDE14");
                }
            }
        }
    }
}
