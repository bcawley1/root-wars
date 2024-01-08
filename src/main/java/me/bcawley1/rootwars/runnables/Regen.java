package me.bcawley1.rootwars.runnables;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen extends BukkitRunnable {
    public Regen() {
        Bukkit.getOnlinePlayers().forEach(p -> p.setHealth(p.getAttribute((Attribute.GENERIC_MAX_HEALTH)).getValue()));
    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.getHealth()<=p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()-0.5) {
                p.setHealth(p.getHealth() + 0.5);
            }
        }
    }
}
