package me.bcawley1.rootwars.generator;

import java.util.Arrays;

public record GeneratorData(int delay, GeneratorItem[] items) {
    public GeneratorData {
        Arrays.sort(items);
    }
}
