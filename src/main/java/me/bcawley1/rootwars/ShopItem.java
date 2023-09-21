package me.bcawley1.rootwars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ShopItem {
    private Material buyItem;
    private int buyAmount;
    private Material costItem;
    private int costAmount;
    private String name;
    private int invSlot;
    private static Map<ItemStack, ShopItem> ShopItems = new HashMap<>();
    private static PlayerCooldown buyCooldown = new PlayerCooldown();
    private BiConsumer<Player, ShopItem> onClick;

    public ShopItem(Material buyItem, int buyAmount, Material costItem, int costAmount, String name, BiConsumer<Player, ShopItem> onClick) {
        this.invSlot = invSlot;
        this.buyItem = buyItem;
        this.costItem = costItem;
        this.costAmount = costAmount;
        this.name = name;
        this.buyAmount = buyAmount;
        this.onClick = onClick;
        ShopItems.put(this.getShopItem(), this);
    }
    public ShopItem(Material buyItem, int buyAmount, Material costItem, int costAmount, String name) {
        this(buyItem, buyAmount, costItem, costAmount, name, (p, i) -> {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                if(buyCooldown.getCooldown(p.getUniqueId())==0) {
                    buyCooldown.setCooldown(p.getUniqueId(), 200);
                    p.getInventory().removeItem(i.getCostItem());
                    p.getInventory().addItem(i.getPurchasedItem());
                    p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getI18NDisplayName()));
                }
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        });
    }

    public static PlayerCooldown getBuyCooldown() {
        return buyCooldown;
    }

    public void onItemClick(Player p){
        onClick.accept(p, this);
    }

    public static ShopItem getShopItemFromItem(ItemStack item){
        return ShopItems.get(item);
    }


    public ItemStack getShopItem(){
        String description = "%s%sCost: %s%s %s\n%sClick to buy!!!!".formatted(ChatColor.RESET,ChatColor.GRAY, ChatColor.WHITE,costAmount, new ItemStack(costItem).getI18NDisplayName(), ChatColor.YELLOW);
        ItemStack item = new ItemStack(buyItem, buyAmount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        meta.setLore(List.of(description.split("\n")));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getPurchasedItem(){
        return new ItemStack(buyItem, buyAmount);
    }
    public ItemStack getCostItem(){
        return new ItemStack(costItem, costAmount);
    }

    public static boolean hasShopItem(ItemStack item){
        return ShopItems.containsKey(item);
    }
}
