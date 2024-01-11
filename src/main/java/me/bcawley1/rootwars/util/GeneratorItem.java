package me.bcawley1.rootwars.util;

import org.bukkit.Material;

import java.util.Comparator;

public record GeneratorItem(Material item, int chance) {
    public static class GeneratorItemComparator implements Comparator<GeneratorItem>{

        @Override
        public int compare(GeneratorItem o1, GeneratorItem o2) {
            return (o2.chance()- o1.chance());
        }
    }
}