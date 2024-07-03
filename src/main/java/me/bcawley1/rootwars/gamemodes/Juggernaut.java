package me.bcawley1.rootwars.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Juggernaut extends GameMode {
    public Juggernaut() {
        super();
    }
    public static void registerGameMode(){
        GameMode.registerGameMode(Juggernaut.class);
    }

    @Override
    public void assignPlayersToTeams() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);
        teams.get(0).addPlayer(players.get(0).getUniqueId());
        for (int i = 1; i < players.size(); i++) {
            teams.get(1).addPlayer(players.get(i).getUniqueId());
        }
    }
}
