package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class ScheduledEvent implements Comparable<ScheduledEvent> {
    protected final int delay;
    protected final String name;
    protected final Runnable runnable;
    private BukkitTask task;


    public ScheduledEvent(String name, int delay, Runnable runnable) {
        this.name = name;
        this.delay = delay;
        this.runnable = runnable;
    }

    public void scheduleEvent() {
        if (isAlreadyScheduled()) {
            task.cancel();
        }
        task = Bukkit.getScheduler().runTaskLater(RootWars.getPlugin(), runnable, delay);
    }

    protected void setTask(BukkitTask task) {
        this.task = task;
    }

    public boolean isAlreadyScheduled(){
        return task!=null;
    }

    public void cancelEvent() {
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
        return delay - o.delay;
    }

}
