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

    public Shop(Shop shop) {
        this.items = shop.items;
        this.tabs = shop.tabs;
        upgrades = new ArrayList<>();
        for (UpgradableItem upgrade : shop.upgrades) {
            upgrades.add(new UpgradableItem(upgrade));
        }
    }

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
//        tabs.add(new ShopTab("Blocks", Material.WHITE_WOOL, List.of(
//                new ShopItem(Material.BLUE_WOOL, 16, Material.IRON_INGOT, 4, "Wool", BuyActions.DEFAULT),
//                new ShopItem(Material.END_STONE, 12, Material.IRON_INGOT, 12, "End Stone", BuyActions.DEFAULT),
//                new ShopItem(Material.GLASS, 4, Material.GOLD_INGOT, 6, "Glass", BuyActions.DEFAULT))));
//
//        tabs.add(new ShopTab("Quick Buy", Material.NETHER_STAR, List.of(
//                new ShopItem(Material.FIRE_CHARGE, 1, Material.IRON_INGOT, 40, "Fire Ball", BuyActions.DEFAULT),
//                new ShopItem(Material.GOLDEN_APPLE, 1, Material.GOLD_INGOT, 3, "Golden Apple", BuyActions.DEFAULT),
//                new ShopItem(Material.STONE_SWORD, 1, Material.IRON_INGOT, 10, "Stone Sword", BuyActions.DEFAULT))));
//
//        upgrades.add(new UpgradableItem(Material.FURNACE, BuyActions.GENERATOR, "Generator", 4, new ItemStack[]{
//                new ItemStack(Material.DIAMOND, 2),
//                new ItemStack(Material.DIAMOND, 4),
//                new ItemStack(Material.DIAMOND, 6),
//                new ItemStack(Material.DIAMOND, 8)
//        }));
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
