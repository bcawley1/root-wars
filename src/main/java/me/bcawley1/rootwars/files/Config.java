package me.bcawley1.rootwars.files;

import me.bcawley1.rootwars.RootWars;

import java.io.File;

public class Config {
    public static void setup() {
        //I'd like this to be not hard coded, but this is the only solution I have currently.
        String[] maps = {"Greenery", "Grimace", "John Pork", "Smurf Cat"};
        String[] gameModes = {"nobuild", "overgrowth", "rush", "standard", "twoteams"};

        for(String map : maps){
            checkAndAddFile("Maps/%s/%s.json".formatted(map, map));
            checkAndAddFile("Maps/%s/%s.schem".formatted(map, map));
        }
        for(String gameMode : gameModes){
            checkAndAddFile("GameModes/%s.json".formatted(gameMode));
        }

        checkAndAddFile("shop.json");
        checkAndAddFile("spawn.schem");
    }

    private static void checkAndAddFile(String fileName) {
        if (!new File(RootWars.getPlugin().getDataFolder().getAbsolutePath() + "/" + fileName).exists()) {
            RootWars.getPlugin().saveResource(fileName, true);
        }
    }
}
