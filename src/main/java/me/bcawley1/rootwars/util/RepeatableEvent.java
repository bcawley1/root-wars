package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;

public class RepeatableEvent extends ScheduledEvent{
    private final int repeatTime;

    public RepeatableEvent(String name, int delay, int repeatTime, Runnable runnable) {
        super(name, delay, runnable);
        this.repeatTime = repeatTime;
    }
    @Override
    public void scheduleEvent() {
        if(isAlreadyScheduled()){
           cancelEvent();
        }
        setTask(Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), runnable, delay, repeatTime));
    }
}
