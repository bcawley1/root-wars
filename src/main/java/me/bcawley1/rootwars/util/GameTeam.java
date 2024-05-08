package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.events.RootCheckEvent;
import me.bcawley1.rootwars.generator.Generator;
import me.bcawley1.rootwars.generator.GeneratorData;
import me.bcawley1.rootwars.maps.GameMap;
import me.bcawley1.rootwars.maps.TeamData;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameTeam {
    private final TeamColor color;
    private final TeamData teamData;
    private boolean hasRoot;
    private final Generator generator;
    private RootCheckEvent rootCheckEvent;
    private final List<Player> playersInTeam;
    private Map<String, Integer> upgrades;

    public GameTeam(String name, GeneratorData... generatorData) {
        GameMap map = RootWars.getCurrentMap();
        playersInTeam = new ArrayList<>();
        upgrades = new HashMap<>();
        this.color = TeamColor.valueOf(name.toUpperCase());
        hasRoot = true;
        teamData = map.getTeamData(color);
        generator = new Generator(teamData.getGeneratorLocation().add(0.5, 0.5, 0.5), generatorData);
        rootCheckEvent = new RootCheckEvent(this);
        rootCheckEvent.register();
    }

    public List<Player> getPlayersInTeam() {
        return playersInTeam;
    }

    public int numPlayersInTeam() {
        return playersInTeam.size();
    }

    public void removePlayer(Player p) {
        playersInTeam.remove(p);
        RootWars.getPlayer(p).setTeam(null);
    }

    public void addPlayer(Player p) {
        playersInTeam.add(p);
        RootWars.getPlayer(p).setTeam(this);
    }

    public Generator getGenerator() {
        return generator;
    }

    public void upgrade(String s){
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

    @Override
    public String toString() {
        return playersInTeam.toString();
    }

    public enum TeamColor {RED, BLUE, GREEN, YELLOW}
}
