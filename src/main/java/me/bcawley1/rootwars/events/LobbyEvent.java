package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.vote.MapVote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyEvent implements Listener {
    public void putPlayerInLobby(Player p){
        p.setMaxHealth(20);
        p.setGameMode(org.bukkit.GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setExp(0);
        p.setHealth(p.getMaxHealth());
        p.clearActivePotionEffects();
        p.teleport(new Location(Bukkit.getWorld("world"),562, 1, 9));
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + "Right click to start game.");
        item.setItemMeta(meta);
        p.getInventory().addItem(item);
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event){
        event.setCancelled(true);
    }
    @EventHandler
    public void openInventory(InventoryOpenEvent event){
        event.setCancelled(true);
    }
    @EventHandler
    public void itemInteract(PlayerInteractEvent event){
        if(event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND)){
            MapVote.startVoting();
            HandlerList.unregisterAll(this);
        }
    }
    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        putPlayerInLobby(event.getPlayer());
    }
    @EventHandler
    public void playerDeath(PlayerDeathEvent event){
        event.setCancelled(true);
    }

}
