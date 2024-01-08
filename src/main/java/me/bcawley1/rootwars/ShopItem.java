package me.bcawley1.rootwars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.BiConsumer;

public class ShopItem extends ItemStack {
    private Material buyItem;
    private int buyAmount;
    private Material costItem;
    private int costAmount;
    private String name;
    private static PlayerCooldown buyCooldown = new PlayerCooldown();
    private BiConsumer<Player, ShopItem> onClick;

    public ShopItem(Material buyItem, int buyAmount, Material costItem, int costAmount, String name, BiConsumer<Player, ShopItem> onClick) {
        super(buyItem, buyAmount);
        String description = "%s%sCost: %s%s %s\n%sClick to buy!!!!".formatted(ChatColor.RESET, ChatColor.GRAY, ChatColor.WHITE, costAmount, ShopItem.getFormattedName(costItem), ChatColor.YELLOW);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        meta.setLore(List.of(description.split("\n")));
        setItemMeta(meta);

        this.buyItem = buyItem;
        this.costItem = costItem;
        this.costAmount = costAmount;
        this.name = name;
        this.buyAmount = buyAmount;
        this.onClick = onClick;
    }

    public ShopItem(Material buyItem, int buyAmount, Material costItem, int costAmount, String name) {
        this(buyItem, buyAmount, costItem, costAmount, name, (p, i) -> {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                if (buyCooldown.getCooldown(p.getUniqueId()) == 0) {
                    buyCooldown.setCooldown(p.getUniqueId(), 200);
                    p.getInventory().removeItem(i.getCostItem());
                    p.getInventory().addItem(i.getPurchasedItem());
                    p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(i.getPurchasedItem().getType())));
                }
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        });
    }

    public ShopItem() {
    }

    public static PlayerCooldown getBuyCooldown() {
        return buyCooldown;
    }

    public void onItemClick(Player p) {
        onClick.accept(p, this);
    }

    public ItemStack getPurchasedItem() {
        return new ItemStack(buyItem, buyAmount);
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
