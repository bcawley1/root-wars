package me.bcawley1.rootwars.generator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public record GeneratorData(@JsonProperty int delay, @JsonProperty GeneratorItem[] items) {
    public GeneratorData {
        Arrays.sort(items);
    }
}
