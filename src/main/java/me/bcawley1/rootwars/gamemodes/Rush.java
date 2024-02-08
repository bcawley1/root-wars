//package me.bcawley1.rootwars.gamemodes;
//
//import me.bcawley1.rootwars.util.GeneratorData;
//import me.bcawley1.rootwars.util.GeneratorItem;
//import org.bukkit.Material;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
//
//public class Rush extends GameMode{
//    public Rush() {
//        super("Rush", """
//                The standard Root Wars experience,
//                but twice as fast""", Material.GREEN_WOOL, 5, new String[]{"blue","red","green","yellow"},10,
//                new GeneratorData[]{
//                        new GeneratorData(7, new GeneratorItem(Material.IRON_INGOT, 80), new GeneratorItem(Material.GOLD_INGOT, 20)),
//                        new GeneratorData(5, new GeneratorItem(Material.IRON_INGOT, 75), new GeneratorItem(Material.GOLD_INGOT, 25)),
//                        new GeneratorData(3, new GeneratorItem(Material.IRON_INGOT, 74), new GeneratorItem(Material.GOLD_INGOT, 25), new GeneratorItem(Material.EMERALD, 1)),
//                        new GeneratorData(1, new GeneratorItem(Material.IRON_INGOT, 72), new GeneratorItem(Material.GOLD_INGOT, 25), new GeneratorItem(Material.EMERALD, 3))},
//                new GeneratorData[]{
//                        new GeneratorData(600, new GeneratorItem(Material.EMERALD, 100)),
//                        new GeneratorData(300, new GeneratorItem(Material.EMERALD, 100)),
//                        new GeneratorData(150, new GeneratorItem(Material.EMERALD, 100))},
//                new GeneratorData[]{
//                        new GeneratorData(300, new GeneratorItem(Material.DIAMOND, 100)),
//                        new GeneratorData(150, new GeneratorItem(Material.DIAMOND, 100)),
//                        new GeneratorData(75, new GeneratorItem(Material.DIAMOND, 100))});
//        effects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, -1, 5, false, false, false));
//    }
//}