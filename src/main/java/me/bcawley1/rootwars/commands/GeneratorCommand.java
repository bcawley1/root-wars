package me.bcawley1.rootwars.commands;

import me.bcawley1.rootwars.Generator;
import me.bcawley1.rootwars.GeneratorItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCommand implements CommandExecutor, TabExecutor {
    private JavaPlugin plugin;
    private static int taskID = 0;

    public GeneratorCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            int x, y, z;
            boolean on = false;
            if (command.getName().equalsIgnoreCase("generator")) {
                try {
                    x = Integer.parseInt(strings[0]);
                    y = Integer.parseInt(strings[1]);
                    z = Integer.parseInt(strings[2]);
                    on = Boolean.parseBoolean(strings[3]);
                } catch (Exception e) {
                    return false;
                }
                Location location = new Location(Bukkit.getWorld("world"), x, y, z);
                if (on && !Generator.containsGenerator(location)) {
                    new Generator(plugin, x, y, z, 20, new ArrayList<GeneratorItem>(List.of(new GeneratorItem(new ItemStack(Material.IRON_INGOT),100))));
                } else if (!on && Generator.containsGenerator(location)) {
                    Generator.removeGenerator(location);
                } else if (on && Generator.containsGenerator(location)) {
                    player.sendMessage("There is already a generator at that location.");
                } else if (!on && !Generator.containsGenerator(location)) {
                    player.sendMessage("There is no generator at that location.");
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            try {
                if (!player.getTargetBlock(null, 5).getType().isAir()) {
                    if (strings[0].equals("")) {
                        return new ArrayList<>(List.of(String.valueOf((int) (player.getTargetBlock(null, 5).getLocation().x()))));
                    } else if (strings[1].equals("")) {
                        return new ArrayList<>(List.of(String.valueOf((int) (player.getTargetBlock(null, 5).getLocation().y()))));
                    } else if (strings[2].equals("")) {
                        return new ArrayList<>(List.of(String.valueOf((int) (player.getTargetBlock(null, 5).getLocation().z()))));
                    }
                }
                if (!strings[0].equals("") && !strings[1].equals("") && !strings[2].equals("")) {
                    if (strings[3].equals("")) {
                        return new ArrayList<>(List.of("true", "false"));
                    } else if ("true".contains(strings[3].toLowerCase())) {
                        return new ArrayList<>(List.of("true"));
                    } else if ("false".contains(strings[3].toLowerCase())) {
                        return new ArrayList<>(List.of("false"));
                    } else {
                        return new ArrayList<>();
                    }
                }
            } catch (Exception e){

            }
        }
        return new ArrayList<>();
    }
}
