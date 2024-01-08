package me.bcawley1.rootwars.gamemodes;

import org.bukkit.Material;

public class TwoTeams extends GameMode {
    public TwoTeams() {
        super("Two Teams", """
                Root Wars but with only 2 teams!""", Material.RED_WOOL, 5, new String[]{"blue","red"},20);
        gameModes.put("Two Teams", this);
        emeraldCooldown = 600;
        diamondCooldown = 300;
    }
}
