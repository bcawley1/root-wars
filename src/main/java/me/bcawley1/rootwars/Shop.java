package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    private static Map<String, List<ShopItem>> shop = new HashMap<>();
    private static List<ItemStack> topBarItems = new ArrayList<>();
    private static List<BuyActions> topBarActions = new ArrayList<>();
    private static List<ShopItem> upgrades = new ArrayList<>();


    public Shop() {
        JSONParser jsonParser = new JSONParser();
        JSONObject JSONObj = null;
        try (FileReader reader = new FileReader(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/shop.json")) {
            Object obj = jsonParser.parse(reader);
            JSONObj = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, ArrayList<Map<String, Object>>> JSONMap = new HashMap<>(JSONObj);
        for (Map.Entry<String, ArrayList<Map<String, Object>>> entry : JSONMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Top Bar")) {
                for (Map<String, Object> m : entry.getValue()) {
                    ItemStack item = new ItemStack(Material.valueOf((String) m.get("material")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName((String) m.get("name"));
                    meta.setLore(List.of("%s%sClick to open the %s menu.".formatted(ChatColor.RESET, ChatColor.YELLOW, ((String) m.get("name")).toLowerCase())));
                    item.setItemMeta(meta);
                    topBarItems.add(item);
                    topBarActions.add(BuyActions.valueOf((String) m.get("action")));
                }
            } else {
                List<ShopItem> list = new ArrayList<>();
                for (Map<String, Object> m : entry.getValue()) {
                    ShopItem item;
                    if (m.containsKey("action")) {
                        item = new ShopItem(Material.valueOf((String) m.get("buyMaterial")), Math.toIntExact((Long) m.get("buyAmount")),
                                Material.valueOf((String) m.get("costMaterial")), Math.toIntExact((Long) m.get("costAmount")), (String) m.get("name"), BuyActions.valueOf((String) m.get("action")).getAction());
                    } else {
                        item = new ShopItem(Material.valueOf((String) m.get("buyMaterial")), Math.toIntExact((Long) m.get("buyAmount")),
                                Material.valueOf((String) m.get("costMaterial")), Math.toIntExact((Long) m.get("costAmount")), (String) m.get("name"));
                    }
                    list.add(item);
                }
                shop.put(entry.getKey(), list);
            }
        }
    }

    public static boolean containsTab(String tab) {
        return shop.containsKey(tab);
    }

    public static List<ShopItem> getShopTab(String tab) {
        return shop.get(tab);
    }

    public static List<ItemStack> getTopBarItems() {
        return topBarItems;
    }

    public static boolean isTopBar(ItemStack i) {
        return getTopBarItems().contains(i);
    }

    public static BuyActions getTopBarAction(ItemStack i) {
        return topBarActions.get(getTopBarItems().indexOf(i));
    }

    public static List<BuyActions> getTopBarActions() {
        return topBarActions;
    }

    public static Inventory getInventoryTab(Player p, String tab) {
        Inventory inv = Bukkit.createInventory(p, 54, tab);
        for (ItemStack i : getTopBarItems()) {
            inv.setItem(getTopBarItems().indexOf(i), i);
        }
        for (ShopItem i : getShopTab(tab)) {
            int indexOf = getShopTab(tab).indexOf(i);
            inv.setItem((indexOf % 7 + 1) + 9 * (2 + indexOf / 7), i.getShopItem());
        }
        return inv;
    }
    public static Inventory getUpgradeTab(Player p){
        Inventory inv = Bukkit.createInventory(p, 9, "Upgrades");
        for (ShopItem i : getShopTab("Upgrades")) {
            int indexOf = getShopTab("Upgrades").indexOf(i);
            inv.setItem(indexOf*2+2, i.getShopItem());
        }
        return inv;
    }
}
