package me.bcawley1.rootwars.util;

import java.util.List;

public record GeneratorData(List<GeneratorItem> items, int delay) {
    public GeneratorData {
        items.sort(new GeneratorItem.GeneratorItemComparator());
    }
}
