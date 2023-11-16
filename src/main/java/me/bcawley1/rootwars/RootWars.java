package me.bcawley1.rootwars;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.bcawley1.rootwars.commands.GeneratorCommand;
import me.bcawley1.rootwars.commands.LoadCommand;
import me.bcawley1.rootwars.commands.VillagerCommand;
import me.bcawley1.rootwars.events.LobbyEvent;
import me.bcawley1.rootwars.gamemodes.GameMode;
import me.bcawley1.rootwars.gamemodes.Rush;
import me.bcawley1.rootwars.gamemodes.Standard;
import me.bcawley1.rootwars.gamemodes.TwoTeams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class RootWars extends JavaPlugin {
    private static final Map<String, GameTeam> teams = new HashMap<>();
    private static JavaPlugin plugin;
    private static GameMap currentMap;
    private static GameMode gameMode;

    public enum GameMaps {
        GREENERY("greenery"),
        JOHNPORK("johnpork"),
        GRIMACE("grimace"),
        SMURFCAT("smurf_cat");

        GameMaps(final String gamemap) {
            this.gamemap = gamemap;
        }

        public String getGamemap() {
            return gamemap;
        }

        private String gamemap;
    }

    @Override
    public void onEnable() {
        Arrays.stream(GameMaps.values()).forEach(x -> new GameMap(x.getGamemap()));
        new Standard();
        new TwoTeams();
        new Rush();
        new Shop();

        // Sets Commands
        getCommand("Generator").setExecutor(new GeneratorCommand(this));
        getCommand("Load").setExecutor(new LoadCommand(this));
        getCommand("Villager").setExecutor(new VillagerCommand());

        Bukkit.getPluginManager().registerEvents(new LobbyEvent(), this);

        plugin = this;
        try {
            pasteSchematic(557, 1, 14, "spawn");
        } catch (IOException e) {

        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void pasteSchematic(int x, int y, int z, String schem) throws IOException {
        File myfile = new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/%s.schem".formatted(schem));
        ClipboardFormat format = ClipboardFormats.findByFile(myfile);
        ClipboardReader reader = format.getReader(new FileInputStream(myfile));
        Clipboard clipboard = reader.read();

        try (
                EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")))) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    // configure here
                    .build();
            Operations.complete(operation);
        } catch (
                WorldEditException ignored) {
        }
    }

    public static void startGame(GameMode mode){
        JavaPlugin plugin = RootWars.getPlugin();
        gameMode = mode;
        teams.clear();
        teams.put("red", new GameTeam(currentMap, "red", plugin));
        teams.put("blue", new GameTeam(currentMap, "blue", plugin));
        teams.put("green", new GameTeam(currentMap, "green", plugin));
        teams.put("yellow", new GameTeam(currentMap, "yellow", plugin));

        gameMode.startGame();
    }

    public static GameMode getCurrentGameMode() {
        return gameMode;
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

    public static void setCurrentMap(GameMap currentMap) {
        RootWars.currentMap = currentMap;
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
