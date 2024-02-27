package me.bcawley1.rootwars.maps;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Location;

import java.util.Map;

public class TeamData {
    private final Location spawnPoint;
    private final Location itemVillager;
    private final Location upgradeVillager;
    private final Location generatorLocation;
    private final Location rootLocation;

    TeamData(Map<String, Map<String, Long>> teamData) {
        this.spawnPoint = new Location(RootWars.getWorld(), teamData.get("spawnPoint").get("x"), teamData.get("spawnPoint").get("y"), teamData.get("spawnPoint").get("z"));
        this.itemVillager = new Location(RootWars.getWorld(), teamData.get("itemVillager").get("x"), teamData.get("itemVillager").get("y"), teamData.get("itemVillager").get("z"));
        this.upgradeVillager = new Location(RootWars.getWorld(), teamData.get("upgradeVillager").get("x"), teamData.get("upgradeVillager").get("y"), teamData.get("upgradeVillager").get("z"));
        this.generatorLocation = new Location(RootWars.getWorld(), teamData.get("spawnPoint").get("x"), teamData.get("spawnPoint").get("y"), teamData.get("spawnPoint").get("z"));
        this.rootLocation = new Location(RootWars.getWorld(), teamData.get("root").get("x"), teamData.get("root").get("y"), teamData.get("root").get("z"));
    }

    public Location getSpawnPoint() {
        return spawnPoint.clone();
    }

    public Location getItemVillager() {
        return itemVillager.clone();
    }

    public Location getUpgradeVillager() {
        return upgradeVillager.clone();
    }

    public Location getGeneratorLocation() {
        return generatorLocation.clone();
    }

    public Location getRootLocation() {
        return rootLocation.clone();
    }
}

