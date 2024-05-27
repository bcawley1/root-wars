package me.bcawley1.rootwars.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.Location;

public class DefaultWorldLocation extends Location {
    @JsonCreator
    public DefaultWorldLocation(@JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("z") double z) {
        super(RootWars.getWorld(), x, y, z);
    }

    private Location toLocation(){
        return new Location(RootWars.getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
    }
}
