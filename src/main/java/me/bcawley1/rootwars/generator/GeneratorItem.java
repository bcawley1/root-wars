package me.bcawley1.rootwars.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record GeneratorItem(@JsonProperty Material item, @JsonProperty int chance) implements Comparable<GeneratorItem> {
    @Override
    public int compareTo(@NotNull GeneratorItem o) {
        return (chance - o.chance());
    }
}