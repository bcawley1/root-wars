package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.Generator;
import me.bcawley1.rootwars.GeneratorItem;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameTeam {
    private final String color;
    private boolean hasRoot;
    private final Location itemVilLoc;
    private final Location upgVilLoc;
    private final Location rootLoc;
    private final Location spawnLoc;
    private final Location genLocation;
    private Generator generator;
    private boolean genUpgrade;
    private boolean protection;
    private boolean sharpness;
    private GameMap map;
    private int rootCheckID;
    private List<Player> playersInTeam;

    public GameTeam(GameMap map, String name) {
        playersInTeam = new ArrayList<>();
        protection = false;
        sharpness = false;
        genUpgrade = false;
        this.color = name;
        this.map = map;
        hasRoot = true;
        itemVilLoc = map.getItemVillagerLocation(name);
        List<GeneratorItem> items = new ArrayList<>(List.of(
                new GeneratorItem(new ItemStack(Material.IRON_INGOT), 90),
                new GeneratorItem(new ItemStack(Material.GOLD_INGOT), 10)));
        generator = new Generator((int) map.getGeneratorLocation(name).getX(), (int) map.getGeneratorLocation(name).getY(), (int) map.getGeneratorLocation(name).getZ(), 15, items);
        upgVilLoc = map.getUpgradeVillager(name);
        genLocation = map.getGeneratorLocation(name);
        spawnLoc = map.getSpawnPointLocation(name);
        rootLoc = map.getRootLocation(name);
        rootCheckID = Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            if(!RootWars.getWorld().getBlockAt(rootLoc).getType().equals(Material.MANGROVE_ROOTS)){
                hasRoot = false;
                RootWars.getCurrentGameMode().onRootBreak(this);
                Bukkit.getScheduler().cancelTask(rootCheckID);
            }
        }, 0, 1).getTaskId();
    }

    public List<Player> getPlayersInTeam() {
        return playersInTeam;
    }
    public int numPlayersInTeam(){
        return playersInTeam.size();
    }
    public void removePlayer(Player p){
        playersInTeam.remove(p);
        RootWars.getPlayer(p).setTeam(null);
    }

    public void addPlayer(Player p){
        playersInTeam.add(p);
        RootWars.getPlayer(p).setTeam(this);
    }

    public void setGenUpgrade(boolean genUpgrade) {
        this.genUpgrade = genUpgrade;
    }

    public boolean isGenUpgrade() {
        return genUpgrade;
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
        return color;
    }

    public GameMap getMap() {
        return map;
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }

    public boolean isItemVillager(Location location){
        return itemVilLoc.equals(location);
    }
    public boolean isUpgradeVillager(Location location){
        return upgVilLoc.equals(location);
    }

    public boolean hasRoot() {
        return hasRoot;
    }
    public Location getRootLocation(){
        return rootLoc;
    }
    public void removeGenerator(){
        Generator.removeGenerator(genLocation);
    }

    public void spawnVillagers() {
        Villager ItemVillager = (Villager) RootWars.getWorld().spawnEntity(itemVilLoc.add(0.5, 0, 0.5), EntityType.VILLAGER);
        ItemVillager.setGravity(false);
        ItemVillager.setInvulnerable(true);
        ItemVillager.setPersistent(true);
        ItemVillager.setAI(false);
        Villager UpgVillager = (Villager) RootWars.getWorld().spawnEntity(upgVilLoc.add(0.5, 0, 0.5), EntityType.VILLAGER);
        UpgVillager.setGravity(false);
        UpgVillager.setInvulnerable(true);
        UpgVillager.setPersistent(true);
        UpgVillager.setAI(false);

    }

    public void upgradeGenerator(List<GeneratorItem> list, int delay){
        Generator.removeGenerator(genLocation);
        generator = new Generator((int) genLocation.getX(), (int) genLocation.getY(), (int) genLocation.getZ(), delay, list);
    }

    @Override
    public String toString() {
        return playersInTeam.toString();
    }
}
