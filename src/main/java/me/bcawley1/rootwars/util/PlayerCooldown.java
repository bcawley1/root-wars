package me.bcawley1.rootwars.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCooldown {
    private Map<UUID, Long> cooldowns;
    private final int length;

    public PlayerCooldown(int length) {
        this.length = length;
        cooldowns = new HashMap<>();
    }

    public void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    public long getCooldown(UUID uuid) {
        long cooldown;
        if (!cooldowns.containsKey(uuid)) {
            return 0;
        } else if ((cooldowns.get(uuid)+length)-System.currentTimeMillis()<=0) {
            cooldowns.remove(uuid);
            return 0;
        } else {
            return ((cooldowns.get(uuid)+length)-System.currentTimeMillis());
        }
    }
}
