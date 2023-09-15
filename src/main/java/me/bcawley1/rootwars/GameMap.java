package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
    private static Map<String, GameMap> maps = new HashMap<>();
    private Map<String, Map<String, Map<String, Long>>> bases;
    private Map<String, List<Map<String, Long>>> generators;
    private Map<String, Map<String, Long>> mapLocations;
    private ItemStack map = new ItemStack(Material.FILLED_MAP);
    private String mapName;

    public GameMap(String mapName) {
        this.mapName = mapName;
        this.renderMap();
        JSONParser jsonParser = new JSONParser();
        JSONObject JSONObj = null;
        try (FileReader reader = new FileReader(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/%s.json".formatted(mapName))) {
            Object obj = jsonParser.parse(reader);
            JSONObj = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try  {

            bases = (JSONObject) JSONObj.get("bases");
            generators = (JSONObject) JSONObj.get("generators");
            mapLocations = (JSONObject) JSONObj.get("mapLocations");
        } catch (Exception e) {
            System.out.println("------------------------");
            e.printStackTrace();
        }
        maps.put(mapName,this);
    }

    private void renderMap(){
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        MapView mapView = Bukkit.createMap(Bukkit.getWorld("world"));

        mapView.getRenderers().clear();
        mapView.addRenderer(new MapImageRenderer(mapName));

        mapMeta.setDisplayName(this.getMapName());
        mapMeta.setMapView(mapView);
        map.setItemMeta(mapMeta);
    }

    public ItemStack getMap() {
        return map;
    }

    public static Map<String, GameMap> getMaps() {
        return maps;
    }

    public String getMapName() {
        return "%s%s".formatted(mapName.substring(0,1).toUpperCase(), mapName.substring(1));
    }

    public Location getSpawnPointLocation(String team){
        Map<String, Long> locationMap = bases.get(team).get("spawnPoint");
        return new Location(Bukkit.getWorld("world"),locationMap.get("x"),locationMap.get("y"),locationMap.get("z"));
    }
    public Location getGeneratorLocation(String team){
        Map<String, Long> locationMap = bases.get(team).get("generator");
        return new Location(Bukkit.getWorld("world"),locationMap.get("x"),locationMap.get("y"),locationMap.get("z"));
    }
    public Location getItemVillagerLocation(String team){
        Map<String, Long> locationMap = bases.get(team).get("itemVillager");
        return new Location(Bukkit.getWorld("world"),locationMap.get("x"),locationMap.get("y"),locationMap.get("z"));
    }
    public Location getUpgradeVillager(String team){
        Map<String, Long> locationMap = bases.get(team).get("upgradeVillager");
        return new Location(Bukkit.getWorld("world"),locationMap.get("x"),locationMap.get("y"),locationMap.get("z"));
    }
    public List<Location> getDiamondGeneratorLocations(){
        List<Map<String, Long>> genList = generators.get("diamond");
        List<Location> genFormatted = new ArrayList<>();

        genList.forEach(map -> {
            genFormatted.add(new Location(Bukkit.getWorld("world"),map.get("x"),map.get("y"),map.get("z")));
        });

        return genFormatted;
    }
    public List<Location> getEmeraldGeneratorLocations(){
        List<Map<String, Long>> genList = generators.get("emerald");
        List<Location> genFormatted = new ArrayList<>();

        genList.forEach(map -> {
            genFormatted.add(new Location(Bukkit.getWorld("world"),map.get("x"),map.get("y"),map.get("z")));
        });

        return genFormatted;
    }
    public Location getMapSpawnPoint(){
        return(new Location(Bukkit.getWorld("world"), mapLocations.get("spawnBlock").get("x"), mapLocations.get("spawnBlock").get("y"), mapLocations.get("spawnBlock").get("z")));
    }
    public Map<String, Integer> getMapBorder(){
        Map<String, Integer> borders = new HashMap<>();
        borders.put("posX", Math.toIntExact(mapLocations.get("positiveXBorder").get("x")));
        borders.put("negX", Math.toIntExact(mapLocations.get("negativeXBorder").get("x")));
        borders.put("posZ", Math.toIntExact(mapLocations.get("positiveYBorder").get("y")));
        borders.put("negZ", Math.toIntExact(mapLocations.get("positiveYBorder").get("y")));
        return borders;
    }
}