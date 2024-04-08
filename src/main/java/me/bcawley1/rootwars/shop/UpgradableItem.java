package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UpgradableItem extends ActionItem {
    @JsonProperty
    private final ItemStack[] cost;

    UpgradableItem(UpgradableItem item) {
        super(item.name, item.type, item.action);
        this.cost = item.cost;
    }

    @JsonCreator
    private UpgradableItem(@JsonProperty("material") Material type, @JsonProperty("action") BuyActions action, @JsonProperty("name") String name, @JsonProperty("cost") ItemStack[] cost) {
        super(name, type, action);
        this.cost = cost;
    }

    @JsonIgnore
    public ItemStack getCost(int tier) {
        return cost[tier];
    }

    @JsonIgnore
    public boolean isMax(int tier) {
        return tier > cost.length - 1;
    }

    @Override
    public ItemStack getItem(Player p) {
        int tier = RootWars.getPlayer(p).getTeam().getUpgrade(name);
        String displayName;
        String description;
        if (isMax(tier)) {
            displayName = "§f%s Upgrade: §cMAX".formatted(name);
            description = """
                    §cYou have all of the %s upgrades!""".formatted(name);
        } else {
            displayName = "§f%s Upgrade: §cTier %s".formatted(name, tier + 2);
            description = """
                    §r§7Cost: §f%s %s
                    §eClick to buy!""".formatted(cost[tier].getAmount(), ShopItem.getFormattedName(cost[tier].getType()));
        }
        meta.setDisplayName(displayName);
        meta.setLore(List.of(description.split("\n")));
        ItemStack item = new ItemStack(type, amount);
        item.setItemMeta(meta);
        return item;
    }

    public boolean defaultBuyCheck(Player p) {
        int tier = RootWars.getPlayer(p).getTeam().getUpgrade(name);
        if (isMax(tier)) {
            p.sendMessage(ChatColor.RED + "You cannot buy anymore %s upgrades".formatted(name));
        } else if (p.getInventory().containsAtLeast(getCost(tier), getCost(tier).getAmount())) {
            for (Player player : RootWars.getPlayer(p).getTeam().getPlayersInTeam()) {
                player.sendMessage("You purchased %s upgrade!!!!!".formatted(name));
            }
            RootWars.getPlayer(p).getTeam().upgrade(name);
            p.getInventory().removeItem(getCost(tier));
            p.openInventory(RootWars.getCurrentGameMode().getShop().getUpgradeTab(p));
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
        return false;
    }
}
