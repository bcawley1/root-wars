package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapImageRenderer extends MapRenderer {
    public static Map<String, Boolean> renderedMaps = new HashMap<>();
    private String imageName;

    public MapImageRenderer(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        if(!renderedMaps.containsKey(imageName)){
            Image image = null;
            try {
                image = ImageIO.read(new File(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/Maps/%s/%s.jpg".formatted(imageName,imageName)));
            } catch (IOException ignored) {}
            renderedMaps.put(imageName, true);
            mapCanvas.drawImage(0, 0, MapPalette.resizeImage(image));
        }
    }
}
