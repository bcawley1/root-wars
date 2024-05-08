package me.bcawley1.rootwars.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.lang.ref.Reference;

public abstract class LocationMixin {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonIgnore
    private Reference<World> world;
    @JsonProperty
    private double x;
    @JsonProperty
    private double y;
    @JsonProperty
    private double z;
    @JsonIgnore
    private float pitch;
    @JsonIgnore
    private float yaw;

    public LocationMixin(@JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("z") double z) {}

    @JsonIgnore
    public abstract boolean isWorldLoaded();

    @JsonIgnore
    public abstract World getWorld();

    @JsonIgnore
    public abstract Chunk getChunk();

    @JsonIgnore
    public abstract Block getBlock();

    @JsonIgnore
    public abstract int getBlockX();

    @JsonIgnore
    public abstract int getBlockY();

    @JsonIgnore
    public abstract int getBlockZ();

    @JsonIgnore
    public abstract float getYaw();

    @JsonIgnore
    public abstract float getPitch();

    @JsonIgnore
    public abstract Vector getDirection();
}
