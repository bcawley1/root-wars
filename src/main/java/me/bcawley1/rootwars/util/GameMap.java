package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
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
    private static Map<String, GameMap> maps = new HashMap<>();

    private Map<String, Location> teamGen;
    private Map<String, Location> root;
    private Map<String, Map<String, Map<String, Long>>> bases;
    //team -> location -> xyz
    private Map<String, List<Map<String, Long>>> generators;
    //em/dia -> xyz
    private Map<String, Map<String, Long>> mapLocations;
    //borders and spawnloc
    private final ItemStack item;
    private String mapName;

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

        try {
            bases = (JSONObject) JSONObj.get("bases");
            generators = (JSONObject) JSONObj.get("generators");
            mapLocations = (JSONObject) JSONObj.get("mapLocations");
        } catch (Exception ignored) {
        }
    }

    public static void registerMap(String mapName, int colorNum) {
        maps.put(mapName, new GameMap(mapName, colorNum));
    }

    public void buildMap() {
        RootWars.pasteSchem((int) this.getMapSpawnPoint().getX(), (int) this.getMapSpawnPoint().getY(), (int) this.getMapSpawnPoint().getZ(), "Maps/%s/%s".formatted(mapName, mapName));
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    public static Map<String, GameMap> getMaps() {
        return maps;
    }


    @Override
    public String getName() {
        return mapName;
    }

    public Location getRootLocation(String team) {
        Map<String, Long> locationMap = bases.get(team).get("root");
        return new Location(RootWars.getWorld(), locationMap.get("x"), locationMap.get("y"), locationMap.get("z"));
    }

    public Location getSpawnPointLocation(String team) {
        Map<String, Long> locationMap = bases.get(team).get("spawnPoint");
        return new Location(RootWars.getWorld(), locationMap.get("x"), locationMap.get("y"), locationMap.get("z"));
    }

    public Location getGeneratorLocation(String team) {
        Map<String, Long> locationMap = bases.get(team).get("generator");
        return new Location(RootWars.getWorld(), locationMap.get("x"), locationMap.get("y"), locationMap.get("z"));
    }

    public Location getItemVillagerLocation(String team) {
        Map<String, Long> locationMap = bases.get(team).get("itemVillager");
        return new Location(RootWars.getWorld(), locationMap.get("x"), locationMap.get("y"), locationMap.get("z"));
    }

    public Location getUpgradeVillager(String team) {
        Map<String, Long> locationMap = bases.get(team).get("upgradeVillager");
        return new Location(RootWars.getWorld(), locationMap.get("x"), locationMap.get("y"), locationMap.get("z"));
    }

    public List<Location> getDiamondGeneratorLocations() {
        List<Map<String, Long>> genList = generators.get("diamond");
        List<Location> genFormatted = new ArrayList<>();

        genList.forEach(map -> {
            genFormatted.add(new Location(RootWars.getWorld(), map.get("x"), map.get("y"), map.get("z")));
        });

        return genFormatted;
    }

    public List<Location> getEmeraldGeneratorLocations() {
        List<Map<String, Long>> genList = generators.get("emerald");
        List<Location> genFormatted = new ArrayList<>();

        genList.forEach(map -> {
            genFormatted.add(new Location(RootWars.getWorld(), map.get("x"), map.get("y"), map.get("z")));
        });

        return genFormatted;
    }

    public Location getMapSpawnPoint() {
        return (new Location(RootWars.getWorld(), mapLocations.get("spawnBlock").get("x"), mapLocations.get("spawnBlock").get("y"), mapLocations.get("spawnBlock").get("z")));
    }

    public Map<String, Integer> getMapBorder() {
        Map<String, Integer> borders = new HashMap<>();
        borders.put("posX", Math.toIntExact(mapLocations.get("positiveXBorder").get("x")));
        borders.put("negX", Math.toIntExact(mapLocations.get("negativeXBorder").get("x")));
        borders.put("posZ", Math.toIntExact(mapLocations.get("positiveZBorder").get("z")));
        borders.put("negZ", Math.toIntExact(mapLocations.get("negativeZBorder").get("z")));
        borders.put("posY", Math.toIntExact(mapLocations.get("positiveYBorder").get("y")));
        borders.put("negY", Math.toIntExact(mapLocations.get("negativeYBorder").get("y")));
        return borders;
    }

    public class MapBorder {
        private final int positiveX;
        private final int positiveY;
        private final int positiveZ;
        private final int negativeX;
        private final int negativeY;
        private final int negativeZ;

        private MapBorder() {
            positiveX = Math.toIntExact(mapLocations.get("positiveXBorder").get("x"));
            positiveY = Math.toIntExact(mapLocations.get("negativeXBorder").get("x"));
            positiveZ = Math.toIntExact(mapLocations.get("positiveZBorder").get("z"));
            negativeX = Math.toIntExact(mapLocations.get("negativeZBorder").get("z"));
            negativeY = Math.toIntExact(mapLocations.get("positiveYBorder").get("y"));
            negativeZ = Math.toIntExact(mapLocations.get("negativeYBorder").get("y"));
        }

        public int getPositiveX() {
            return positiveX;
        }

        public int getPositiveY() {
            return positiveY;
        }

        public int getPositiveZ() {
            return positiveZ;
        }

        public int getNegativeX() {
            return negativeX;
        }

        public int getNegativeY() {
            return negativeY;
        }

        public int getNegativeZ() {
            return negativeZ;
        }
    }
}