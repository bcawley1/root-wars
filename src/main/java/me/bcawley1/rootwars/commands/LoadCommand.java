package me.bcawley1.rootwars.commands;

import me.bcawley1.rootwars.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LoadCommand implements CommandExecutor {
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
            }*/

            GameMap testMap = new GameMap("team");
            System.out.println(String.valueOf(testMap.getSpawnPointLocation("red").getX()));
            Bukkit.broadcastMessage(String.valueOf(testMap.getSpawnPointLocation("red").getX()));
            return true;
        } else {
            return false;
        }
    }
}
