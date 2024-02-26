package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GamePlayer {
    Player player;
    GameTeam team;

    public GamePlayer(Player player) {
        this.player = player;
        this.team = null;
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public GameTeam getTeam() {
        return team;
    }

    public void replacePlayer(Player p) {
        player = p;
    }

    public void respawnPlayer() {
        ItemStack[] armor = player.getInventory().getArmorContents();
        player.getInventory().clear();
        player.getInventory().setArmorContents(armor);
        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.getActivePotionEffects().forEach(potionEffect -> {
            if (!RootWars.getCurrentGameMode().isGlobalEffect(potionEffect.getType())) {
                player.removePotionEffect(potionEffect.getType());
            }
        });
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(team.getTeamData().getSpawnPoint());
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "player=" + player +
                ", team=" + team +
                '}';
    }
}
