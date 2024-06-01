package me.bcawley1.rootwars.commands;

import me.bcawley1.rootwars.util.GameTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NameTeam implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("nameteam") && commandSender instanceof Player p && strings.length == 1 && strings[0].length()<40) {
            GameTeam.getTeam(p.getUniqueId()).setName(strings[0]);
            return true;
        } else {
            return false;
        }
    }
}
