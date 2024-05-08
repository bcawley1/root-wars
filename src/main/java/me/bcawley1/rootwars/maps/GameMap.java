package me.bcawley1.rootwars.maps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.mixin.LocationMixin;
import me.bcawley1.rootwars.util.DefaultWorldLocation;
import me.bcawley1.rootwars.util.GameTeam;
import me.bcawley1.rootwars.vote.Votable;
import me.bcawley1.rootwars.vote.Vote;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameMap implements Votable {
    @JsonIgnore
    private static final Map<String, GameMap> maps = new HashMap<>();
    @JsonProperty
    private final MapBorder border;
    @JsonProperty
    private final Location mapPlacementLocation;
    @JsonProperty
    private final Map<GameTeam.TeamColor, TeamData> teamData;
    @JsonProperty
    private final List<Location> emeraldGenerators;
    @JsonProperty
    private final List<Location> diamondGenerators;
    @JsonIgnore
    private final ItemStack item;
    @JsonProperty
    private final String mapName;

    @JsonCreator
    private GameMap(@JsonProperty("border") MapBorder border, @JsonProperty("mapPlacementLocation") DefaultWorldLocation mapPlacementLocation,
                    @JsonProperty("teamData") Map<GameTeam.TeamColor, TeamData> teamData, @JsonProperty("emeraldGenerators") List<DefaultWorldLocation> emeraldGenerators,
                    @JsonProperty("diamondGenerators") List<DefaultWorldLocation> diamondGenerators, @JsonProperty("mapName") String mapName) {
        this.border = border;
        this.mapPlacementLocation = mapPlacementLocation;
        this.teamData = teamData;
        this.emeraldGenerators = new ArrayList<>(emeraldGenerators);
        this.diamondGenerators = new ArrayList<>(diamondGenerators);
        this.mapName = mapName;
        this.item = Vote.getItem(Material.valueOf(RootWars.COLORS[maps.size() % RootWars.COLORS.length] + "_WOOL"), mapName);
    }

    public static void registerMap(String mapName) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .build();
        objectMapper.addMixIn(Location.class, LocationMixin.class);
        try {
            GameMap map = objectMapper.readValue(new File(RootWars.getPlugin().getDataFolder() + "/Maps/%s/%s.json".formatted(mapName, mapName)), GameMap.class);
            maps.put(mapName, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildMap() {
        RootWars.pasteSchem(mapPlacementLocation, "Maps/%s/%s".formatted(mapName, mapName));
    }

    @JsonIgnore
    public static Map<String, GameMap> getMaps() {
        return new HashMap<>(maps);
    }

    @JsonIgnore
    @Override
    public ItemStack getItem() {
        return item.clone();
    }

    @JsonIgnore
    @Override
    public String getName() {
        return mapName;
    }

    @JsonIgnore
    public List<Location> getEmeraldGenerators() {
        return new ArrayList<>(emeraldGenerators);
    }

    @JsonIgnore
    public List<Location> getDiamondGenerators() {
        return new ArrayList<>(diamondGenerators);
    }

    @JsonIgnore
    public boolean isInsideBorders(Location loc) {
        return (loc.getX() > getMapBorder().getNegativeX() && loc.getX() < getMapBorder().getPositiveX() &&
                loc.getY() > getMapBorder().getNegativeY() && loc.getY() < getMapBorder().getPositiveY() &&
                loc.getZ() > getMapBorder().getNegativeZ() && loc.getZ() < getMapBorder().getPositiveZ());
    }

    @JsonIgnore
    public MapBorder getMapBorder() {
        return border;
    }

    @JsonIgnore
    public TeamData getTeamData(GameTeam.TeamColor color) {
        return teamData.get(color);
    }
}