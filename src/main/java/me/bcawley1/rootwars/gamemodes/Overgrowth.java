package me.bcawley1.rootwars.gamemodes;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class Overgrowth extends GameMode {
    private Set<Location> expandedBlocks;
    private int rootExpand;

    public Overgrowth() {
        super("overgrowth");
//        events.add(new ScheduledEvent("Root Expands", 600, () -> teams.forEach(team -> {
//            Vector direction = new Vector();
//            direction.setX(0.0D + Math.random() - Math.random());
//            direction.setY(Math.random());
//            direction.setZ(0.0D + Math.random() - Math.random());
//            return direction;
//            team.getRootLocation().toVector().
//        })));
    }

    @Override
    public void startGame() {
        super.startGame();
        expandedBlocks = new HashSet<>();
        teams.forEach(gameTeam -> expandedBlocks.add(gameTeam.getRootLocation()));
        rootExpand = 0;
    }

    @Override
    protected Runnable getRunnable(String s) {
        return super.getRunnable(s) != null ? super.getRunnable(s) : Events.valueOf(s).getRunnable();
    }

    private enum Events {
        ROOTS_EXPAND(() -> {
            if (!(((Overgrowth) RootWars.getCurrentGameMode()).rootExpand >= 10)) {
                Random random = new Random();
                List<Location> expandedBlocks = new ArrayList<>(((Overgrowth) RootWars.getCurrentGameMode()).expandedBlocks);
                ((Overgrowth) RootWars.getCurrentGameMode()).expandedBlocks.clear();
                for (Location loc : expandedBlocks) {
                    for (int i = 0; i < 4; i++) {
                        Location expanded;
                        do {
                            expanded = new Location(RootWars.getWorld(), loc.getX(), loc.getY(), loc.getZ());
                            expanded.add(random.nextInt(-2, 2), random.nextInt(-2, 2), random.nextInt(-2, 2));
                        } while (expandedBlocks.contains(expanded));
                        RootWars.getWorld().getBlockAt(expanded).setType(Material.MANGROVE_ROOTS);
                        ((Overgrowth) RootWars.getCurrentGameMode()).expandedBlocks.add(expanded);
                    }
                }
            }
        });

        private final Runnable runnable;

        Events(Runnable runnable) {
            this.runnable = runnable;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }
}
