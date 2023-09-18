package me.bcawley1.rootwars.commands;

import me.bcawley1.rootwars.Vote;
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
            Player player = (Player) commandSender;
            /*File myfile = new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/greenery.schem");
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
                        .to(BlockVector3.at(0, 64, 0))
                        // configure here
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException ignored) {
            }*/

            /*GameMap testMap = new GameMap("team");
            System.out.println(String.valueOf(testMap.getSpawnPointLocation("red").getX()));
            Bukkit.broadcastMessage(String.valueOf(testMap.getSpawnPointLocation("red").getX()));*/

            /*List<GeneratorItem> items = new ArrayList<>();
            items.add(new GeneratorItem(new ItemStack(Material.IRON_INGOT), 70));
            items.add(new GeneratorItem(new ItemStack(Material.GOLD_INGOT), 20));
            items.add(new GeneratorItem(new ItemStack(Material.DIAMOND), 5));
            items.add(new GeneratorItem(new ItemStack(Material.EMERALD), 5));
            new Generator(plugin, 0, 20, 0, 20, items);*/

            /*for(GameMap map : GameMap.getMaps().values()){
                Bukkit.getPlayer("bcawley1").getInventory().addItem(map.getMap());
            }*/
            Vote.startVoting(plugin);


            return true;
        } else {
            return false;
        }
    }
}
