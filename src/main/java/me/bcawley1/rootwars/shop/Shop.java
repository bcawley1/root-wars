package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    @JsonIgnore
    private Map<String, ActionItem> items;
    @JsonProperty
    private List<ShopTab> tabs;
    @JsonProperty
    private List<UpgradableItem> upgrades;

    @JsonCreator
    private Shop(@JsonProperty("tabs") List<ShopTab> tabs, @JsonProperty("upgrades") List<UpgradableItem> upgrades) {
        this.tabs = tabs;
        this.upgrades = upgrades;
        items = new HashMap<>();

        for (ShopTab tab : tabs) {
            for (ShopItem item : tab.getItems()) {
                items.put(item.getName(), item);
            }
        }
        for (UpgradableItem item : upgrades) {
            items.put(item.getName(), item);
        }
    }

    public boolean containsTab(String tab) {
        for (ShopTab shopTab : tabs) {
            if(shopTab.getName().equals(tab)){
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public ActionItem getTopBarItem(ItemStack i) {
        for (ShopTab tab : tabs) {
            if(ChatColor.stripColor(i.getItemMeta().getDisplayName()).equals(tab.getName())){
                return tab.getTabItem();
            }
        }
        return null;
    }

    @JsonIgnore
    public boolean isTopBar(ItemStack i) {
        return getTopBarItem(i) != null;
    }

    @JsonIgnore
    public Inventory getUpgradeTab(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, "Upgrades");
        for (UpgradableItem i : upgrades) {
            int indexOf = upgrades.indexOf(i);
            inv.setItem(indexOf * 2 + 2, i.getItem(p));
        }
        return inv;
    }

    @JsonIgnore
    public ActionItem getActionItem(ItemStack item) {
        return items.get(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
    }

    @JsonIgnore
    public ActionItem getActionItemFromString(String s) {
        return items.get(s);
    }

    @JsonIgnore
    public List<ActionItem> getTabItems() {
        List<ActionItem> tabItems = new ArrayList<>();
        for (ShopTab tab : tabs) {
            tabItems.add(tab.getTabItem());
        }
        return tabItems;
    }

    public List<ShopTab> getTabs() {
        return tabs;
    }
}
