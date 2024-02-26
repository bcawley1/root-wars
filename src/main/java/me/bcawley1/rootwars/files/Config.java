package me.bcawley1.rootwars.files;

import me.bcawley1.rootwars.RootWars;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Config {
    public static void setup() {
        checkAndAddFile("GameModes");
        checkAndAddFile("Maps");
        checkAndAddFile("shop.json");
        checkAndAddFile("spawn.schem");
    }

    private static void checkAndAddFile(String fileName) {
        File file = new File(RootWars.getPlugin().getDataFolder().getAbsolutePath() + "/" + fileName);
        File original = new File("src/main/resources/" + fileName);
        if (file.exists()){
        } else {
            try {
                Files.copy(original.toPath(), file.toPath());
            } catch (IOException ignored){}
        }
    }
}
