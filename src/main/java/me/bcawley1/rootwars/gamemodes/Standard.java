package me.bcawley1.rootwars.gamemodes;

import org.bukkit.Material;

public class Standard extends GameMode {
    public Standard() {
        super("Standard","""
                The standard Root Wars experience.""", Material.BLUE_WOOL, 5, new String[]{"blue","red","green","yellow"}, 20);
        gameModes.put("Standard", this);
        emeraldCooldown = 600;
        diamondCooldown = 300;
    }
}
