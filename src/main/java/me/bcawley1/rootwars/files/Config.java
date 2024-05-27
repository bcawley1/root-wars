package me.bcawley1.rootwars.files;

import me.bcawley1.rootwars.RootWars;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Config {
    private static boolean fileExistsError;

    private static List<Path> getFiles(String s) throws IOException, URISyntaxException {
        //This method returns a list of paths of the items under the directory passed in
        URI uri = Config.class.getResource(s).toURI();
        final Map<String, String> env = new HashMap<>();
        final String[] array = uri.toString().split("!");
        final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
        final Path path = fs.getPath(array[1]);
        Stream<Path> files = Files.walk(path, 1);
        List<Path> paths = List.copyOf(files.toList());
        fs.close();
        return paths;
    }

    public static void setup() throws URISyntaxException, IOException {
        fileExistsError = false;
        //Finds and adds all the files from the resources folder if they don't exist
        getFiles("/Maps").forEach(p -> {
            if (!p.toString().substring(5).isEmpty()) {
                checkAndAddFile((p + "/" + p.toString().substring(6) + ".json").substring(1));
                checkAndAddFile((p + "/" + p.toString().substring(6) + ".schem").substring(1));
            }
        });

        getFiles("/GameModes").forEach(p -> {
            if (!p.toString().substring(10).isEmpty()) {
                checkAndAddFile(p.toString().substring(1));
            }
        });

//        checkAndAddFile("shop.json");
        checkAndAddFile("spawn.schem");
    }

    private static void checkAndAddFile(String fileName) {
        //If a resource doesn't exist, it adds it
        if (!new File(RootWars.getPlugin().getDataFolder().getAbsolutePath() + "/" + fileName).exists()) {
            Logger logger = RootWars.getPlugin().getLogger();
            if (!fileExistsError) {
                logger.log(Level.WARNING, "Ignore these warnings if this is your first time starting the plugin");
                fileExistsError = true;
            }
            logger.log(new LogRecord(Level.WARNING, "Resource '%s' not found. Copying it now.".formatted(fileName)));

            RootWars.getPlugin().saveResource(fileName, true);
        }
    }
}