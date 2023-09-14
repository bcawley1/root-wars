package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameMap {
    private Map<String, Map<String, Map<String, Long>>> bases;
    private Map<String, Map<String, Long>> generators;

    public GameMap(String mapName) {
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
        } catch (Exception e) {
            System.out.println("------------------------");
            e.printStackTrace();
        }
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
        Map<String, Long> locationMap1 = generators.get("diamond1");
        Map<String, Long> locationMap2 = generators.get("diamond2");
        List<Location> generators = new ArrayList<>();
        generators.add(new Location(Bukkit.getWorld("world"),locationMap1.get("x"),locationMap1.get("y"),locationMap1.get("z")));
        generators.add(new Location(Bukkit.getWorld("world"),locationMap2.get("x"),locationMap2.get("y"),locationMap2.get("z")));
        return generators;
    }
    public Location getEmeraldGeneratorLocations(){
        Map<String, Long> locationMap = generators.get("emerald");
        return new Location(Bukkit.getWorld("world"),locationMap.get("x"),locationMap.get("y"),locationMap.get("z"));
    }
}