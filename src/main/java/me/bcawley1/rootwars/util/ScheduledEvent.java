package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class ScheduledEvent implements Comparable<ScheduledEvent>{
    private final int delay;
    private final String name;
    private final Runnable runnable;
    private BukkitTask task;


    public ScheduledEvent(String name, int delay, Runnable runnable) {
        this.name = name;
        this.delay = delay;
        this.runnable = runnable;
    }

    public void scheduleEvent(){
        if(task!=null){
            task.cancel();
        }
        task = Bukkit.getScheduler().runTaskLater(RootWars.getPlugin(), runnable, delay);
    }

    public void cancelEvent(){
        task.cancel();
    }

    public String getName() {
        return name;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public int compareTo(@NotNull ScheduledEvent o) {
        return delay-o.delay;
    }
}
