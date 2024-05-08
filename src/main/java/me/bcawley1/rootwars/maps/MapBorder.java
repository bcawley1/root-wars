package me.bcawley1.rootwars.maps;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MapBorder {
    @JsonProperty
    private final int positiveX;
    @JsonProperty
    private final int positiveY;
    @JsonProperty
    private final int positiveZ;
    @JsonProperty
    private final int negativeX;
    @JsonProperty
    private final int negativeY;
    @JsonProperty
    private final int negativeZ;

    private MapBorder(@JsonProperty("positiveX") int positiveX, @JsonProperty("positiveY") int positiveY, @JsonProperty("positiveZ") int positiveZ,
                     @JsonProperty("negativeX") int negativeX, @JsonProperty("negativeY") int negativeY, @JsonProperty("negativeZ") int negativeZ) {
        this.positiveX = positiveX;
        this.positiveY = positiveY;
        this.positiveZ = positiveZ;
        this.negativeX = negativeX;
        this.negativeY = negativeY;
        this.negativeZ = negativeZ;
    }

    public int getPositiveX() {
        return positiveX;
    }

    public int getPositiveY() {
        return positiveY;
    }

    public int getPositiveZ() {
        return positiveZ;
    }

    public int getNegativeX() {
        return negativeX;
    }

    public int getNegativeY() {
        return negativeY;
    }

    public int getNegativeZ() {
        return negativeZ;
    }
}
