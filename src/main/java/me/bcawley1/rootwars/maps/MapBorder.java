package me.bcawley1.rootwars.maps;

import java.util.Map;

public class MapBorder {
    private final int positiveX;
    private final int positiveY;
    private final int positiveZ;
    private final int negativeX;
    private final int negativeY;
    private final int negativeZ;
    MapBorder(Map<String, Map<String, Long>> mapLocations) {
        positiveX = Math.toIntExact(mapLocations.get("positiveXBorder").get("x"));
        positiveY = Math.toIntExact(mapLocations.get("positiveYBorder").get("y"));
        positiveZ = Math.toIntExact(mapLocations.get("positiveZBorder").get("z"));
        negativeX = Math.toIntExact(mapLocations.get("negativeXBorder").get("x"));
        negativeY = Math.toIntExact(mapLocations.get("negativeYBorder").get("y"));
        negativeZ = Math.toIntExact(mapLocations.get("negativeZBorder").get("z"));
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
