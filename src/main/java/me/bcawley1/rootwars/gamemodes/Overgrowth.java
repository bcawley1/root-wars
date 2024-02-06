package me.bcawley1.rootwars.gamemodes;

import me.bcawley1.rootwars.util.ScheduledEvent;
import org.bukkit.Material;

public class Overgrowth extends GameMode {
    protected Overgrowth() {
        super("Overgrowth", """
                Every 30 seconds, roots will rapidly expand.
                Players must fight through the mess of roots to find the real root.""", Material.YELLOW_WOOL, 20, new String[]{"blue", "red", "green", "yellow"}, 20);
        events.add(new ScheduledEvent("Root Expands", 600, () -> {
            teams.forEach(team -> {
                Vector direction = new Vector();
                direction.setX(0.0D + Math.random() - Math.random());
                direction.setY(Math.random());
                direction.setZ(0.0D + Math.random() - Math.random());
                return direction;
                team.getRootLocation().toVector().
            });
        }));
    }
}
