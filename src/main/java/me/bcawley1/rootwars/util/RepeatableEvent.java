package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;

public class RepeatableEvent extends ScheduledEvent{

    public RepeatableEvent(String name, int delay, int repeatTime, Runnable runnable, EventType type) {
        super(name, delay, runnable, type);
    }
    @Override
    public void scheduleEvent() {
        if(isAlreadyScheduled()){
           cancelEvent();
        }
        task = Bukkit.getScheduler().runTaskLater(RootWars.getPlugin(), runnable, delay);
    }
}
