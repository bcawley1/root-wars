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
import me.bcawley1.rootwars.commands.RootWarsCommand;
import me.bcawley1.rootwars.events.LobbyEvent;
import me.bcawley1.rootwars.files.Config;
import me.bcawley1.rootwars.gamemodes.GameMode;
import me.bcawley1.rootwars.gamemodes.*;
import me.bcawley1.rootwars.maps.GameMap;
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
//        PotionEffect potionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, -1 , 255, false, false, false);

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
        Standard.registerGameMode();
        Rush.registerGameMode();
        NoBuild.registerGameMode();
        TwoTeams.registerGameMode();

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
}
