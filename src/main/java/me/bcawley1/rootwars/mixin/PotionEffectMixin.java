package me.bcawley1.rootwars.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.potion.PotionEffectType;

public abstract class PotionEffectMixin {
    @JsonIgnore
    public static int INFINITE_DURATION;
    @JsonIgnore
    private static String AMPLIFIER;
    @JsonIgnore
    private static String DURATION;
    @JsonIgnore
    private static String TYPE;
    @JsonIgnore
    private static String AMBIENT;
    @JsonIgnore
    private static String PARTICLES;
    @JsonIgnore
    private static String ICON;
    @JsonProperty
    private int amplifier;
    @JsonProperty
    private int duration;
    @JsonProperty
    private PotionEffectType type;
    @JsonProperty
    private boolean ambient;
    @JsonProperty
    private boolean particles;
    @JsonProperty
    private boolean icon;
}
