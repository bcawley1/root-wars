package me.bcawley1.rootwars.vote;

import me.bcawley1.rootwars.RootWars;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Vote {
    private VoteBoard voteBoard;
    private String winningItem;
    private int taskID;
    private int secondsLeft;
    private final VoteEvent event = new Vote.VoteEvent();
    private final List<? extends Votable> voteItems;
    private Consumer<String> endVote;
    private String name;

    public Vote(List<? extends Votable> items, String name, Consumer<String> endVote) {
        this.name = name;
        voteItems = items;
        voteBoard = new VoteBoard(items);
        this.endVote = endVote;

    }

    public void startVoting() {
        RootWars.getPlugin().getLogger().log(new LogRecord(Level.INFO, "Initiating %s Voting".formatted(name)));
        Bukkit.getServer().getPluginManager().registerEvents(event, RootWars.getPlugin());
        winningItem = "";
        secondsLeft = 20;

        taskID = Bukkit.getServer().getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            secondsLeft--;
            updateBoard();
            if (secondsLeft <= 5 || secondsLeft == 10 || secondsLeft == 15 || secondsLeft == 19) {
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.MASTER, 1f, 1f));
            }
            if (secondsLeft <= 0) {
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                HandlerList.unregisterAll(event);
                endVote.accept(winningItem);
            }
        }, 0, 20).getTaskId();

        updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            Inventory inv = Bukkit.createInventory(p, 9, (name + " Voting"));

            for (Votable item : voteItems) {
                inv.addItem(item.getItem());
            }
            p.openInventory(inv);
        }
    }

    public void addVote(String mode, UUID playerID) {
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
        Objective objective = scoreboard.registerNewObjective("vote", Criteria.DUMMY, "%s%sVOTING".formatted(ChatColor.YELLOW, ChatColor.BOLD));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for(int i = 0; i < voteBoard.getBoardSize(); i++){
            VoteEntry entry = voteBoard.getBoard().get(i);
            if (!(entry.getVotes() <= 0)) {
                if (i == (voteBoard.getBoardSize() - 1)) {
                    objective.getScore(ChatColor.GREEN + entry.getName() + ": " + entry.getVotes()).setScore(i + 6);
                    winningItem = entry.getName();
                } else {
                    objective.getScore(ChatColor.WHITE + entry.getName() + ": " + entry.getVotes()).setScore(i + 6);
                }

            }
        }
        if (winningItem.isEmpty()) {
            winningItem = voteItems.get(new Random().nextInt(0, voteItems.size())).getName();
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setScoreboard(scoreboard);
            if (voteBoard.getVotedItem(p.getUniqueId()) == null) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Click the %s you want to vote for.".formatted(name.toLowerCase())));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You voted for %s.".formatted(voteBoard.getVotedItem(p.getUniqueId()))));
            }
        });
        objective.getScore(ChatColor.AQUA + "Players Voted: %s/%s".formatted(voteBoard.playersVoted(), Bukkit.getOnlinePlayers().size())).setScore(4);
        objective.getScore(ChatColor.AQUA + "Time Left: %s".formatted(secondsLeft)).setScore(3);
        objective.getScore(ChatColor.WHITE + " ").setScore(2);
        objective.getScore(ChatColor.LIGHT_PURPLE + " ").setScore(5);
        objective.getScore(ChatColor.LIGHT_PURPLE + "Root Wars " + ChatColor.WHITE + "on " + ChatColor.YELLOW + "Lopixel").setScore(1);

        objective.getScore("Current Votes:").setScore(voteBoard.getBoardSize() + 6);
    }

    public static ItemStack getItem(Material material, String name, String description){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("%s%s%s".formatted(ChatColor.RESET, ChatColor.YELLOW, name));
        meta.setLore(List.of((ChatColor.RESET + "" + ChatColor.WHITE + description).split("\n")));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getItem(Material material, String name){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("%s%s%s".formatted(ChatColor.RESET, ChatColor.YELLOW, name));
        item.setItemMeta(meta);
        return item;
    }


    public class VoteEvent implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (event.getCurrentItem() != null) {
                addVote(event.getCurrentItem().getItemMeta().getDisplayName().substring(2), event.getWhoClicked().getUniqueId());
                updateBoard();
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
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
