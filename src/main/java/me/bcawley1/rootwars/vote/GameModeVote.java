package me.bcawley1.rootwars.vote;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.gamemodes.GameMode;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class GameModeVote {
    private static Scoreboard scoreboard;
    private static Objective objective;
    private static VoteBoard voteBoard;
    private static String winningGameMode;
    private static int taskID;
    private static int secondsLeft;
    private static GameModeVoteEvent event = new GameModeVote.GameModeVoteEvent();

    public static void startVoting() {
        voteBoard = new VoteBoard(VoteType.GAMEMODE);
        Bukkit.getServer().getPluginManager().registerEvents(event, RootWars.getPlugin());
        winningGameMode = "";
        secondsLeft = 20;

        taskID = Bukkit.getServer().getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            secondsLeft--;
            GameModeVote.updateBoard();
            if (secondsLeft <= 5 || secondsLeft == 10 || secondsLeft == 15 || secondsLeft == 19) {
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key("minecraft:block.note_block.hat"), Sound.Source.MASTER, 1f, 1f)));
            }
            if (secondsLeft <= 0) {
                //RootWars.startGame(GameMap.getMaps().get(winningMap), RootWars.getPlugin());
                RootWars.startGame(GameMode.getGameModes().get(winningGameMode));
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                HandlerList.unregisterAll(event);
            }
        }, 0, 20).getTaskId();

        updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            Inventory inv = Bukkit.createInventory(p, 9, "Gamemode Voting");
            for (GameMode mode : GameMode.getGameModes().values()) {
                ItemStack item = new ItemStack(mode.getMaterial());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("%s%s%s".formatted(ChatColor.RESET, ChatColor.YELLOW, mode.getGameModeName()));
                meta.setLore(List.of((ChatColor.RESET + "" + ChatColor.WHITE + mode.getDescription()).split("\n")));
                item.setItemMeta(meta);
                inv.setItem(mode.getInvSlot(), item);
            }
            p.openInventory(inv);
        }
    }

    public static void addVote(String mode, UUID playerID) {
        Bukkit.getPlayer(playerID).playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 1f));
        voteBoard.addVote(mode, playerID);
        if (Bukkit.getOnlinePlayers().size() - voteBoard.playersVoted() == 1 && secondsLeft >= 10) {
            secondsLeft = 10;
        } else if (Bukkit.getOnlinePlayers().size() - voteBoard.playersVoted() == 0 && secondsLeft >= 5) {
            secondsLeft = 5;
        }
    }

    public static void updateBoard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("gamemode", Criteria.DUMMY, Component.text("VOTING")
                .decoration(TextDecoration.BOLD, true)
                .color(TextColor.color(255, 255, 85)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (Map.Entry<Integer, VoteEntry> entry : voteBoard.getBoard().entrySet()) {
            if (!(entry.getValue().getVotes() <= 0)) {
                if (entry.getKey() == (voteBoard.getBoardSize() - 1)) {
                    objective.getScore(ChatColor.GREEN + entry.getValue().getName() + ": " + entry.getValue().getVotes()).setScore(entry.getKey() + 6);
                    winningGameMode = entry.getValue().getName();
                } else {
                    objective.getScore(ChatColor.WHITE + entry.getValue().getName() + ": " + entry.getValue().getVotes()).setScore(entry.getKey() + 6);
                }

            }
        }
        if (winningGameMode.equals("")) {
            winningGameMode = (String) GameMode.getGameModes().keySet().toArray()[new Random().nextInt(0, GameMode.getGameModes().size())];
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(scoreboard);
            if (voteBoard.getVotedMap(p.getUniqueId()) == null) {
                p.sendActionBar(ChatColor.YELLOW + "Click the gamemode you want to vote for.");
            } else {
                p.sendActionBar(ChatColor.GREEN + "You voted for %s.".formatted(voteBoard.getVotedMap(p.getUniqueId())));
            }
        }
        objective.getScore(ChatColor.AQUA + "Players Voted: %s/%s".formatted(voteBoard.playersVoted(), Bukkit.getOnlinePlayers().size())).setScore(4);
        objective.getScore(ChatColor.AQUA + "Time Left: %s".formatted(secondsLeft)).setScore(3);
        objective.getScore(ChatColor.WHITE + " ").setScore(2);
        objective.getScore(ChatColor.LIGHT_PURPLE + " ").setScore(5);
        objective.getScore(ChatColor.LIGHT_PURPLE + "Root Wars " + ChatColor.WHITE + "on " + ChatColor.YELLOW + "Lopixel").setScore(1);

        objective.getScore("Current Votes:").setScore(voteBoard.getBoardSize() + 6);
    }


    public static class GameModeVoteEvent implements Listener {
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
        public void playerJoin(PlayerJoinEvent event){
            updateBoard();
            event.getPlayer().getInventory().clear();
            Inventory inv = Bukkit.createInventory(event.getPlayer(), 9, "Gamemode Voting");
            for (GameMode mode : GameMode.getGameModes().values()) {
                ItemStack item = new ItemStack(mode.getMaterial());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("%s%s%s".formatted(ChatColor.RESET, ChatColor.YELLOW, mode.getGameModeName()));
                meta.setLore(List.of((ChatColor.RESET + "" + ChatColor.WHITE + mode.getDescription()).split("\n")));
                item.setItemMeta(meta);
                inv.setItem(mode.getInvSlot(), item);
            }
            event.getPlayer().openInventory(inv);
        }
    }
}
