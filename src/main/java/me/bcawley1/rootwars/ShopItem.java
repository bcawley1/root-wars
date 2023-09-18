package me.bcawley1.rootwars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItem {
    private Material buyItem;
    private int buyAmount;
    private Material costItem;
    private int costAmount;
    private String name;
    private int invSlot;
    private boolean giveListedItem;
    private static Map<ItemStack, ShopItem> ShopItems = new HashMap<>();

    public ShopItem(Material buyItem, int buyAmount, Material costItem, int costAmount, String name, int invSlot, boolean giveBuyItem) {
        this.giveListedItem = giveBuyItem;
        this.invSlot = invSlot;
        this.buyItem = buyItem;
        this.costItem = costItem;
        this.costAmount = costAmount;
        this.name = name;
        this.buyAmount = buyAmount;
        ShopItems.put(this.getShopItem(), this);
    }

    public int getInvSlot() {
        return invSlot;
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

    public boolean doesGiveListedItem() {
        return giveListedItem;
    }
    public static boolean hasShopItem(ItemStack item){
        return ShopItems.containsKey(item);
    }
}
