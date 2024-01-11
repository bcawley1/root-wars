package me.bcawley1.rootwars.shop;

import me.bcawley1.rootwars.util.PlayerCooldown;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.BiConsumer;

public class ShopItem extends ActionItem {
    private Material costItem;
    private int costAmount;
    private static PlayerCooldown buyCooldown = new PlayerCooldown();

    public ShopItem(Material buyItem, int buyAmount, Material costItem, int costAmount, String name, BiConsumer<Player, ShopItem> action) {
        super(buyItem, buyAmount, action);
        String description = "%s%sCost: %s%s %s\n%sClick to buy!!!!".formatted(ChatColor.RESET, ChatColor.GRAY, ChatColor.WHITE, costAmount, ShopItem.getFormattedName(costItem), ChatColor.YELLOW);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        meta.setLore(List.of(description.split("\n")));
        setItemMeta(meta);

        this.costItem = costItem;
        this.costAmount = costAmount;
    }

    public static PlayerCooldown getBuyCooldown() {
        return buyCooldown;
    }

    public void onItemClick(Player p) {
        action.accept(p, this);
    }

    public ItemStack getPurchasedItem() {
        return new ItemStack(getType(), getAmount());
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
