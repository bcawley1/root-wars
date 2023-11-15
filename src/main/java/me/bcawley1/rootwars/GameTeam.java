package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

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
    private boolean genUpgrade;
    private boolean protection;
    private boolean sharpness;
    private GameMap map;
    private int rootCheckID;
    private int playersAlive;

    public GameTeam(GameMap map, String name, JavaPlugin plugin) {
        protection = false;
        sharpness = false;
        genUpgrade = false;
        this.name = name;
        this.plugin = plugin;
        this.map = map;
        isRoot = true;
        playersInTeam = new ArrayList<>();
        itemVilLoc = map.getItemVillagerLocation(name);
        List<GeneratorItem> items = new ArrayList<>(List.of(
                new GeneratorItem(new ItemStack(Material.IRON_INGOT), 90),
                new GeneratorItem(new ItemStack(Material.GOLD_INGOT), 10)));
        generator = new Generator(plugin, (int) map.getGeneratorLocation(name).getX(), (int) map.getGeneratorLocation(name).getY(), (int) map.getGeneratorLocation(name).getZ(), 15, items);
        upgVilLoc = map.getUpgradeVillager(name);
        genLocation = map.getGeneratorLocation(name);
        spawnLoc = map.getSpawnPointLocation(name);
        rootLoc = map.getRootLocation(name);
        rootCheckID = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(!Bukkit.getWorld("world").getBlockAt(rootLoc).getType().equals(Material.MANGROVE_ROOTS)){
                isRoot = false;
                RootWars.getCurrentGameMode().onRootBreak(this);
                Bukkit.getScheduler().cancelTask(rootCheckID);
            }
        }, 0, 1).getTaskId();
    }

    public void setGenUpgrade(boolean genUpgrade) {
        this.genUpgrade = genUpgrade;
    }

    public boolean isGenUpgrade() {
        return genUpgrade;
    }

    public int getPlayersAlive() {
        return playersAlive;
    }
    public void removePlayersAlive(){
        playersAlive--;
    }

    public void setProtection(boolean protection) {
        this.protection = protection;
    }

    public void setSharpness(boolean sharpness) {
        this.sharpness = sharpness;
    }

    public boolean isProtection() {
        return protection;
    }

    public boolean isSharpness() {
        return sharpness;
    }

    public String getName() {
        return name;
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
        playersAlive++;
    }
    public void respawnPlayer(Player player){
        ItemStack[] armor = player.getInventory().getArmorContents();
        player.getInventory().clear();
        player.getInventory().setArmorContents(armor);
        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
        player.setHealth(player.getMaxHealth());
        player.clearActivePotionEffects();
        player.teleport(spawnLoc);
    }
    public boolean isItemVillager(Location location){
        return itemVilLoc.equals(location.add(-0.5, 0, -0.5));
    }
    public boolean isUpgradeVillager(Location location){
        return upgVilLoc.equals(location.add(-0.5, 0, -0.5));
    }

    public boolean isRoot() {
        return isRoot;
    }
    public Location getRootLocation(){
        return rootLoc;
    }
    public void removeGenerator(){
        Generator.removeGenerator(genLocation);
    }
    public static void replacePlayer(Player player){
        for(GameTeam team : RootWars.getTeams().values()) {
            for (Player p : team.playersInTeam) {
                if (p.getName().equals(player.getName())) {
                    team.playersInTeam.remove(p);
                    team.playersInTeam.add(player);
                }
            }
        }
    }

    public void spawnVillagers() {
        Villager ItemVillager = (Villager) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), itemVilLoc.getX()+0.5, itemVilLoc.getY(), itemVilLoc.getZ()+0.5), EntityType.VILLAGER);
        ItemVillager.setGravity(false);
        ItemVillager.setInvulnerable(true);
        ItemVillager.setPersistent(true);
        ItemVillager.setAI(false);
        Villager UpgVillager = (Villager) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), upgVilLoc.getX()+0.5, upgVilLoc.getY(), upgVilLoc.getZ()+0.5), EntityType.VILLAGER);
        UpgVillager.setGravity(false);
        UpgVillager.setInvulnerable(true);
        UpgVillager.setPersistent(true);
        UpgVillager.setAI(false);
    }

    public void upgradeGenerator(List<GeneratorItem> list, int delay){
        Generator.removeGenerator(genLocation);
        generator = new Generator(plugin, (int) genLocation.getX(), (int) genLocation.getY(), (int) genLocation.getZ(), delay, list);
    }

}
