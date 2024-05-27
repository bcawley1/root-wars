package me.bcawley1.rootwars.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.events.RootCheckEvent;
import me.bcawley1.rootwars.generator.Generator;
import me.bcawley1.rootwars.generator.GeneratorData;
import me.bcawley1.rootwars.maps.TeamData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.*;

public class GameTeam {
    @JsonIgnore
    private static final Map<UUID, GameTeam> playerTeams = new HashMap<>();
    @JsonProperty
    private String name;
    @JsonProperty
    private TeamColor color;
    @JsonIgnore
    private TeamData teamData;
    @JsonIgnore
    private boolean hasRoot;
    @JsonIgnore
    private Generator generator;
    @JsonIgnore
    private RootCheckEvent rootCheckEvent;
    @JsonIgnore
    private List<UUID> playersInTeam;
    @JsonIgnore
    private Map<String, Integer> upgrades;

    @JsonCreator
    public GameTeam(@JsonProperty("name") String name, @JsonProperty("color") TeamColor color) {
        this.name = name;
        this.color = color;
    }

    public void resetTeam(GeneratorData... generatorData) {
        if(playersInTeam != null) {
            playersInTeam.forEach(playerTeams::remove);
        }
        teamData = RootWars.getCurrentMap().getTeamData(color);
        hasRoot = true;
        if (generator != null) {
            generator.stopGenerator();
        }
        if (rootCheckEvent != null) {
            rootCheckEvent.cancel();
        }
        generator = new Generator(teamData.getGeneratorLocation().add(0.5, 0, 0.5), generatorData);
        rootCheckEvent = new RootCheckEvent(this);
        rootCheckEvent.register();
        playersInTeam = new ArrayList<>();
        upgrades = new HashMap<>();
    }

    public List<UUID> getPlayersInTeam() {
        return playersInTeam;
    }

    public int numPlayersInTeam() {
        return playersInTeam.size();
    }

    public void removePlayer(UUID id) {
        playersInTeam.remove(id);
        playerTeams.remove(id);
    }

    public void addPlayer(UUID id) {
        playersInTeam.add(id);
        playerTeams.put(id, this);
    }

    public static GameTeam getTeam(UUID id) {
        return playerTeams.get(id);
    }

    public Generator getGenerator() {
        return generator;
    }

    public void upgrade(String s) {
        upgrades.put(s, upgrades.getOrDefault(s, 0) + 1);
    }

    public int getUpgrade(String s) {
        return upgrades.getOrDefault(s, 0);
    }

    public String getName() {
        return name;
    }

    public TeamColor getColor() {
        return color;
    }

    public boolean isItemVillager(Location location) {
        return teamData.getItemVillager().equals(location.add(-0.5, 0, -0.5));
    }

    public boolean isUpgradeVillager(Location location) {
        return teamData.getUpgradeVillager().equals(location.add(-0.5, 0, -0.5));
    }

    public boolean hasRoot() {
        return hasRoot;
    }

    public void breakRoot() {
        hasRoot = false;
        RootWars.getCurrentGameMode().onRootBreak(this);
        rootCheckEvent.cancel();
    }

    public TeamData getTeamData() {
        return teamData;
    }

    public void spawnVillagers() {
        Villager itemVillager = (Villager) RootWars.getWorld().spawnEntity(teamData.getItemVillager().add(0.5, 0, 0.5), EntityType.VILLAGER);
        Villager upgVillager = (Villager) RootWars.getWorld().spawnEntity(teamData.getUpgradeVillager().add(0.5, 0, 0.5), EntityType.VILLAGER);
        setVillagerStuff(itemVillager);
        setVillagerStuff(upgVillager);
    }

    private void setVillagerStuff(Villager villager) {
        villager.setGravity(false);
        villager.setInvulnerable(true);
        villager.setPersistent(true);
        villager.setAI(false);
    }

    public enum TeamColor {
        RED(ChatColor.RED, Color.RED), BLUE(ChatColor.BLUE, Color.BLUE), GREEN(ChatColor.GREEN, Color.GREEN), YELLOW(ChatColor.YELLOW, Color.YELLOW);

        public final ChatColor chatColor;
        public final Color color;

        TeamColor(ChatColor chatColor, Color color) {
            this.chatColor = chatColor;
            this.color = color;
        }
    }
}
