package me.bcawley1.rootwars.maps;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.util.GameTeam;
import me.bcawley1.rootwars.vote.Votable;
import me.bcawley1.rootwars.vote.Vote;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameMap implements Votable {
    private static final Map<String, GameMap> maps = new HashMap<>();
    private final MapBorder border;
    private final Location mapPlacementLocation;
    private final Map<GameTeam.TeamColor, TeamData> teamData;
    //team -> location -> xyz
    private final List<Location> emeraldGenerators;
    private final List<Location> diamondGenerators;
    //em/dia -> xyz
//    private Map<String, Map<String, Long>> mapLocations;
    //borders and spawnloc
    private final ItemStack item;
    private final String mapName;

    private GameMap(String mapName, int colorNum) {
        this.item = Vote.getItem(Material.valueOf(RootWars.colors[colorNum % RootWars.colors.length] + "_WOOL"), mapName);

        this.mapName = mapName;
        JSONParser jsonParser = new JSONParser();
        JSONObject JSONObj = null;
        try (FileReader reader = new FileReader(RootWars.getPlugin().getDataFolder().getAbsolutePath() + "/Maps/%s/%s.json".formatted(mapName, mapName))) {
            Object obj = jsonParser.parse(reader);
            JSONObj = (JSONObject) obj;
        } catch (Exception ignored) {
        }

        teamData = new HashMap<>();
        for (GameTeam.TeamColor color : GameTeam.TeamColor.values()) {
            teamData.put(color, new TeamData((JSONObject) ((JSONObject) JSONObj.get("bases")).get(color.toString().toLowerCase())));
        }

        Map<String, Long> mapLocation = (Map<String, Long>) ((JSONObject) JSONObj.get("mapLocations")).get("spawnBlock");
        mapPlacementLocation = new Location(RootWars.getWorld(), mapLocation.get("x"), mapLocation.get("y"), mapLocation.get("z"));
        border = new MapBorder((JSONObject) JSONObj.get("mapLocations"));

        diamondGenerators = getListOfLocations((List<Map<String, Long>>) ((JSONObject)JSONObj.get("generators")).get("diamond"));
        emeraldGenerators = getListOfLocations((List<Map<String, Long>>) ((JSONObject)JSONObj.get("generators")).get("emerald"));
    }

    public static void registerMap(String mapName) {
        maps.put(mapName, new GameMap(mapName, maps.size()));
    }

    public void buildMap() {
        RootWars.pasteSchem(mapPlacementLocation, "Maps/%s/%s".formatted(mapName, mapName));
    }


    public static Map<String, GameMap> getMaps() {
        return maps;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public String getName() {
        return mapName;
    }

    private Location getLocationFromMap(Map<String, Long> map) {
        return new Location(RootWars.getWorld(), map.get("x"), map.get("y"), map.get("z"));
    }

    private List<Location> getListOfLocations(List<Map<String, Long>> map){
        List<Location> locations = new ArrayList<>();
        for (Map<String, Long> location : map){
            locations.add(getLocationFromMap(location));
        }
        return locations;
    }

    public List<Location> getEmeraldGenerators() {
        return emeraldGenerators;
    }

    public List<Location> getDiamondGenerators() {
        return diamondGenerators;
    }

    public Location getMapPlacementLocation() {
        return mapPlacementLocation;
    }

    public MapBorder getMapBorder() {
        return border;
    }

    public TeamData getTeamData(GameTeam.TeamColor color) {
        return teamData.get(color);
    }
}