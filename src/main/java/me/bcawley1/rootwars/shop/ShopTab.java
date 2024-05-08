package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ShopTab {
    @JsonProperty
    private String name;
    @JsonProperty
    private Material item;
    @JsonProperty
    List<ShopItem> items;

    private ShopTab() {}

    @JsonIgnore
    public ActionItem getTabItem() {
        TabItem tabItem = new TabItem(name, item);
        tabItem.setLore(List.of("§r§eClick to open the %s menu.".formatted(name)));
        return tabItem;
    }

    public Inventory getInventoryTab(Player p) {
        Inventory inv = Bukkit.createInventory(p, 54, name);
        List<ActionItem> tabItems = RootWars.getCurrentGameMode().getShop().getTabItems();
        for (int i = 0; i < tabItems.size(); i++) {
            inv.setItem(i, tabItems.get(i).getItem(p));
        }
        for (int i = 0; i < items.size(); i++) {
            inv.setItem((i % 7 + 1) + 9 * (2 + i / 7), items.get(i).getItem(p));
        }
        return inv;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public class TabItem extends ActionItem {
        public TabItem(String name, Material type) {
            super(name, type, null);
        }

        @Override
        public void onItemClick(Player p) {
            p.openInventory(getInventoryTab(p));
        }
    }
}
