package me.bcawley1.rootwars.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.events.RootCheckEvent;
import me.bcawley1.rootwars.generator.Generator;
import me.bcawley1.rootwars.generator.GeneratorData;
import me.bcawley1.rootwars.maps.GameMap;
import me.bcawley1.rootwars.maps.TeamData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.*;

public class GameTeam {
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
        RootWars.getPlayer(p).setTeam(null);
    }

    public void addPlayer(UUID id) {
        playersInTeam.add(p);
        RootWars.getPlayer(p).setTeam(this);
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
        return color.toString().toLowerCase();
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

    public enum TeamColor {RED, BLUE, GREEN, YELLOW}
}
