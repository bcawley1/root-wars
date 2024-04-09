package me.bcawley1.rootwars.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class ScheduledEvent implements Comparable<ScheduledEvent> {
    protected final int delay;
    protected final String name;
    protected final String message;
    protected final String runnable;
    private BukkitTask task;


    @JsonCreator
    protected ScheduledEvent(@JsonProperty("name") String name, @JsonProperty("message") String message, @JsonProperty("delay") int delay, @JsonProperty("runnable") String runnable) {
        this.name = name;
        this.message = message;
        this.delay = delay;
        this.runnable = runnable;
    }

    public void scheduleEvent() {

        if (isAlreadyScheduled()) {
            task.cancel();
        }
        task = Bukkit.getScheduler().runTaskLater(RootWars.getPlugin(), RootWars.getCurrentGameMode().getRunnable(runnable), delay);
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
