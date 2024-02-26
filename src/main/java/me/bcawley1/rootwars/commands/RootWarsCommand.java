package me.bcawley1.rootwars.commands;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.gamemodes.GameMode;
import me.bcawley1.rootwars.maps.GameMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RootWarsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equals("rootwars") && commandSender instanceof Player p){
            if(strings[0].equals("start")){
                //Checks if game mode name entered is a valid game mode.
                boolean isGameMode = false;
                for (String name : GameMode.getGameModes().keySet()) {
                    if(name.equalsIgnoreCase(strings[1])){
                        isGameMode = true;
                    }
                }

                boolean isMap = false;
                for(String name : GameMap.getMaps().keySet()){

                }
                return true;
            } else if (strings[0].equals("stop")) {
                RootWars.getCurrentGameMode().endGame();
                return true;
            } else{
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
