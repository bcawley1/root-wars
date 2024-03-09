package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.BiConsumer;

public class ActionItem {
    @JsonProperty
    protected final String name;
    @JsonProperty("material")
    protected Material type;
    @JsonProperty
    protected final int amount;
    protected final ItemMeta meta;
    @JsonProperty
    protected final BuyActions action;

    public ActionItem(String name, Material type, int amount, BuyActions action) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.action = action;
        this.meta = Bukkit.getItemFactory().getItemMeta(type);
        meta.setDisplayName("%s%s%s".formatted(ChatColor.RESET, ChatColor.WHITE, name));
    }

    public ActionItem(String name, Material type, BuyActions action) {
        this(name, type, 1, action);
    }

    public void onItemClick(Player p) {
        action.getAction().accept(p, this);
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public ItemStack getItem() {
        ItemStack item = new ItemStack(type, amount);
        item.setItemMeta(meta);
        return item;
    }

    public void setLore(List<String> lore) {
        meta.setLore(lore);
    }
}
