package me.bcawley1.rootwars.maps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.util.DefaultWorldLocation;
import org.bukkit.Location;

public class TeamData {
    @JsonProperty
    private final Location spawnPoint;
    @JsonProperty
    private final Location itemVillager;
    @JsonProperty
    private final Location upgradeVillager;
    @JsonProperty
    private final Location generatorLocation;
    @JsonProperty
    private final Location rootLocation;

    @JsonCreator
    private TeamData(@JsonProperty("spawnPoint") DefaultWorldLocation spawnPoint, @JsonProperty("itemVillager") DefaultWorldLocation itemVillager,
                     @JsonProperty("upgradeVillager") DefaultWorldLocation upgradeVillager, @JsonProperty("generatorLocation") DefaultWorldLocation generatorLocation, @JsonProperty("rootLocation") DefaultWorldLocation rootLocation) {
        this.spawnPoint = spawnPoint;
        this.itemVillager = itemVillager;
        this.upgradeVillager = upgradeVillager;
        this.generatorLocation = generatorLocation;
        this.rootLocation = rootLocation;
    }

    public Location getSpawnPoint() {
        return new Location(spawnPoint.getWorld(), spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), spawnPoint.getYaw(), spawnPoint.getPitch());
    }

    public Location getItemVillager() {
        return new Location(itemVillager.getWorld(), itemVillager.getX(), itemVillager.getY(), itemVillager.getZ(), itemVillager.getYaw(), itemVillager.getPitch());
    }

    public Location getUpgradeVillager() {
        return new Location(upgradeVillager.getWorld(), upgradeVillager.getX(), upgradeVillager.getY(), upgradeVillager.getZ(), upgradeVillager.getYaw(), upgradeVillager.getPitch());
    }

    public Location getGeneratorLocation() {
        return new Location(generatorLocation.getWorld(), generatorLocation.getX(), generatorLocation.getY(), generatorLocation.getZ(), generatorLocation.getYaw(), generatorLocation.getPitch());
    }

    public Location getRootLocation() {
        return new Location(rootLocation.getWorld(), rootLocation.getX(), rootLocation.getY(), rootLocation.getZ(), rootLocation.getYaw(), rootLocation.getPitch());
    }
}

