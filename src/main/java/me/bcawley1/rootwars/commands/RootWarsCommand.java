package me.bcawley1.rootwars.commands;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.gamemodes.GameMode;
import me.bcawley1.rootwars.maps.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootWarsCommand implements CommandExecutor, TabCompleter {
    private final Map<String, String> mapNames;
    private final Map<String, String> gameModeNames;

    public RootWarsCommand() {
        mapNames = new HashMap<>();
        for (String map : GameMap.getMaps().keySet()) {
            mapNames.put(map.replaceAll(" ", "").toLowerCase(), map);
        }

        gameModeNames = new HashMap<>();
        for (String map : GameMode.getGameModes().keySet()) {
            gameModeNames.put(map.replaceAll(" ", "").toLowerCase(), map);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("rootwars") && commandSender instanceof Player p) {
            if (strings[0].equalsIgnoreCase("stop")) {
                if (RootWars.getCurrentGameMode()!=null && RootWars.getCurrentGameMode().isGameOn()) {
                    RootWars.getCurrentGameMode().endGame();
                } else {
                    p.sendMessage(ChatColor.RED + "There is currently no game in session.");
                }
                return true;
            } else if (strings[0].equalsIgnoreCase("start")) {
                if (RootWars.getCurrentGameMode()!=null && RootWars.getCurrentGameMode().isGameOn()) {
                    p.sendMessage(ChatColor.RED + "There is already a game in session.");
                    return true;
                } else {
                    if (gameModeNames.containsKey(strings[1].toLowerCase()) && mapNames.containsKey(strings[2].toLowerCase())) {
                        RootWars.setCurrentMap(GameMap.getMaps().get(mapNames.get(strings[2])));
                        RootWars.startGame(GameMode.getGameModes().get(gameModeNames.get(strings[1].toLowerCase())));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return switch (strings.length) {
            case 1 -> new ArrayList<>(List.of("stop", "start"));
            case 2 -> new ArrayList<>(gameModeNames.keySet());
            case 3 -> new ArrayList<>(mapNames.keySet());
            default -> null;
        };
    }
}
