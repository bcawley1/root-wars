package me.bcawley1.rootwars.shop;

import me.bcawley1.rootwars.RootWars;
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
import java.util.*;

public class Shop {
    private Map<String, List<ShopItem>> shop;
    private Map<String, ActionItem> items;
    private final List<ActionItem> topBar;
    private List<UpgradableItem> upgrades;

    public Shop() {
        shop = new HashMap<>();
        topBar = new ArrayList<>();
        items = new HashMap<>();
        upgrades = new ArrayList<>();

        JSONParser jsonParser = new JSONParser();
        JSONObject JSONObj = null;
        try (FileReader reader = new FileReader(RootWars.getPlugin().getDataFolder().getAbsolutePath() + "/shop.json")) {
            Object obj = jsonParser.parse(reader);
            JSONObj = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, ArrayList<Map<String, Object>>> JSONMap = new HashMap<String, ArrayList<Map<String, Object>>>(JSONObj);
        for (Map.Entry<String, ArrayList<Map<String, Object>>> entry : JSONMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Top Bar")) {
                for (Map<String, Object> m : entry.getValue()) {
                    ActionItem item = new ActionItem(Material.valueOf((String) m.get("material")), BuyActions.valueOf((String) m.get("action")).getAction());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("%s%s%s".formatted(ChatColor.RESET, ChatColor.WHITE, m.get("name")));
                    meta.setLore(List.of("%s%sClick to open the %s menu.".formatted(ChatColor.RESET, ChatColor.YELLOW, ((String) m.get("name")).toLowerCase())));
                    item.setItemMeta(meta);
                    topBar.add(item);
                }
            } else if (entry.getKey().equalsIgnoreCase("Upgrades")) {
                for (Map<String, Object> m : entry.getValue()) {
                    List<Long> costAmount = (List<Long>) m.get("costAmount");
                    List<ItemStack> cost = new ArrayList<>();
                    costAmount.forEach(i -> cost.add(new ItemStack(Material.valueOf((String) m.get("costMaterial")), Math.toIntExact(i))));
                    UpgradableItem item = new UpgradableItem(Material.valueOf((String) m.get("buyMaterial")), BuyActions.valueOf((String) m.get("action")).getAction(), (String) m.get("name"), cost.size()+1, cost);
                    upgrades.add(item);
                    items.put((String) m.get("name"), item);
                }
            } else {
                List<ShopItem> list = new ArrayList<>();
                for (Map<String, Object> m : entry.getValue()) {
                    ShopItem item;
                    item = new ShopItem(Material.valueOf((String) m.get("buyMaterial")), Math.toIntExact((Long) m.get("buyAmount")),
                            Material.valueOf((String) m.get("costMaterial")), Math.toIntExact((Long) m.get("costAmount")), (String) m.get("name"), m.containsKey("action") ? BuyActions.valueOf((String) m.get("action")).getAction() : BuyActions.DEFAULT.getAction());
                    list.add(item);
                    items.put(item.getItemMeta().getDisplayName(), item);
                }
                shop.put(entry.getKey(), list);
            }
        }
    }

    public boolean containsTab(String tab) {
        return shop.containsKey(tab);
    }

    public List<ShopItem> getShopTab(String tab) {
        return shop.get(tab);
    }

    public ActionItem getTopBarItem(ItemStack i) {
        for (ActionItem actionItem : topBar) {
            if (actionItem.getItemMeta().getDisplayName().equals(i.getItemMeta().getDisplayName())) {
                return actionItem;
            }
        }
        return null;
    }

    public boolean isTopBar(ItemStack i) {
        return getTopBarItem(i) != null;
    }

    public Inventory getInventoryTab(Player p, String tab) {
        Inventory inv = Bukkit.createInventory(p, 54, tab);
        for (ActionItem i : topBar) {
            inv.setItem(topBar.indexOf(i), i);
        }
        for (ShopItem i : getShopTab(tab)) {
            int indexOf = getShopTab(tab).indexOf(i);
            inv.setItem((indexOf % 7 + 1) + 9 * (2 + indexOf / 7), i);
        }
        return inv;
    }

    public Inventory getUpgradeTab(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, "Upgrades");
        for (UpgradableItem i : upgrades) {
            int indexOf = upgrades.indexOf(i);
            inv.setItem(indexOf * 2 + 2, i);
        }
        return inv;
    }

    public ActionItem getActionItem(ItemStack item) {
        return items.get(item.getItemMeta().getDisplayName());
    }
    public ActionItem getActionItemFromString(String s){
        return items.get(s);
    }
}
