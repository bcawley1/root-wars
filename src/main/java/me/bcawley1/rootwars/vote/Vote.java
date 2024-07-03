package me.bcawley1.rootwars.vote;

import me.bcawley1.rootwars.RootWars;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Vote<T extends Votable> {
    private final VoteEvent event = new VoteEvent();
    private final String name;
    private final VoteBoard<T> voteBoard;
    private T winningItem;
    private BukkitTask task;
    private int secondsLeft;
    private final List<T> voteItems;
    private final Consumer<T> endVote;

    public Vote(List<T> items, String name, Consumer<T> endVote) {
        this.name = name;
        voteItems = items;
        voteBoard = new VoteBoard<>(items);
        this.endVote = endVote;

    }

    public void startVoting() {
        RootWars.getPlugin().getLogger().log(new LogRecord(Level.INFO, "Initiating %s Voting".formatted(name)));
        Bukkit.getServer().getPluginManager().registerEvents(event, RootWars.getPlugin());
        secondsLeft = 20;

        task = Bukkit.getServer().getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            secondsLeft--;
            updateBoard();

            switch (secondsLeft) {
                case 19, 15, 10, 5, 4, 3, 2, 1 ->
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.MASTER, 1f, 1f));
                case 0 -> {
                    task.cancel();
                    HandlerList.unregisterAll(event);
                    endVote.accept(winningItem);
                }
            }
        }, 0, 20);

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.getInventory().clear();
            Inventory inv = Bukkit.createInventory(p, 9, (name + " Voting"));
            voteItems.forEach(i -> inv.addItem(i.getItem()));
            p.openInventory(inv);
        });

        updateBoard();
    }

    public void addVote(T mode, UUID playerID) {
        Bukkit.getPlayer(playerID).playSound(Bukkit.getPlayer(playerID), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 1f);
        voteBoard.addVote(mode, playerID);

        if (Bukkit.getOnlinePlayers().size() - voteBoard.playersVoted() == 1 && secondsLeft >= 10) {
            secondsLeft = 10;
        } else if (Bukkit.getOnlinePlayers().size() - voteBoard.playersVoted() == 0 && secondsLeft >= 5) {
            secondsLeft = 5;
        }
    }

    public void updateBoard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("vote", Criteria.DUMMY, "§e§lVOTING");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < voteBoard.getBoardSize(); i++) {
            VoteEntry<T> entry = voteBoard.getBoard().get(i);
            if (entry.votes() > 0) {
                if (i == 0) {
                    objective.getScore("§a%s: %d".formatted(entry.item().getName(), entry.votes())).setScore(i + 6);
                    winningItem = entry.item();
                } else {
                    objective.getScore("§f%s: %d".formatted(entry.item().getName(), entry.votes())).setScore(i + 6);
                }
            }
        }
        if (winningItem == null) {
            winningItem = voteItems.get(new Random().nextInt(0, voteItems.size()));
        }
        objective.getScore("Current Votes:").setScore(voteBoard.getBoardSize() + 6);
        objective.getScore("  ").setScore(5);
        objective.getScore("§bPlayers Voted: %d/%d".formatted(voteBoard.playersVoted(), Bukkit.getOnlinePlayers().size())).setScore(4);
        objective.getScore("§bTime Left: %d".formatted(secondsLeft)).setScore(3);
        objective.getScore(" ").setScore(2);
        objective.getScore(ChatColor.translateAlternateColorCodes('&',RootWars.getPlugin().getConfig().getString("server-watermark"))).setScore(1);

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setScoreboard(scoreboard);
            if (voteBoard.getVotedItem(p.getUniqueId()) == null) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§eClick the %s you want to vote for.".formatted(name.toLowerCase())));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aYou voted for %s.".formatted(voteBoard.getVotedItem(p.getUniqueId()).getName())));
            }
        });
    }

    public static ItemStack getItem(Material material, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e%s".formatted(name));
        meta.setLore(Arrays.stream(description.split("\n"))
                .map(s -> "§r§f" + s)
                .toList());
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e%s".formatted(name));
        item.setItemMeta(meta);
        return item;
    }


    public class VoteEvent implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (event.getCurrentItem() != null) {
                for (T item : voteItems) {
                    if (item.getName().equalsIgnoreCase(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()))) {
                        addVote(item, event.getWhoClicked().getUniqueId());
                        updateBoard();
                        event.setCancelled(true);
                        event.getWhoClicked().closeInventory();
                        break;
                    }
                }
            }
        }

        @EventHandler
        public void playerJoin(PlayerJoinEvent event) {
            updateBoard();
            event.getPlayer().getInventory().clear();
            event.getPlayer().setGameMode(org.bukkit.GameMode.ADVENTURE);
            RootWars.defaultJoin(event.getPlayer());
        }

        @EventHandler
        public void onPlayerDamage(EntityDamageEvent event) {
            event.setCancelled(true);
        }
    }
}