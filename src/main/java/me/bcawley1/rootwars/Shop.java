package me.bcawley1.rootwars;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    public static Map<String, List<ShopItem>> shop = new HashMap<>();
    public Shop(){
        JSONParser jsonParser = new JSONParser();
        JSONObject JSONObj = null;
        try (FileReader reader = new FileReader(Bukkit.getServer().getPluginManager().getPlugin("RootWars").getDataFolder().getAbsolutePath() + "/shop.json")) {
            Object obj = jsonParser.parse(reader);
            JSONObj = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, ArrayList<Map<String, Object>>> JSONMap = new HashMap<>(JSONObj);
        for(Map.Entry<String, ArrayList<Map<String, Object>>> entry : JSONMap.entrySet()){
            //if quickbuy then do something cool
            List<ShopItem> list = new ArrayList<>();
            for(Map<String, Object> m : entry.getValue()){
                ShopItem item;
                System.out.println(m.get("buyAmount"));
                System.out.println(m.get("costAmount"));
                if(m.containsKey("action")){
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

    public static List<ShopItem> getShopTab(String tab) {
        return shop.get(tab);
    }
    public static Inventory getInventoryTab(Player p, String tab){
        Inventory inv = Bukkit.createInventory(p, 54, tab);
        for(ShopItem i : getShopTab(tab)){
            inv.setItem(getShopTab(tab).indexOf(i), i.getShopItem());
        }
        return inv;
    }
}
