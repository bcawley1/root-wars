package me.bcawley1.rootwars.util;

import java.util.Arrays;

public record GeneratorData(int delay, GeneratorItem[] items) {
    public GeneratorData {
        Arrays.sort(items, new GeneratorItem.GeneratorItemComparator());
    }
}
