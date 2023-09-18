package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class DeathEvent implements Listener {
    private static JavaPlugin plugin;
    private static Map<Player, Integer> respawnTimers = new HashMap<>();
    private static Map<Player, Integer> respawnTimerID = new HashMap<>();

    public static void setPlugin(JavaPlugin plugin) {
        DeathEvent.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        event.setCancelled(true);
        event.getPlayer().setHealth(20);
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        RootWars.getTeamFromPlayer(event.getPlayer()).respawnPlayer(event.getPlayer());
        if(RootWars.getTeamFromPlayer(event.getPlayer()).isRoot()){
            respawnTimers.put(event.getPlayer(), 6);
            respawnTimerID.put(event.getPlayer(), Bukkit.getScheduler().runTaskTimer(plugin,() -> {
                respawnTimers.merge(event.getPlayer(), -1, Integer::sum);
                event.getPlayer().sendTitle(String.valueOf(respawnTimers.get(event.getPlayer())),"");
                if(respawnTimers.get(event.getPlayer())<=0){
                    event.getPlayer().setGameMode(GameMode.SURVIVAL);
                    RootWars.getTeamFromPlayer(event.getPlayer()).respawnPlayer(event.getPlayer());
                    Bukkit.getScheduler().cancelTask(respawnTimerID.get(event.getPlayer()));
                }
            }, 0, 20).getTaskId());
        }
    }
}
