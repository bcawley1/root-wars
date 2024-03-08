package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UpgradableItem extends ActionItem {
    @JsonIgnore
    private int stage;
    private final int numUpgrades;
    @JsonProperty
    private final ItemStack[] cost;

    public UpgradableItem(Material type, BuyActions action, String name, int numUpgrades, ItemStack[] cost) {
        super(name, type, action);
        this.cost = cost;
        this.numUpgrades = numUpgrades;
        stage = 0;
        updateItemMeta();
    }

    public void upgrade() {
        stage++;
        updateItemMeta();
    }

    private void updateItemMeta() {
        String displayName;
        String description;
        if (stage >= numUpgrades - 1) {
            displayName = "&f%s Upgrade: &cMAX".formatted(name);
            description = """
                    &cYou have all of the %s upgrades!""".formatted(name);
        } else {
            displayName = "&f%s Upgrade: &cTier %s".formatted(name, stage + 2);
            description = """
                    &r&7Cost: &f%s %s
                    &eClick to buy!""".formatted(cost[stage].getAmount(), ShopItem.getFormattedName(cost[stage].getType()));
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', description).split("\n")));
    }

    @JsonIgnore
    public ItemStack getCost() {
        return cost[stage];
    }

    @JsonIgnore
    public boolean isMax() {
        return stage >= numUpgrades - 1;
    }

    public boolean defaultBuyCheck(Player p) {
        if (isMax()) {
            p.sendMessage(ChatColor.RED + "You cannot buy anymore %s upgrades", name);
        } else if (p.getInventory().containsAtLeast(getCost(), getCost().getAmount())) {
            for (Player player : RootWars.getPlayer(p).getTeam().getPlayersInTeam()) {
                player.sendMessage("You purchased %s upgrade!!!!!".formatted(name));
            }
            p.getInventory().removeItem(getCost());
            upgrade();
            p.openInventory(RootWars.getPlayer(p).getTeam().getShop().getUpgradeTab(p));
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
        return false;
    }
}
