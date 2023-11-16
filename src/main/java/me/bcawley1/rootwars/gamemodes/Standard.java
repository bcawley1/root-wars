package me.bcawley1.rootwars.gamemodes;

import me.bcawley1.rootwars.*;
import me.bcawley1.rootwars.events.LobbyEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
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
    private int regenID;
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
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setMaxHealth(20);
            player.setHealth(20);
            player.clearActivePotionEffects();
        }
        Bukkit.getPluginManager().registerEvents(this, RootWars.getPlugin());
        GameMap map = RootWars.getCurrentMap();
        JavaPlugin plugin = RootWars.getPlugin();

        //creates diamond and emerald generators
        map.getDiamondGeneratorLocations().forEach(l -> new Generator(plugin, (int) l.getX(), (int) l.getY(), (int) l.getZ(),
                600, new ArrayList<>(List.of(new GeneratorItem(new ItemStack(Material.DIAMOND), 100)))));
        map.getEmeraldGeneratorLocations().forEach(l -> new Generator(plugin, (int) l.getX(), (int) l.getY(), (int) l.getZ(),
                1200, new ArrayList<>(List.of(new GeneratorItem(new ItemStack(Material.EMERALD), 100)))));

        //assigns players to teams
        String[] teamColors = {"red", "blue", "yellow", "green"};
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for(int i = 0; i < players.size(); i++){
            RootWars.getTeams().get(teamColors[i%4]).addPlayer(players.get(i));
            players.get(i).setGameMode(org.bukkit.GameMode.SURVIVAL);
        }

        RootWars.getCurrentMap().buildMap();

        Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getHealth()<=19.5) {
                    p.setHealth(p.getHealth() + 0.5);
                }
                p.setFoodLevel(20);
            }
        }, 0, 20);

        for(GameTeam team : RootWars.getTeams().values()){
            team.spawnVillagers();
            for(Player p : team.getPlayersInTeam()){
                p.setPlayerListName(ChatColor.valueOf(team.getName().toUpperCase()) + p.getName());
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
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                meta.setUnbreakable(true);
                chestplate.setItemMeta(meta);
                p.getInventory().setChestplate(chestplate);

                updateScoreboard();
            }
        }
    }

    @Override
    public void endGame() {
        Bukkit.getScheduler().cancelTask(regenID);
        LobbyEvent lobbyEvent = new LobbyEvent();
        HandlerList.unregisterAll(this);
        for(Entity entity : Bukkit.getWorld("world").getEntities()){
            if(!entity.getType().equals(EntityType.PLAYER)){
                entity.remove();
            }
        }
        for(GameTeam team : RootWars.getTeams().values()){
            team.removeGenerator();
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            lobbyEvent.putPlayerInLobby(player);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.clearActivePotionEffects();
        }
        Bukkit.getPluginManager().registerEvents(lobbyEvent, RootWars.getPlugin());
    }
}
