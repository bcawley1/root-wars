package me.bcawley1.rootwars.util;

import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public record GeneratorItem(ItemStack item, int chance) {
    public static class GeneratorItemComparator implements Comparator<GeneratorItem>{

        @Override
        public int compare(GeneratorItem o1, GeneratorItem o2) {
            return (o2.chance()- o1.chance());
        }
    }
}