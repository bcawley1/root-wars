package me.bcawley1.rootwars;

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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Vote {
    private static String winningMap;
    private static int taskID;
    private static Scoreboard scoreboard;
    private static VoteBoard voteBoard;
    private static Objective objective;
    private static int secondsLeft = 0;
    private static VoteDropItemEvent event = new VoteDropItemEvent();

    public static void startVoting() {
        Bukkit.getServer().getPluginManager().registerEvents(event, RootWars.getPlugin());
        taskID = 0;
        winningMap = "";
        //creates the time to tick down the seconds every second
        taskID = Bukkit.getServer().getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            secondsLeft--;
            Vote.updateBoard();
            if(secondsLeft<=5||secondsLeft==10||secondsLeft==15||secondsLeft==19){
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key("minecraft:block.note_block.hat"), Sound.Source.MASTER, 1f, 1f)));
            }
            if(secondsLeft<=0){
                RootWars.startGame(GameMap.getMaps().get(winningMap), RootWars.getPlugin());
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                HandlerList.unregisterAll(event);
            }
        }, 0, 20).getTaskId();
        //creates a new board and puts values in it or somthign idk
        voteBoard = new VoteBoard();
        secondsLeft = 20;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("vote", "dummy", Component.text("VOTING")
                .decoration(TextDecoration.BOLD, true)
                .color(TextColor.color(255, 255, 85)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore("Current Votes:").setScore(voteBoard.getBoardSize() + 4);
        objective.getScore(ChatColor.WHITE + " ").setScore(2);
        objective.getScore(ChatColor.LIGHT_PURPLE + " ").setScore(5);
        objective.getScore(ChatColor.LIGHT_PURPLE + "Root Wars " + ChatColor.WHITE + "on " + ChatColor.YELLOW + "Lopixel").setScore(1);

        Team playersVoted = scoreboard.registerNewTeam("playersVoted");
        playersVoted.addEntry("Players Voted: ");
        playersVoted.setSuffix("0/" + Bukkit.getOnlinePlayers());
        playersVoted.setColor(ChatColor.AQUA);
        objective.getScore("Players Voted: ").setScore(4);
        Team timeLeft = scoreboard.registerNewTeam("timeLeft");
        timeLeft.addEntry("Time Left: ");
        timeLeft.setSuffix(String.valueOf(secondsLeft));
        timeLeft.setColor(ChatColor.AQUA);
        objective.getScore("Time Left: ").setScore(3);

        Map<VoteEntry, Team> teams = new HashMap<>();
        for (Map.Entry<Integer, VoteEntry> entry : voteBoard.getBoard().entrySet()) {
            Team team = scoreboard.registerNewTeam(GameMap.DisplaytoMapName(entry.getValue().getName()));
            team.addEntry(entry.getValue().getName() + ": ");
            team.setSuffix(String.valueOf(entry.getValue().getVotes()));
            objective.getScore(entry.getValue().getName() + ": ").resetScore();
        }

        updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendActionBar(ChatColor.YELLOW+"Drop the map you want to vote for.");
            p.getInventory().clear();
            p.setScoreboard(scoreboard);
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
        for (Map.Entry<Integer, VoteEntry> entry : voteBoard.getBoard().entrySet()){
            Team team = scoreboard.getTeam(GameMap.DisplaytoMapName(entry.getValue().getName()));
            team.setSuffix(String.valueOf(entry.getValue().getVotes()));
            if(entry.getValue().getVotes()<=0) {
                objective.getScore(entry.getValue().getName() + " :").resetScore();
            } else {
                objective.getScore(entry.getValue().getName() + " :").setScore(entry.getKey());
            }
            if(entry.getKey()==voteBoard.getBoardSize()){
                team.setColor(ChatColor.GREEN);
                winningMap = GameMap.DisplaytoMapName(entry.getValue().getName());
            } else {
                team.setColor(ChatColor.WHITE);
            }
        }
        if(winningMap.equals("")){
            winningMap = (String) GameMap.getMaps().keySet().toArray()[new Random().nextInt(0, GameMap.getMaps().size())];
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            if(voteBoard.getVotedMap(p.getUniqueId())==null) {
                p.sendActionBar(ChatColor.YELLOW + "Drop the map you want to vote for.");
            } else{
                p.sendActionBar(ChatColor.GREEN + "You voted for %s.".formatted(voteBoard.getVotedMap(p.getUniqueId())));
            }
        }

        scoreboard.getTeam("playersVoted").setSuffix(voteBoard.playersVoted() + "/" + Bukkit.getOnlinePlayers().size());
        scoreboard.getTeam("timeLeft").setSuffix(String.valueOf(secondsLeft));

        objective.getScore("Current Votes:").setScore(voteBoard.getBoardSize() + 6);
    }

    public static class VoteComparator implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o1.getValue() - o2.getValue();
        }
    }
    public static class VoteDropItemEvent implements Listener {
        @EventHandler
        public void onItemDrop(PlayerDropItemEvent event){
            if(event.getItemDrop().getItemStack().getType().equals(Material.FILLED_MAP)){
                event.getItemDrop().remove();
                event.getPlayer().getInventory().clear();
                Vote.addVote(event.getItemDrop().getItemStack().getItemMeta().getDisplayName(), event.getPlayer().getUniqueId());
                Vote.updateBoard();
            }
        }
    }
}

