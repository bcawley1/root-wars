package me.bcawley1.rootwars.commands;

import me.bcawley1.rootwars.util.GameMap;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.gamemodes.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class LoadCommand implements CommandExecutor {
    JavaPlugin plugin;

    public LoadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("load")) {
            Player p = (Player) commandSender;
            RootWars.setCurrentMap(GameMap.getMaps().get("Greenery"));
            RootWars.startGame(GameMode.getGameModes().get("Standard"));
            return true;
        } else {
            return false;
        }
    }
}
