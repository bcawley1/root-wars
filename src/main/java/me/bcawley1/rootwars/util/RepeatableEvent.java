package me.bcawley1.rootwars.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;

public class RepeatableEvent extends ScheduledEvent{
    private final int repeatTime;

    @JsonCreator
    private RepeatableEvent(@JsonProperty("name") String name, @JsonProperty("description") String message, @JsonProperty("delay") int delay, @JsonProperty("repeatTime") int repeatTime, @JsonProperty("runnable") String runnable) {
        super(name, message, delay, runnable);
        this.repeatTime = repeatTime;
    }
    @Override
    public void scheduleEvent() {
        if(isAlreadyScheduled()){
           cancelEvent();
        }
        setTask(Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), RootWars.getCurrentGameMode().getRunnable(runnable), delay, repeatTime));
    }
}
