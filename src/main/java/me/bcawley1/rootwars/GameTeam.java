package me.bcawley1.rootwars;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameTeam {
    private List<Player> playersInTeam;
    private JavaPlugin plugin;
    private String name;
    private Location itemVilLoc;
    private boolean isRoot;
    private Location upgVilLoc;
    private Location rootLoc;
    private Location spawnLoc;
    private Location genLocation;
    private Generator generator;
    private GameMap map;
    private int rootCheckID;
    private List<ShopItem> itemStoreItems;

    public GameTeam(GameMap map, String name, JavaPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.map = map;
        isRoot = true;
        itemStoreItems = new ArrayList<>();
        playersInTeam = new ArrayList<>();
        itemVilLoc = map.getItemVillagerLocation(name);
        List<GeneratorItem> items = new ArrayList<>(List.of(
                new GeneratorItem(new ItemStack(Material.IRON_INGOT), 80),
                new GeneratorItem(new ItemStack(Material.GOLD_INGOT), 20)));
        generator = new Generator(plugin, (int) map.getGeneratorLocation(name).getX(), (int) map.getGeneratorLocation(name).getY(), (int) map.getGeneratorLocation(name).getZ(), 15, items);
        upgVilLoc = map.getUpgradeVillager(name);
        genLocation = map.getGeneratorLocation(name);
        spawnLoc = map.getSpawnPointLocation(name);
        rootLoc = map.getRootLocation(name);
        rootCheckID = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(!Bukkit.getWorld("world").getBlockAt(rootLoc).getType().equals(Material.MANGROVE_ROOTS)){
                isRoot = false;
                for(Player p : playersInTeam){
                    p.playSound(Sound.sound(Key.key("minecraft:entity.warden.roar"), Sound.Source.MASTER, 1f, 1f));
                    p.sendTitle("Your Root Broke", "");
                }
                Bukkit.getScheduler().cancelTask(rootCheckID);
            }
        }, 0, 1).getTaskId();


        if(name.equalsIgnoreCase("red")){
            itemStoreItems.add(new ShopItem(Material.RED_WOOL, 16, Material.IRON_INGOT, 4, "Wool", 19));
        } else if(name.equalsIgnoreCase("blue")){
            itemStoreItems.add(new ShopItem(Material.RED_WOOL, 16, Material.IRON_INGOT, 4, "Wool", 19));
        } else if(name.equalsIgnoreCase("yellow")){
            itemStoreItems.add(new ShopItem(Material.RED_WOOL, 16, Material.IRON_INGOT, 4, "Wool", 19));
        } else if(name.equalsIgnoreCase("green")){
            itemStoreItems.add(new ShopItem(Material.RED_WOOL, 16, Material.IRON_INGOT, 4, "Wool", 19));
        }
        itemStoreItems.add(new ShopItem(Material.STONE_SWORD, 1, Material.IRON_INGOT, 10, "Stone Sword", 20));
        itemStoreItems.add(new ShopItem(Material.CHAINMAIL_BOOTS, 1, Material.IRON_INGOT, 24, "Permanent Chainmail Armor", 21));
        itemStoreItems.add(new ShopItem(Material.GLASS, 4, Material.IRON_INGOT, 12, "Glass",22));
        itemStoreItems.add(new ShopItem(Material.LADDER, 8, Material.IRON_INGOT, 4, "Ladder",23));
        itemStoreItems.add(new ShopItem(Material.HONEY_BOTTLE, 1, Material.EMERALD, 1, "Speed II Potion",24));
        itemStoreItems.add(new ShopItem(Material.ENDER_PEARL, 1, Material.EMERALD, 4, "Ender Pearl",25));

        itemStoreItems.add(new ShopItem(Material.OAK_PLANKS, 16, Material.GOLD_INGOT, 4, "Planks",28));
        itemStoreItems.add(new ShopItem(Material.IRON_SWORD, 1, Material.IRON_INGOT, 7, "Iron Sword",29));
        itemStoreItems.add(new ShopItem(Material.IRON_BOOTS, 1, Material.GOLD_INGOT, 12, "Permanent Iron Armor",30,(p, i) -> {
            if(!p.getInventory().getHelmet().equals(new ItemStack(Material.DIAMOND_HELMET))||p.getInventory().getHelmet().equals(new ItemStack(Material.IRON_HELMET))) {
                ItemStack helmet = new ItemStack(Material.IRON_HELMET);
                ItemMeta helmetMeta = helmet.getItemMeta();
                helmetMeta.setUnbreakable(true);
                ItemStack boots = new ItemStack(Material.IRON_BOOTS);
                ItemMeta bootsMeta = boots.getItemMeta();
                helmetMeta.setUnbreakable(true);
                ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                ItemMeta leggingsMeta = leggings.getItemMeta();
                helmetMeta.setUnbreakable(true);
                p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS).setItemMeta(meta));
                p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            } else {
                p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to iron.");
            }
        }));
        itemStoreItems.add(new ShopItem(Material.BOW, 1, Material.GOLD_INGOT, 12, "Bow",31));
        itemStoreItems.add(new ShopItem(Material.FIRE_CHARGE, 1, Material.IRON_INGOT, 40, "Fireball",32));
        itemStoreItems.add(new ShopItem(Material.HONEY_BOTTLE, 1, Material.EMERALD, 2, "Invisibility Potion",33));
        itemStoreItems.add(new ShopItem(Material.WATER_BUCKET, 1, Material.GOLD_INGOT, 3, "Water Bucket",34));

        itemStoreItems.add(new ShopItem(Material.STICK, 1, Material.GOLD_INGOT, 5, "Knockback Stick",37));
        itemStoreItems.add(new ShopItem(Material.SHEARS, 1, Material.IRON_INGOT, 20, "Shears",38));
        itemStoreItems.add(new ShopItem(Material.WOODEN_PICKAXE, 1, Material.IRON_INGOT, 20, "Wooden Pickaxe",39));
        itemStoreItems.add(new ShopItem(Material.END_STONE, 12, Material.IRON_INGOT, 24, "End Stone",40));
        itemStoreItems.add(new ShopItem(Material.IRON_GOLEM_SPAWN_EGG, 1, Material.IRON_INGOT, 120, "Dream :) Defender",41));
        itemStoreItems.add(new ShopItem(Material.WOODEN_AXE, 1, Material.IRON_INGOT, 12, "Wooden Axe",42));
        itemStoreItems.add(new ShopItem(Material.GOLDEN_APPLE, 1, Material.GOLD_INGOT, 3, "Golden Apple",43));



    }

    public Map<String, Inventory> getItemShop(Player p) {
        Map<String, Inventory> inventories = new HashMap<>();
        Inventory quickBuy = Bukkit.createInventory(p, 54, "Quick Buy");
        for(ShopItem item : itemStoreItems){
            quickBuy.setItem(item.getInvSlot(), item.getShopItem());
        }
        inventories.put("quickBuy", quickBuy);
        return inventories;
    }

    public GameMap getMap() {
        return map;
    }

    public boolean containsPlayer(Player player){
        return playersInTeam.contains(player);
    }

    public List<Player> getPlayersInTeam() {
        return playersInTeam;
    }

    public void addPlayer(Player player){
        playersInTeam.add(player);
    }
    public void respawnPlayer(Player player){
        player.getInventory().clear();
        player.teleport(spawnLoc);
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void spawnVillagers() {
        Villager ItemVillager = (Villager) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), itemVilLoc.getX(), itemVilLoc.getY(), itemVilLoc.getZ()), EntityType.VILLAGER);
        ItemVillager.setGravity(false);
        ItemVillager.setInvulnerable(true);
        ItemVillager.setPersistent(true);
        ItemVillager.setAI(false);
        Villager UpgVillager = (Villager) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), upgVilLoc.getX(), upgVilLoc.getY(), upgVilLoc.getZ()), EntityType.VILLAGER);
        UpgVillager.setGravity(false);
        UpgVillager.setInvulnerable(true);
        UpgVillager.setPersistent(true);
        UpgVillager.setAI(false);
    }

    public void upgradeGenerator(List<GeneratorItem> list){
        Generator.removeGenerator(genLocation);
        generator = new Generator(plugin, (int) genLocation.getX(), (int) genLocation.getY(), (int) genLocation.getZ(), 15, list);
    }

}
