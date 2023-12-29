package me.bcawley1.rootwars;

import org.bukkit.entity.Player;

public record GamePlayer(Player player, GameTeam team) { }
