package me.bcawley1.rootwars.commands;

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
import me.bcawley1.rootwars.GameMap;
import me.bcawley1.rootwars.Generator;
import me.bcawley1.rootwars.GeneratorItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadCommand implements CommandExecutor {
    JavaPlugin plugin;

    public LoadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("load")){
            /*File myfile = new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/test.schem");
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

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(0, 0, 0))
                        // configure here
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException ignored) {
            }

            GameMap testMap = new GameMap("team");
            System.out.println(String.valueOf(testMap.getSpawnPointLocation("red").getX()));
            Bukkit.broadcastMessage(String.valueOf(testMap.getSpawnPointLocation("red").getX()));*/

            List<GeneratorItem> items = new ArrayList<>();
            items.add(new GeneratorItem(new ItemStack(Material.IRON_INGOT), 50));
            items.add(new GeneratorItem(new ItemStack(Material.GOLD_INGOT), 50));
            new Generator(plugin, 0, 20, 0, 20, items);

            return true;
        } else {
            return false;
        }
    }
}
