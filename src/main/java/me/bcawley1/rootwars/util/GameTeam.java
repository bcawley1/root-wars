package me.bcawley1.rootwars.util;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.generator.GeneratorData;
import me.bcawley1.rootwars.maps.GameMap;
import me.bcawley1.rootwars.maps.TeamData;
import me.bcawley1.rootwars.generator.Generator;
import me.bcawley1.rootwars.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameTeam {
    private final TeamColor color;
    private final TeamData teamData;
    private boolean hasRoot;
    private final Generator generator;
    private int protection;
    private int sharpness;
    private int rootCheckID;
    private final List<Player> playersInTeam;
    private final Shop shop;

    public GameTeam(String name, GeneratorData... generatorData) {
        shop = new Shop();
        GameMap map = RootWars.getCurrentMap();
        playersInTeam = new ArrayList<>();
        protection = 0;
        sharpness = 0;
        this.color = TeamColor.valueOf(name.toUpperCase());
        hasRoot = true;
        teamData = map.getTeamData(color);
        generator = new Generator(teamData.getGeneratorLocation().add(0.5, 0.5, 0.5), generatorData);

        rootCheckID = Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), () -> {
            if (!RootWars.getWorld().getBlockAt(teamData.getRootLocation()).getType().equals(Material.MANGROVE_ROOTS)) {
                breakRoot();
            }
        }, 0, 1).getTaskId();
    }

    public List<Player> getPlayersInTeam() {
        return playersInTeam;
    }

    public int numPlayersInTeam() {
        return playersInTeam.size();
    }

    public void removePlayer(Player p) {
        playersInTeam.remove(p);
        RootWars.getPlayer(p).setTeam(null);
    }

    public void addPlayer(Player p) {
        playersInTeam.add(p);
        RootWars.getPlayer(p).setTeam(this);
    }

    public Generator getGenerator() {
        return generator;
    }

    public void upgradeProtection() {
        protection++;
        for (Player p : playersInTeam) {
            for (ItemStack armor : p.getInventory().getArmorContents()) {
                if (armor != null) {
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
                }
            }
        }
    }

    public int getProtection() {
        return protection;
    }

    public int getSharpness() {
        return sharpness;
    }

    public void upgradeSharpness() {
        sharpness++;
        for (Player p : playersInTeam) {
            for (ItemStack item : p.getInventory()) {
                if (item != null) {
                    switch (item.getType()) {
                        case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD ->
                                item.addEnchantment(Enchantment.DAMAGE_ALL, sharpness);
                    }
                }
            }
        }
    }

    public String getName() {
        return color.toString().toLowerCase();
    }

    public boolean isItemVillager(Location location) {
        return teamData.getItemVillager().equals(location.add(-0.5, 0, -0.5));
    }

    public boolean isUpgradeVillager(Location location) {
        return teamData.getUpgradeVillager().equals(location.add(-0.5, 0, -0.5));
    }

    public boolean hasRoot() {
        return hasRoot;
    }

    public void breakRoot() {
        hasRoot = false;
        RootWars.getCurrentGameMode().onRootBreak(this);
        Bukkit.getScheduler().cancelTask(rootCheckID);
    }

    public Shop getShop() {
        return shop;
    }

    public TeamData getTeamData() {
        return teamData;
    }

    public void spawnVillagers() {
        Villager itemVillager = (Villager) RootWars.getWorld().spawnEntity(teamData.getItemVillager().add(0.5, 0, 0.5), EntityType.VILLAGER);
        Villager upgVillager = (Villager) RootWars.getWorld().spawnEntity(teamData.getUpgradeVillager().add(0.5, 0, 0.5), EntityType.VILLAGER);
        setVillagerStuff(itemVillager);
        setVillagerStuff(upgVillager);
    }

    private void setVillagerStuff(Villager villager) {
        villager.setGravity(false);
        villager.setInvulnerable(true);
        villager.setPersistent(true);
        villager.setAI(false);
    }

    @Override
    public String toString() {
        return playersInTeam.toString();
    }

    public enum TeamColor {RED, BLUE, GREEN, YELLOW}
}
