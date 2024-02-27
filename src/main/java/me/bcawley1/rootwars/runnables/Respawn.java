package me.bcawley1.rootwars.runnables;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Respawn extends BukkitRunnable {
    private int time;
    private final Player p;

    public Respawn(int time, Player p) {
        this.time = time;
        this.p = p;
    }

    @Override
    public void run() {
        time--;
        p.sendTitle(String.valueOf(time), "", 0, 20, 0);
        if(time<=0) {
            RootWars.getPlayer(p).respawnPlayer();
            this.cancel();
        }
    }
}
