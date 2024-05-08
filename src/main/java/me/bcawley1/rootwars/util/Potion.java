package me.bcawley1.rootwars.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.potion.PotionEffect;

public record Potion(@JsonProperty PotionType type, @JsonProperty int amplifier) {
    @JsonIgnore
    public PotionEffect getPotionEffect(){
        return new PotionEffect(type.getType(), -1, amplifier, false, false, false);
    }
}
