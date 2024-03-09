package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<ShopTab> tabs;
    private List<UpgradableItem> upgrades;

    public Shop() {
        tabs = new ArrayList<>();
        items = new HashMap<>();
        upgrades = new ArrayList<>();
//        JSONParser jsonParser = new JSONParser();
//        JSONObject JSONObj = null;
//        try (FileReader reader = new FileReader(RootWars.getPlugin().getDataFolder().getAbsolutePath() + "/shop.json")) {
//            Object obj = jsonParser.parse(reader);
//            JSONObj = (JSONObject) obj;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Map<String, ArrayList<Map<String, Object>>> JSONMap = new HashMap<String, ArrayList<Map<String, Object>>>(JSONObj);
//        for (Map.Entry<String, ArrayList<Map<String, Object>>> entry : JSONMap.entrySet()) {
//            if (entry.getKey().equalsIgnoreCase("Top Bar")) {
//                for (Map<String, Object> m : entry.getValue()) {
//                    ActionItem item = new ActionItem(Material.valueOf((String) m.get("material")), BuyActions.valueOf((String) m.get("action")));
//                    ItemMeta meta = item.getItemMeta();
//                    meta.setDisplayName("%s%s%s".formatted(ChatColor.RESET, ChatColor.WHITE, m.get("name")));
//                    meta.setLore(List.of("%s%sClick to open the %s menu.".formatted(ChatColor.RESET, ChatColor.YELLOW, ((String) m.get("name")).toLowerCase())));
//                    item.setItemMeta(meta);
//                    topBar.add(item);
//                }
//            } else if (entry.getKey().equalsIgnoreCase("Upgrades")) {
//                for (Map<String, Object> m : entry.getValue()) {
//                    List<Long> costAmount = (List<Long>) m.get("costAmount");
//                    List<ItemStack> cost = new ArrayList<>();
//                    costAmount.forEach(i -> cost.add(new ItemStack(Material.valueOf((String) m.get("costMaterial")), Math.toIntExact(i))));
//                    UpgradableItem item = new UpgradableItem(Material.valueOf((String) m.get("buyMaterial")), BuyActions.valueOf((String) m.get("action")), (String) m.get("name"), cost.size()+1, cost);
//                    upgrades.add(item);
//                    items.put((String) m.get("name"), item);
//                }
//            } else {
//                List<ShopItem> list = new ArrayList<>();
//                for (Map<String, Object> m : entry.getValue()) {
//                    ShopItem item;
//                    item = new ShopItem(Material.valueOf((String) m.get("buyMaterial")), Math.toIntExact((Long) m.get("buyAmount")),
//                            Material.valueOf((String) m.get("costMaterial")), Math.toIntExact((Long) m.get("costAmount")), (String) m.get("name"), m.containsKey("action") ? BuyActions.valueOf((String) m.get("action")) : BuyActions.DEFAULT);
//                    list.add(item);
//                    items.put(item.getItemMeta().getDisplayName(), item);
//                }
//                shop.put(entry.getKey(), list);
//            }
//        }
    }

    public boolean containsTab(String tab) {
        for (ShopTab shopTab : tabs) {
            if(shopTab.getName().equals(tab)){
                return true;
            }
        }
        return false;
    }

    public ActionItem getTopBarItem(ItemStack i) {
        for (ShopTab tab : tabs) {
            if(ChatColor.stripColor(i.getItemMeta().getDisplayName()).equals(tab.getName())){
                return tab.getTabItem();
            }
        }
        return null;
    }

    public boolean isTopBar(ItemStack i) {
        return getTopBarItem(i) != null;
    }

    public Inventory getUpgradeTab(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, "Upgrades");
        for (UpgradableItem i : upgrades) {
            int indexOf = upgrades.indexOf(i);
            inv.setItem(indexOf * 2 + 2, i.getItem());
        }
        return inv;
    }

    public ActionItem getActionItem(ItemStack item) {
        return items.get(item.getItemMeta().getDisplayName());
    }
    public ActionItem getActionItemFromString(String s) {
        return items.get(s);
    }

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
