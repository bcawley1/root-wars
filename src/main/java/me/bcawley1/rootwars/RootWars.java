package me.bcawley1.rootwars;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import me.bcawley1.rootwars.commands.RootWarsCommand;
import me.bcawley1.rootwars.events.LobbyEvent;
import me.bcawley1.rootwars.files.Config;
import me.bcawley1.rootwars.gamemodes.GameMode;
import me.bcawley1.rootwars.gamemodes.*;
import me.bcawley1.rootwars.maps.GameMap;
import me.bcawley1.rootwars.shop.ActionItem;
import me.bcawley1.rootwars.shop.BuyActions;
import me.bcawley1.rootwars.util.GamePlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class RootWars extends JavaPlugin {
    private static Map<Player, GamePlayer> players = new HashMap<>();
    private static JavaPlugin plugin;
    private static GameMap currentMap;
    private static GameMode gameMode;
    private static World world;
    public final static String[] COLORS = {"BLUE", "RED", "YELLOW", "GREEN", "CYAN", "MAGENTA", "ORANGE", "GRAY", "BLACK", "BROWN", "LIME", "PINK", "PURPLE", "WHITE", "LIGHT_BLUE", "LIGHT_GRAY"};

    @Override
    public void onEnable() {
        ActionItem actionItem = new ActionItem("Test", Material.BLUE_WOOL, BuyActions.DEFAULT);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(getDataFolder() + "/test.json"), actionItem);
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin = this;
        saveDefaultConfig();
        try {
            Config.setup();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.getConfig().getString("world") == null) {
            this.getConfig().set("world", Bukkit.getServer().getWorlds().get(0).getName());
            this.saveConfig();
        }
        world = Bukkit.getWorld(this.getConfig().getString("world"));

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, this.getConfig().getBoolean("daylight-cycle"));
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, this.getConfig().getBoolean("weather-cycle"));

        for (File file : new File(this.getDataFolder().getAbsolutePath() + "/Maps").listFiles()) {
            GameMap.registerMap(file.getName());
        }

        new Standard();
        new TwoTeams();
        new Rush();
        new NoBuild();

        // Sets Commands
        getCommand("RootWars").setExecutor(new RootWarsCommand());

        Bukkit.getPluginManager().registerEvents(new LobbyEvent(), this);

        pasteSchem(557, 1, 14, "spawn");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void pasteSchem(Location loc, String schem) {
        pasteSchem((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), schem);
    }

    public static void pasteSchem(int x, int y, int z, String schem) {
        File myfile = new File(plugin.getDataFolder().getAbsolutePath() + "/%s.schem".formatted(schem));
        ClipboardFormat format = ClipboardFormats.findByFile(myfile);
        ClipboardReader reader = null;
        try {
            reader = format.getReader(new FileInputStream(myfile));
        } catch (IOException ignored) {
        }

        Clipboard clipboard = null;
        try {
            clipboard = reader.read();
        } catch (IOException ignored) {
        }

        try (
                EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(RootWars.getWorld()))) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException ignored) {
        }
    }

    public static void startGame(GameMode mode) {
        gameMode = mode;
        getPlugin().getLogger().log(new LogRecord(Level.INFO, "Starting game on map: %s, with game mode: %s".formatted(currentMap.getName(), gameMode.getName())));
        HandlerList.unregisterAll(LobbyEvent.getCurrentLobbyEvent());
        gameMode.startGame();
    }

    public static GamePlayer getPlayer(Player p) {
        return players.get(p);
    }

    public static GameMode getCurrentGameMode() {
        return gameMode;
    }

    public static GameMap getCurrentMap() {
        return currentMap;
    }

    public static World getWorld() {
        return world;
    }

    public static void setCurrentMap(GameMap currentMap) {
        RootWars.currentMap = currentMap;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void addPlayer(Player p) {
        if (players.containsKey(p)) {
            players.get(p).replacePlayer(p);
        } else {
            players.put(p, new GamePlayer(p));
        }
    }

    public static void defaultJoin(Player p) {
        RootWars.addPlayer(p);
        String message = RootWars.getPlugin().getConfig().getString("join-message").replace("{player}", p.getName());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

//    public static void generatorShopJSON(){
////        {
////                "name": "Planks",
////                "buyMaterial": "OAK_PLANKS",
////                "buyAmount": 16,
////                "costMaterial": "GOLD_INGOT",
////                "costAmount": 28
////        },
//        JSONArray tab = new JSONArray();
//        JSONObject defaultItem = new JSONObject();
//        defaultItem.put("name", "");
//        defaultItem.put("buyMaterial", "");
//        defaultItem.put("buyAmount", 0);
//        defaultItem.put("costMaterial", "");
//        defaultItem.put("costAmount", 0);
//        JSONObject customItem = new JSONObject(defaultItem);
//        customItem.put("action", "");
//        tab.add(defaultItem);
//        tab.add(customItem);
//        JSONObject shop = new JSONObject();
//        shop.put("Quick Buy", tab);
//
//        try (FileWriter file = new FileWriter(new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/me.bcawley1.rootwars.shop.json"))) {
//            //We can write any JSONArray or JSONObject instance to the file
//            file.write(shop.toJSONString());
//            file.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void generateJSON() {
//        JSONObject generator = new JSONObject();
//        generator.put("x", 0);
//        generator.put("y", 0);
//        generator.put("z", 0);
//
//        JSONObject spawnPoint = new JSONObject();
//        spawnPoint.put("x", 0);
//        spawnPoint.put("y", 0);
//        spawnPoint.put("z", 0);
//
//        JSONObject itemVillager = new JSONObject();
//        itemVillager.put("x", 0);
//        itemVillager.put("y", 0);
//        itemVillager.put("z", 0);
//
//        JSONObject upgradeVillager = new JSONObject();
//        upgradeVillager.put("x", 0);
//        upgradeVillager.put("y", 0);
//        upgradeVillager.put("z", 0);
//
//        JSONObject teamBaseLocs = new JSONObject();
//        teamBaseLocs.put("spawnPoint",spawnPoint);
//        teamBaseLocs.put("generator",generator);
//        teamBaseLocs.put("itemVillager",itemVillager);
//        teamBaseLocs.put("upgradeVillager",upgradeVillager);
//        teamBaseLocs.put("root",upgradeVillager);
//
//        JSONObject bases = new JSONObject();
//        bases.put("red", teamBaseLocs);
//        bases.put("blue", teamBaseLocs);
//        bases.put("green", teamBaseLocs);
//        bases.put("yellow", teamBaseLocs);
//
//        JSONArray generatorList = new JSONArray();
//        generatorList.add(generator);
//        generatorList.add(generator);
//
//        JSONObject generators = new JSONObject();
//        generators.put("diamond", generatorList);
//        generators.put("emerald", generatorList);
//
//        JSONObject test = new JSONObject();
//        test.put("x", 0);
//        JSONObject testY = new JSONObject();
//        testY.put("y", 0);
//        JSONObject testZ = new JSONObject();
//        testZ.put("z", 0);
//
//        JSONObject locations = new JSONObject();
//        locations.put("negativeXBorder", test);
//        locations.put("positiveXBorder", test);
//        locations.put("negativeZBorder", testZ);
//        locations.put("positiveZBorder", testZ);
//        locations.put("negativeYBorder", testY);
//        locations.put("positiveYBorder", testY);
//        locations.put("spawnBlock", generator);
//
//
//        JSONObject mapData = new JSONObject();
//        mapData.put("bases",bases);
//        mapData.put("generators", generators);
//        mapData.put("mapLocations", locations);
//        mapData.put("displayName", "test");
//
//
//        try (FileWriter file = new FileWriter(new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/team.json"))) {
//            //We can write any JSONArray or JSONObject instance to the file
//            file.write(mapData.toJSONString());
//            file.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        /*JSONParser jsonParser = new JSONParser();
//        try (FileReader reader = new FileReader(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/team.json"))
//        {
//            //Read JSON file
//            Object obj = jsonParser.parse(reader);
//
//            JSONArray readTeams = (JSONArray) obj;
//            System.out.println(((JSONObject)((JSONObject)readTeams.get(0)).get("generator")).get("x"));
//
//            //Iterate over employee array
//
//        } catch (Exception ignored) {}*/
//    }
}
