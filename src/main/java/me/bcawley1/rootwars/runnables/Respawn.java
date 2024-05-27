package me.bcawley1.rootwars.runnables;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Respawn extends BukkitRunnable {
    private int time;
    private final UUID id;

    public Respawn(int time, UUID id) {
        this.time = time;
        this.id = id;
    }

    @Override
    public void run() {
        time--;
        Bukkit.getPlayer(id).sendTitle(String.valueOf(time), "", 0, 20, 0);
        if(time<=0) {
            RootWars.respawnPlayer(id);
            this.cancel();
        }
    }
}
