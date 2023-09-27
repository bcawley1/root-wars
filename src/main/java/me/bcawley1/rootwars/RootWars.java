package me.bcawley1.rootwars;

import me.bcawley1.rootwars.commands.GeneratorCommand;
import me.bcawley1.rootwars.commands.LoadCommand;
import me.bcawley1.rootwars.commands.VillagerCommand;
import me.bcawley1.rootwars.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RootWars extends JavaPlugin {
    private static Map<String, GameTeam> teams = new HashMap<>();
    private static JavaPlugin plugin;
    private static GameMap currentMap;
    @Override
    public void onEnable() {
        new GameMap("greenery");
        new GameMap("johnpork");
        /*new GameMap("grimace");
        new GameMap("test1");
        new GameMap("test2");
        new GameMap("test3");
        new GameMap("test4");
        new GameMap("test5");
        new GameMap("test6");*/
        //new GameMap("grimace");
        new Shop();
        // Sets Commands
        getCommand("Generator").setExecutor(new GeneratorCommand(this));
        getCommand("Load").setExecutor(new LoadCommand(this));
        getCommand("Villager").setExecutor(new VillagerCommand());
        // Registers Event Listeners
        getServer().getPluginManager().registerEvents(new EntityInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        getServer().getPluginManager().registerEvents(new PickupEvent(), this);
        getServer().getPluginManager().registerEvents(new DropItemEvent(), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlaceEvent(), this);
        getServer().getPluginManager().registerEvents(new BreakEvent(), this);
        DeathEvent.setPlugin(this);
        plugin = this;

        generateJSON();



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void startGame(GameMap map, JavaPlugin plugin){
        currentMap = map;
        teams.put("red", new GameTeam(map, "red", plugin));
        teams.put("blue", new GameTeam(map, "blue", plugin));
        teams.put("green", new GameTeam(map, "green", plugin));
        teams.put("yellow", new GameTeam(map, "yellow", plugin));
        map.getDiamondGeneratorLocations().forEach(l -> new Generator(plugin, (int) l.getX(), (int) l.getY(), (int) l.getZ(),
                1200, new ArrayList<>(List.of(new GeneratorItem(new ItemStack(Material.DIAMOND), 100)))));
        map.getEmeraldGeneratorLocations().forEach(l -> new Generator(plugin, (int) l.getX(), (int) l.getY(), (int) l.getZ(),
                1200, new ArrayList<>(List.of(new GeneratorItem(new ItemStack(Material.EMERALD), 100)))));

        String[] teamColors = {"red", "blue", "yellow", "green"};
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for(int i = 0; i < players.size(); i++){
            teams.get(teamColors[i%4]).addPlayer(players.get(i));
        }

        map.buildMap();
        Bukkit.getWorld("world").getEntities().forEach(entity -> {
            if(entity.getType().equals(EntityType.VILLAGER)){
                entity.remove();
            } else if(entity.getType().equals(EntityType.DROPPED_ITEM)){
                entity.remove();
            }
        });

        for(GameTeam team : teams.values()){
            team.spawnVillagers();
            for(Player p : team.getPlayersInTeam()){
                team.respawnPlayer(p);
            }
        }
    }

    public static GameMap getCurrentMap() {
        return currentMap;
    }

    public static GameTeam getTeamFromPlayer(Player p){
        for(GameTeam team : teams.values()){
            if(team.containsPlayer(p)){
                return team;
            }
        }
        return null;
    }

    public static Map<String, GameTeam> getTeams() {
        return teams;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void generatorShopJSON(){
//        {
//                "name": "Planks",
//                "buyMaterial": "OAK_PLANKS",
//                "buyAmount": 16,
//                "costMaterial": "GOLD_INGOT",
//                "costAmount": 28
//        },
        JSONArray tab = new JSONArray();
        JSONObject defaultItem = new JSONObject();
        defaultItem.put("name", "");
        defaultItem.put("buyMaterial", "");
        defaultItem.put("buyAmount", 0);
        defaultItem.put("costMaterial", "");
        defaultItem.put("costAmount", 0);
        JSONObject customItem = new JSONObject(defaultItem);
        customItem.put("action", "");
        tab.add(defaultItem);
        tab.add(customItem);
        JSONObject shop = new JSONObject();
        shop.put("Quick Buy", tab);

        try (FileWriter file = new FileWriter(new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/shop.json"))) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(shop.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateJSON() {
        JSONObject generator = new JSONObject();
        generator.put("x", 0);
        generator.put("y", 0);
        generator.put("z", 0);

        JSONObject spawnPoint = new JSONObject();
        spawnPoint.put("x", 0);
        spawnPoint.put("y", 0);
        spawnPoint.put("z", 0);

        JSONObject itemVillager = new JSONObject();
        itemVillager.put("x", 0);
        itemVillager.put("y", 0);
        itemVillager.put("z", 0);

        JSONObject upgradeVillager = new JSONObject();
        upgradeVillager.put("x", 0);
        upgradeVillager.put("y", 0);
        upgradeVillager.put("z", 0);

        JSONObject teamBaseLocs = new JSONObject();
        teamBaseLocs.put("spawnPoint",spawnPoint);
        teamBaseLocs.put("generator",generator);
        teamBaseLocs.put("itemVillager",itemVillager);
        teamBaseLocs.put("upgradeVillager",upgradeVillager);
        teamBaseLocs.put("root",upgradeVillager);

        JSONObject bases = new JSONObject();
        bases.put("red", teamBaseLocs);
        bases.put("blue", teamBaseLocs);
        bases.put("green", teamBaseLocs);
        bases.put("yellow", teamBaseLocs);

        JSONArray generatorList = new JSONArray();
        generatorList.add(generator);
        generatorList.add(generator);

        JSONObject generators = new JSONObject();
        generators.put("diamond", generatorList);
        generators.put("emerald", generatorList);

        JSONObject test = new JSONObject();
        test.put("x", 0);
        JSONObject testY = new JSONObject();
        testY.put("y", 0);
        JSONObject testZ = new JSONObject();
        testZ.put("z", 0);

        JSONObject locations = new JSONObject();
        locations.put("negativeXBorder", test);
        locations.put("positiveXBorder", test);
        locations.put("negativeZBorder", testZ);
        locations.put("positiveZBorder", testZ);
        locations.put("negativeYBorder", testY);
        locations.put("positiveYBorder", testY);
        locations.put("spawnBlock", generator);


        JSONObject mapData = new JSONObject();
        mapData.put("bases",bases);
        mapData.put("generators", generators);
        mapData.put("mapLocations", locations);
        mapData.put("displayName", "test");


        try (FileWriter file = new FileWriter(new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/team.json"))) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(mapData.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }



        /*JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/team.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray readTeams = (JSONArray) obj;
            System.out.println(((JSONObject)((JSONObject)readTeams.get(0)).get("generator")).get("x"));

            //Iterate over employee array

        } catch (Exception ignored) {}*/
    }
}
