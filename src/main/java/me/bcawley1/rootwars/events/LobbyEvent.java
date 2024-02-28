package me.bcawley1.rootwars.events;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.gamemodes.GameMode;
import me.bcawley1.rootwars.maps.GameMap;
import me.bcawley1.rootwars.vote.Vote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class LobbyEvent implements Listener {
    private static LobbyEvent currentLobbyEvent;

    public LobbyEvent() {
        currentLobbyEvent = this;
    }

    public void putPlayerInLobby(Player p) {
        //Resets players and places them in the lobby.
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
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
        //If a player interacts with a diamond, the voting process will begin.
        if (event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND)) {
            Vote<GameMode> gameModeVote = new Vote<>(new ArrayList<>(GameMode.getGameModes().values()), "Game Mode", s1 -> RootWars.startGame(GameMode.getGameModes().get(s1)));

            Vote<GameMap> mapVote = new Vote<>(new ArrayList<>(GameMap.getMaps().values()), "Map", s -> {
                RootWars.setCurrentMap(GameMap.getMaps().get(s));
                gameModeVote.startVoting();
            });

            mapVote.startVoting();
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        putPlayerInLobby(event.getPlayer());
        RootWars.defaultJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    public static LobbyEvent getCurrentLobbyEvent() {
        return currentLobbyEvent;
    }
}
