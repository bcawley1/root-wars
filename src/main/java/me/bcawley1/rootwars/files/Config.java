package me.bcawley1.rootwars.files;

import me.bcawley1.rootwars.RootWars;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    public static void setup() throws URISyntaxException, IOException {
        URL url = Config.class.getResource("/Maps");
        Path path = Paths.get(url.toURI());
        Files.walk(path, 1).forEach(p -> System.out.printf("- %s%n", p.toString()));
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