//    private static String winningMap;
//    private static int taskID;
//    private static Scoreboard voteBoard;
//    private static Map<UUID, Scoreboard> playerBoards = new HashMap<>();
//    private static Objective objective;
//    private static int playersVoted = 0;
//    private static int secondsLeft = 0;
//    private static boolean voting = false;
//    private static Map<String, Integer> votes = new TreeMap<>();
//    private static Map<UUID, String> playerVotes = new HashMap<>();
//
//    public static void startVoting(JavaPlugin plugin) {
//        taskID = 0;
//        winningMap = "";
//        taskID = Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
//            secondsLeft--;
//            Vote.updateBoard();
//            if(secondsLeft<=5||secondsLeft==10||secondsLeft==15||secondsLeft==19){
//                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key("minecraft:block.note_block.hat"), Sound.Source.MASTER, 1f, 1f)));
//            }
//            if(secondsLeft<=0){
//                RootWars.startGame(GameMap.getMaps().get(winningMap), plugin);
//                Bukkit.getServer().getScheduler().cancelTask(taskID);
//            }
//        }, 0, 20).getTaskId();
//        playerVotes.clear();
//        votes.clear();
//        voting = true;
//        secondsLeft = 20;
//        playersVoted = 0;
//        playerBoards.clear();
//        voteBoard = Bukkit.getScoreboardManager().getNewScoreboard();
//        objective = voteBoard.registerNewObjective("vote", "dummy", Component.text("VOTING")
//                .decoration(TextDecoration.BOLD, true)
//                .color(TextColor.color(255, 255, 85)));
//        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
//        objective.getScore("Current Votes:").setScore(GameMap.getMaps().size() + 4);
//        objective.getScore(ChatColor.WHITE + " ").setScore(2);
//
//        objective.getScore(ChatColor.LIGHT_PURPLE + " ").setScore(5);
//        objective.getScore(ChatColor.LIGHT_PURPLE + "Root Wars " + ChatColor.WHITE + "on " + ChatColor.YELLOW + "Lopixel").setScore(1);
//
//        for (GameMap map : GameMap.getMaps().values()) {
//            votes.put(map.getDisplayName(), 0);
//        }
//        Team playersVoted = voteBoard.registerNewTeam("playersVoted");
//        playersVoted.addEntry("Players Voted: ");
//        playersVoted.setSuffix("0/" + Bukkit.getOnlinePlayers());
//        playersVoted.setColor(ChatColor.AQUA);
//        Team timeLeft = voteBoard.registerNewTeam("timeLeft");
//        timeLeft.addEntry("Time Left: ");
//        objective.getScore("Time Left: ").setScore(3);
//        timeLeft.setSuffix(String.valueOf(secondsLeft));
//        timeLeft.setColor(ChatColor.AQUA);
//        objective.getScore("Players Voted: ").setScore(4);
//        Team[] teams = new Team[GameMap.getMaps().size()];
//        for (int i = 0; i < teams.length; i++) {
//
//            teams[i] = voteBoard.registerNewTeam("team" + i);
//            teams[i].addEntry(votes.keySet().toArray()[i] + ": ");
//            teams[i].setSuffix(String.valueOf(votes.values().toArray()[i]));
//            objective.getScore(votes.keySet().toArray()[i] + ": ").setScore(i + 1);
//        }
//
//        updateBoard();
//
//        for (Player p : Bukkit.getOnlinePlayers()) {
//            p.sendActionBar(ChatColor.YELLOW+"Drop the map you want to vote for.");
//            playerBoards.put(p.getUniqueId(), voteBoard);
//            p.getInventory().clear();
//            p.setScoreboard(playerBoards.get(p.getUniqueId()));
//            for (GameMap map : GameMap.getMaps().values()) {
//                p.getInventory().addItem(map.getMap());
//            }
//        }
//    }
//
//    public static boolean isVoting() {
//        return voting;
//    }
//
//    public static void addVote(String map, UUID playerID) {
//        if (votes.containsKey(map)) {
//            Bukkit.getPlayer(playerID).playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f,1f));
//
//            if(Bukkit.getOnlinePlayers().size()-playersVoted==1&&secondsLeft>=10){
//                secondsLeft=10;
//            } else if(Bukkit.getOnlinePlayers().size()-playersVoted==0&&secondsLeft>=5){
//                secondsLeft=5;
//            }
//        }
//    }
//
//    public static void updateBoard() {
//        List<Map.Entry<String, Integer>> entryVotes = new ArrayList<>();
//        entryVotes.addAll(votes.entrySet());
//        entryVotes.sort(new VoteComparator());
//        List<Integer> displayedMapID = new ArrayList<>();
//        for (int i = 0; i < votes.size(); i++) {
//            Team team = voteBoard.getTeam("team" + i);
//            team.setSuffix(String.valueOf(votes.get(team.getEntries().iterator().next().substring(0, team.getEntries().iterator().next().length() - 2))));
//            Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>(String.valueOf(team.getEntries().iterator().next().substring(0, team.getEntries().iterator().next().length() - 2)), votes.get(String.valueOf(team.getEntries().iterator().next().substring(0, team.getEntries().iterator().next().length() - 2))));
//            if (votes.get(entry.getKey()) >= 1) {
//                displayedMapID.add(i);
//            } else {
//                objective.getScore(entry.getKey() + ": ").resetScore();
//            }
//            if (entryVotes.indexOf(entry) + 1 == votes.size() && votes.get(entry.getKey()) != 0) {
//                team.setColor(ChatColor.GREEN);
//                winningMap = GameMap.DisplaytoMapName(entry.getKey());
//            } else if (votes.get(entry.getKey()) >= 1) {
//                team.setColor(ChatColor.WHITE);
//            }
//        }
//        if(winningMap.equals("")){
//            winningMap = (String) GameMap.getMaps().keySet().toArray()[new Random().nextInt(0, GameMap.getMaps().size())];
//        }
//
//        List<Map.Entry<String, Integer>> entryVotesLimited = new ArrayList<>();
//        for (Map.Entry<String, Integer> e : votes.entrySet()) {
//            if (e.getValue() >= 1) {
//                entryVotesLimited.add(e);
//            }
//        }
//        for(Player p : Bukkit.getOnlinePlayers()){
//            if(!playerVotes.containsKey(p.getUniqueId())) {
//                p.sendActionBar(ChatColor.YELLOW + "Drop the map you want to vote for.");
//            } else{
//                p.sendActionBar(ChatColor.GREEN + "You voted for %s.".formatted(playerVotes.get(p.getUniqueId())));
//            }
//        }
//
//        entryVotesLimited.sort(new VoteComparator());
//        for (int i : displayedMapID) {
//            Team team = voteBoard.getTeam("team" + i);
//            Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>(String.valueOf(team.getEntries().iterator().next().substring(0, team.getEntries().iterator().next().length() - 2)), votes.get(String.valueOf(team.getEntries().iterator().next().substring(0, team.getEntries().iterator().next().length() - 2))));
//            objective.getScore(entry.getKey() + ": ").setScore(entryVotesLimited.indexOf(entry) + 6);
//            playerBoards.clear();
//            for(Player p : Bukkit.getOnlinePlayers()){
//                playerBoards.put(p.getUniqueId(), voteBoard);
//            }
//        }
//        voteBoard.getTeam("playersVoted").setSuffix(playersVoted + "/" + Bukkit.getOnlinePlayers().size());
//        voteBoard.getTeam("timeLeft").setSuffix(String.valueOf(secondsLeft));
//
//        objective.getScore("Current Votes:").setScore(displayedMapID.size() + 6);
//    }
//
//public static class VoteComparator implements Comparator<Map.Entry<String, Integer>> {
//
//    @Override
//    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//        return o1.getValue() - o2.getValue();
//    }
//}
