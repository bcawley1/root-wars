package me.bcawley1.rootwars.gamemodes;

import me.bcawley1.rootwars.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.List;

public class Standard extends GameMode {
    private Objective objective;
    public Standard() {
        super("Standard");
        description = """
                The standard Root Wars experience.""";
        invSlot = 0;
        material = Material.BLUE_WOOL;
        gameModes.put("Standard", this);
    }

    @Override
    public void startGame() {
        Bukkit.getPluginManager().registerEvents(this, RootWars.getPlugin());
        GameMap map = RootWars.getCurrentMap();
        JavaPlugin plugin = RootWars.getPlugin();

        //creates diamond and emerald generators
        map.getDiamondGeneratorLocations().forEach(l -> new Generator(plugin, (int) l.getX(), (int) l.getY(), (int) l.getZ(),
                1200, new ArrayList<>(List.of(new GeneratorItem(new ItemStack(Material.DIAMOND), 100)))));
        map.getEmeraldGeneratorLocations().forEach(l -> new Generator(plugin, (int) l.getX(), (int) l.getY(), (int) l.getZ(),
                1200, new ArrayList<>(List.of(new GeneratorItem(new ItemStack(Material.EMERALD), 100)))));

        //assigns players to teams
        String[] teamColors = {"red", "blue", "yellow", "green"};
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for(int i = 0; i < players.size(); i++){
            RootWars.getTeams().get(teamColors[i%4]).addPlayer(players.get(i));
        }

        RootWars.getCurrentMap().buildMap();

        for(GameTeam team : RootWars.getTeams().values()){
            team.spawnVillagers();
            for(Player p : team.getPlayersInTeam()){
                team.respawnPlayer(p);
                ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                ItemMeta meta = chestplate.getItemMeta();
                switch (team.getName()){
                    case "blue":
                        ((LeatherArmorMeta)meta).setColor(Color.BLUE);
                        break;
                    case "red":
                        ((LeatherArmorMeta)meta).setColor(Color.RED);
                        break;
                    case "yellow":
                        ((LeatherArmorMeta)meta).setColor(Color.YELLOW);
                        break;
                    case "green":
                        ((LeatherArmorMeta)meta).setColor(Color.GREEN);
                        break;

                }
                chestplate.setItemMeta(meta);
                p.getInventory().setChestplate(chestplate);

                updateScoreboard();
            }
        }
    }

    @Override
    public void updateScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, Component.text("ROOT WARS")
                .decoration(TextDecoration.BOLD, true)
                .color(TextColor.color(255,255,85)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("Teams:").setScore(7);
        objective.getScore(ChatColor.RED+"Red: %s".formatted(RootWars.getTeams().get("red").isRoot() ? "✔" : RootWars.getTeams().get("red").getPlayersAlive())).setScore(6);
        objective.getScore(ChatColor.BLUE+"Blue: %s".formatted(RootWars.getTeams().get("blue").isRoot() ? "✔" : RootWars.getTeams().get("blue").getPlayersAlive())).setScore(5);
        objective.getScore(ChatColor.GREEN+"Green: %s".formatted(RootWars.getTeams().get("green").isRoot() ? "✔" : RootWars.getTeams().get("green").getPlayersAlive())).setScore(4);
        objective.getScore(ChatColor.YELLOW+"Yellow: %s".formatted(RootWars.getTeams().get("yellow").isRoot() ? "✔" : RootWars.getTeams().get("yellow").getPlayersAlive())).setScore(3);
        objective.getScore(" ").setScore(2);
        objective.getScore(ChatColor.LIGHT_PURPLE + "Root Wars " + ChatColor.WHITE + "on " + ChatColor.YELLOW + "Lopixel").setScore(1);
        //sets new scoreboard to players
        for(Player p : Bukkit.getOnlinePlayers()){
            p.setScoreboard(scoreboard);
        }
    }

    @Override
    public void endGame() {
        for(Entity entity : Bukkit.getWorld("world").getEntities()){
            if(!entity.getType().equals(EntityType.PLAYER)){
                entity.remove();
            }
        }
        for(GameTeam team : RootWars.getTeams().values()){
            team.removeGenerator();
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            player.setGameMode(org.bukkit.GameMode.ADVENTURE);
            player.getInventory().clear();
            player.setExp(0);
            player.setHealth(20);
            player.clearActivePotionEffects();
        }
    }
}
