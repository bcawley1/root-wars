package me.bcawley1.rootwars.gamemodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.bcawley1.rootwars.util.GameTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class FreezeTag extends GameMode {
    @JsonIgnore
    private Map<UUID, Location> frozenPlayers;

    public FreezeTag() {
        super();
        frozenPlayers = new HashMap<>();
    }

    public static void registerGameMode() {
        GameMode.registerGameMode(FreezeTag.class);
    }

    @Override
    public void endGame() {
        super.endGame();
        frozenPlayers.clear();
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player hit) {
            if (GameTeam.getTeam(damager.getUniqueId()).getColor() == GameTeam.TeamColor.BLUE && GameTeam.getTeam(hit.getUniqueId()).getColor() == GameTeam.TeamColor.RED && !frozenPlayers.containsKey(hit.getUniqueId())) {
                frozenPlayers.put(hit.getUniqueId(), hit.getLocation());
                hit.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4You have been frozen. Get a non tagger to unfreeze you!"));
                damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aYou froze %s! ".formatted(hit.getName()) + (frozenPlayers.size() == GameTeam.getTeam(hit.getUniqueId()).numPlayersInTeam() ? "Break the non taggers' root to win." : "You have %d more to go.".formatted(GameTeam.getTeam(hit.getUniqueId()).numPlayersInTeam() - frozenPlayers.size()))));
                hit.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, -1, 255, false, false, false));
                if(frozenPlayers.size() == teams.get(1).numPlayersInTeam() && !teams.get(1).hasRoot()){
                    endGame();
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle("Taggers Win", "", 10, 70, 20));
                }
            } else if (GameTeam.getTeam(damager.getUniqueId()).getColor() == GameTeam.TeamColor.RED && GameTeam.getTeam(hit.getUniqueId()).getColor() == GameTeam.TeamColor.RED && frozenPlayers.containsKey(hit.getUniqueId())) {
                frozenPlayers.remove(hit.getUniqueId());
                hit.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You have been unfrozen. Help your team to break the taggers' root."));
                hit.removePotionEffect(PotionEffectType.GLOWING);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
            Location loc = frozenPlayers.get(event.getPlayer().getUniqueId()).clone();
            loc.setYaw(event.getTo().getYaw());
            loc.setPitch(event.getTo().getPitch());
            event.getPlayer().teleport(loc);
        }
    }

    @Override
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(frozenPlayers.containsKey(event.getEntity().getUniqueId())){
            event.setCancelled(true);
        } if (event.getEntity() instanceof Player p && p.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            p.setGameMode(org.bukkit.GameMode.SPECTATOR);
            p.teleport(GameTeam.getTeam(p.getUniqueId()).getTeamData().getSpawnPoint());
            startRespawnTimer(respawnTime, p.getUniqueId());
        }
    }

    @Override
    public void onRootBreak(GameTeam team) {
        updateScoreboard();
        if (team.getColor() == GameTeam.TeamColor.BLUE){
            endGame();
            Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle("Non Taggers Win", "", 10, 70, 20));
        } else {
            if(frozenPlayers.size() == team.numPlayersInTeam()){
                endGame();
                Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle("Taggers Win", "", 10, 70, 20));
            } else {
                team.getPlayersInTeam().forEach(id -> {
                    Player p = Bukkit.getPlayer(id);
                    p.playSound(p, Sound.ENTITY_WARDEN_ROAR, SoundCategory.MASTER, 1f, 1f);
                    p.sendTitle("Your Root Broke", "", 10, 70, 20);
                });
                teams.get(0).getPlayersInTeam().forEach(id -> Bukkit.getPlayer(id).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Freeze all the non taggers to win!")));
            }
        }
    }

    @Override
    public void assignPlayersToTeams() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);
        for (int i = 0; i < players.size(); i++) {
            teams.get(i % 4 == 0 ? 0 : 1).addPlayer(players.get(i).getUniqueId());
            players.get(i).setGameMode(org.bukkit.GameMode.SURVIVAL);
        }
    }
}
