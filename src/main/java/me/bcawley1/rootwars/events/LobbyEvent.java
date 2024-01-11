package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.util.GameMap;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.gamemodes.GameMode;
import me.bcawley1.rootwars.vote.Vote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class LobbyEvent implements Listener {
    public void putPlayerInLobby(Player p) {
        p.setMaxHealth(20);
        p.setGameMode(org.bukkit.GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setExp(0);
        p.setHealth(p.getMaxHealth());
        p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
        p.teleport(new Location(RootWars.getWorld(), 562, 1, 9));
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + "Right click to start game.");
        item.setItemMeta(meta);
        p.getInventory().addItem(item);
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @EventHandler
    public void itemInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND)) {
            new Vote(new ArrayList<>(GameMap.getMaps().values()), "Map", s -> {
                RootWars.setCurrentMap(GameMap.getMaps().get(s));
                new Vote(new ArrayList<>(GameMode.getGameModes().values()), "Game Mode", s1 -> RootWars.startGame(GameMode.getGameModes().get(s1))).startVoting();
            }).startVoting();
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        putPlayerInLobby(event.getPlayer());
        RootWars.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}