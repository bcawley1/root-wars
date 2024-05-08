package me.bcawley1.rootwars.util;

import org.bukkit.potion.PotionEffectType;

public enum PotionType {
    SPEED(PotionEffectType.SPEED),
    SLOWNESS(PotionEffectType.SLOW),
    HASTE(PotionEffectType.FAST_DIGGING),
    MINING_FATIGUE(PotionEffectType.SLOW_DIGGING),
    STRENGTH(PotionEffectType.INCREASE_DAMAGE),
    INSTANT_HEALTH(PotionEffectType.HEAL),
    INSTANT_DAMAGE(PotionEffectType.HARM),
    JUMP_BOOST(PotionEffectType.JUMP),
    NAUSEA(PotionEffectType.CONFUSION),
    REGENERATION(PotionEffectType.REGENERATION),
    RESISTANCE(PotionEffectType.DAMAGE_RESISTANCE),
    FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE),
    WATER_BREATHING(PotionEffectType.WATER_BREATHING),
    INVISIBILITY(PotionEffectType.INVISIBILITY),
    BLINDNESS(PotionEffectType.BLINDNESS),
    NIGHT_VISION(PotionEffectType.NIGHT_VISION),
    HUNGER(PotionEffectType.HUNGER),
    WEAKNESS(PotionEffectType.WEAKNESS),
    POISON(org.bukkit.potion.PotionEffectType.POISON),
    WITHER(PotionEffectType.WITHER),
    HEALTH_BOOST(PotionEffectType.HEALTH_BOOST),
    ABSORPTION(PotionEffectType.ABSORPTION),
    SATURATION(PotionEffectType.SATURATION),
    GLOWING(PotionEffectType.GLOWING),
    LEVITATION(PotionEffectType.LEVITATION),
    LUCK(PotionEffectType.LUCK),
    UNLUCK(PotionEffectType.UNLUCK),
    SLOW_FALLING(PotionEffectType.SLOW_FALLING),
    CONDUIT_POWER(PotionEffectType.CONDUIT_POWER),
    DOLPHINS_GRACE(PotionEffectType.DOLPHINS_GRACE),
    BAD_OMEN(PotionEffectType.BAD_OMEN),
    HERO_OF_THE_VILLAGE(PotionEffectType.HERO_OF_THE_VILLAGE),
    DARKNESS(PotionEffectType.DARKNESS);


    private final PotionEffectType type;
    PotionType(PotionEffectType type) {
        this.type = type;
    }

    public PotionEffectType getType() {
        return type;
    }
}
