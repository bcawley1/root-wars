package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.bcawley1.rootwars.util.PlayerCooldown;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopItem extends ActionItem {
    private final Material costItem;
    private final int costAmount;
    @JsonIgnore
    private final PlayerCooldown buyCooldown;

    public ShopItem(Material type, int amount, Material costItem, int costAmount, String name, BuyActions action) {
        super(name, type, amount, action);
        this.buyCooldown = new PlayerCooldown(200);
        this.costItem = costItem;
        this.costAmount = costAmount;

        String description = "%s%sCost: %s%s %s\n%sClick to buy!!!!".formatted(ChatColor.RESET, ChatColor.GRAY, ChatColor.WHITE, costAmount, ShopItem.getFormattedName(costItem), ChatColor.YELLOW);
        meta.setDisplayName(ChatColor.WHITE + name);
        meta.setLore(List.of(description.split("\n")));
    }

    public boolean defaultBuyCheck(Player p) {
        if (buyCooldown.getCooldown(p.getUniqueId()) == 0 && p.getInventory().containsAtLeast(getCostItem(), costAmount)) {
            buyCooldown.setCooldown(p.getUniqueId());
            p.getInventory().removeItem(getCostItem());
            p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(name));
            return true;
        } else if (!p.getInventory().containsAtLeast(getCostItem(), costAmount)) {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
        return false;
    }

    public ItemStack getPurchasedItem() {
        return new ItemStack(type, amount);
    }

    public ItemStack getCostItem() {
        return new ItemStack(costItem, costAmount);
    }

    public static String getFormattedName(Material material) {
        StringBuilder builder = new StringBuilder();
        for (String word : material.toString().split("_")) {
            builder.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ");
        }
        return builder.toString();
    }
}
