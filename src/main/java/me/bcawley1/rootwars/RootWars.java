package me.bcawley1.rootwars;

import me.bcawley1.rootwars.commands.GeneratorCommand;
import me.bcawley1.rootwars.commands.LoadCommand;
import me.bcawley1.rootwars.commands.VillagerCommand;
import me.bcawley1.rootwars.events.ClickEvent;
import me.bcawley1.rootwars.events.EntityInteractEvent;
import me.bcawley1.rootwars.events.PickupEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class RootWars extends JavaPlugin {

    @Override
    public void onEnable() {
        new GameMap("greenery");
        new GameMap("johnpork");
        new GameMap("grimace");
        // Sets Commands
        getCommand("Generator").setExecutor(new GeneratorCommand(this));
        getCommand("Load").setExecutor(new LoadCommand(this));
        getCommand("Villager").setExecutor(new VillagerCommand());
        // Registers Event Listeners
        getServer().getPluginManager().registerEvents(new EntityInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        getServer().getPluginManager().registerEvents(new PickupEvent(), this);

        generateJSON();
        System.out.println(GameMap.getMaps().get("greenery").getDiamondGeneratorLocations().get(0).getX());






    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void generateJSON() {
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
        JSONObject testZ = new JSONObject();
        testZ.put("z", 0);

        JSONObject locations = new JSONObject();
        locations.put("negativeXBorder", test);
        locations.put("positiveXBorder", test);
        locations.put("negativeYBorder", testZ);
        locations.put("positiveYBorder", testZ);
        locations.put("spawnBlock", generator);


        JSONObject mapData = new JSONObject();
        mapData.put("bases",bases);
        mapData.put("generators", generators);
        mapData.put("mapLocations", locations);


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
