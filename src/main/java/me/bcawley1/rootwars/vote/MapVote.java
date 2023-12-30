package me.bcawley1.rootwars.vote;

import me.bcawley1.rootwars.GameMap;
import me.bcawley1.rootwars.RootWars;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

import java.util.*;

public class MapVote {
    private String winningItem;
    private int taskID;
    private Scoreboard scoreboard;
    private VoteBoard voteBoard;
    private static int secondsLeft = 0;
    private static MapVoteEvent event = new MapVoteEvent();

    public static void startVoting() {
        voteBoard = new VoteBoard(VoteType.MAP);
        Bukkit.getServer().getPluginManager().registerEvents(event, RootWars.getPlugin());
        taskID = 0;
        winningMap = "";
        //creates the time to tick down the seconds every second
        taskID = Bukkit.getServer().getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            secondsLeft--;
            MapVote.updateBoard();
            if(secondsLeft<=5||secondsLeft==10||secondsLeft==15||secondsLeft==19){
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key("minecraft:block.note_block.hat"), Sound.Source.MASTER, 1f, 1f)));
            }
            if(secondsLeft<=0){
                RootWars.setCurrentMap(GameMap.getMaps().get(winningMap));
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                Vote.startVoting();
                HandlerList.unregisterAll(event);
            }
        }, 0, 20).getTaskId();

        secondsLeft = 20;

        updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            for (GameMap map : GameMap.getMaps().values()) {
                p.getInventory().addItem(map.getMap());
            }
        }
    }

    public static void addVote(String map, UUID playerID) {
            Bukkit.getPlayer(playerID).playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f,1f));
            voteBoard.addVote(map, playerID);
            if(Bukkit.getOnlinePlayers().size()-voteBoard.playersVoted()==1&&secondsLeft>=10){
                secondsLeft=10;
            } else if(Bukkit.getOnlinePlayers().size()-voteBoard.playersVoted()==0&&secondsLeft>=5){
                secondsLeft=5;
            }
    }

    public static void updateBoard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("vote", Criteria.DUMMY, Component.text("VOTING")
                .decoration(TextDecoration.BOLD, true)
                .color(TextColor.color(255, 255, 85)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (Map.Entry<Integer, VoteEntry> entry : voteBoard.getBoard().entrySet()){
            if(!(entry.getValue().getVotes()<=0)) {
                if(entry.getKey()==(voteBoard.getBoardSize()-1)){
                    objective.getScore(ChatColor.GREEN + entry.getValue().getName() + ": " + entry.getValue().getVotes()).setScore(entry.getKey()+6);
                    winningMap = entry.getValue().getName();
                } else {
                    objective.getScore(ChatColor.WHITE + entry.getValue().getName() + ": " + entry.getValue().getVotes()).setScore(entry.getKey()+6);
                }

            }
        }
        if(winningMap.equals("")){
            winningMap = (String) GameMap.getMaps().keySet().toArray()[new Random().nextInt(0, GameMap.getMaps().size())];
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            p.setScoreboard(scoreboard);
            if(voteBoard.getVotedMap(p.getUniqueId())==null) {
                p.sendActionBar(ChatColor.YELLOW + "Drop the map you want to vote for.");
            } else{
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

    public static class VoteComparator implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o1.getValue() - o2.getValue();
        }
    }
    public static class VoteEvent implements Listener {
        @EventHandler
        public void onItemDrop(PlayerDropItemEvent event){
            if(event.getItemDrop().getItemStack().getType().equals(Material.FILLED_MAP)){
                event.getItemDrop().remove();
                event.getPlayer().getInventory().clear();
                MapVote.addVote(event.getItemDrop().getItemStack().getItemMeta().getDisplayName(), event.getPlayer().getUniqueId());
                MapVote.updateBoard();
            }
        }
        @EventHandler
        public void playerJoin(PlayerJoinEvent event){
            updateBoard();
            event.getPlayer().getInventory().clear();
            for (GameMap map : GameMap.getMaps().values()) {
                event.getPlayer().getInventory().addItem(map.getMap());
            }
        }
    }
}