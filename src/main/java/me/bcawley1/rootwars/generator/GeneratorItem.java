package me.bcawley1.rootwars.generator;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public record GeneratorItem(Material item, int chance) implements Comparable<GeneratorItem> {
    @Override
    public int compareTo(@NotNull GeneratorItem o) {
        return (chance - o.chance());
    }
}